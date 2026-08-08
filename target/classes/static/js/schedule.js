let examsList = [];
let currentExam = null;

document.addEventListener('DOMContentLoaded', async () => {
    await loadExamsDropdown();
    
    const subjectSelect = document.getElementById('subjectSelect');
    if (subjectSelect) {
        subjectSelect.addEventListener('change', handleSubjectChange);
    }

    const scheduleForm = document.getElementById('scheduleForm');
    if (scheduleForm) {
        scheduleForm.addEventListener('submit', saveSchedule);
    }
});

async function loadExamsDropdown() {
    try {
        const exams = await API.get('/api/exams/all');
        examsList = exams || [];
        const select = document.getElementById('examSelect');
        if (!select) return;
        select.innerHTML = '<option value="">-- Select an Exam --</option>';
        examsList.forEach(exam => {
            const className = exam.schoolClass ? exam.schoolClass.name : '';
            const sectionName = exam.section ? exam.section.name : '';
            const label = sectionName ? `${exam.name} (${className} - ${sectionName})` : `${exam.name} (${className})`;
            
            const opt = document.createElement('option');
            opt.value = exam.id;
            opt.textContent = label;
            select.appendChild(opt);
        });
    } catch (e) {
        showToast('Error loading exams', 'error');
    }
}

async function loadSubjectsDropdown(classId) {
    try {
        let url = '/api/subjects';
        if (classId) url += `?classId=${classId}`;
        let subjects = await API.get(url);
        
        if (!subjects || !subjects.length) {
            subjects = await API.get('/api/subjects');
        }

        const select = document.getElementById('subjectSelect');
        if (!select) return;
        select.innerHTML = '<option value="">Select Subject</option>';
        if (subjects && subjects.length) {
            subjects.forEach(sub => {
                const opt = document.createElement('option');
                opt.value = sub.id;
                opt.textContent = `${sub.name} (${sub.code || ''})`;
                opt.setAttribute('data-type', sub.assessmentType || '');
                select.appendChild(opt);
            });
        }
    } catch (e) {
        console.error('Failed to load subjects:', e);
    }
}

async function loadSchedules() {
    const examSelect = document.getElementById('examSelect');
    const examId = examSelect ? examSelect.value : null;
    const actionBar = document.getElementById('scheduleActionBar');
    const scheduleCard = document.getElementById('scheduleCard');
    const noExamSelected = document.getElementById('noExamSelected');
    const tbody = document.querySelector('#schedulesTable tbody');

    if (!examId) {
        if (actionBar) actionBar.classList.add('hidden');
        if (scheduleCard) scheduleCard.classList.add('hidden');
        if (noExamSelected) noExamSelected.style.display = 'block';
        currentExam = null;
        return;
    }

    currentExam = examsList.find(e => e.id == examId) || null;

    if (actionBar) actionBar.classList.remove('hidden');
    if (scheduleCard) scheduleCard.classList.remove('hidden');
    if (noExamSelected) noExamSelected.style.display = 'none';

    const currentExamName = document.getElementById('currentExamName');
    if (currentExamName && currentExam) {
        currentExamName.textContent = currentExam.name;
    }

    const classId = currentExam && currentExam.schoolClass ? currentExam.schoolClass.id : null;
    await loadSubjectsDropdown(classId);

    showLoading(tbody);

    try {
        const schedules = await API.get(`/api/exam-schedules?examId=${examId}`);
        renderSchedules(schedules);
    } catch (e) {
        showEmpty(tbody, 'Failed to load schedules');
    }
}

function renderSchedules(schedules) {
    const tbody = document.querySelector('#schedulesTable tbody');
    if (!tbody) return;
    if (!schedules || !schedules.length) {
        showEmpty(tbody, 'No schedules found for this exam. Click "Add Schedule" to create one.');
        return;
    }

    tbody.innerHTML = schedules.map(s => {
        const subjectName = s.subject ? s.subject.name : (s.subjectName || '-');
        const theoryMax = s.theoryMaxMarks != null ? s.theoryMaxMarks : 0;
        const practicalMax = s.practicalMaxMarks != null ? s.practicalMaxMarks : 0;

        return `
        <tr>
            <td><strong>${subjectName}</strong></td>
            <td>${formatDate(s.examDate)}</td>
            <td>${formatTime(s.startTime)} - ${formatTime(s.endTime)}</td>
            <td><strong>${s.maxMarks}</strong></td>
            <td><span class="text-danger font-weight-500">${s.passingMarks}</span></td>
            <td>T: ${theoryMax} | P: ${practicalMax}</td>
            <td>${s.room || '-'}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick='editSchedule(${JSON.stringify(s).replace(/'/g, "&apos;")})'>Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteSchedule(${s.id})">Delete</button>
            </td>
        </tr>
    `}).join('');
}

function handleSubjectChange(e) {
    const selectedOption = e.target.options[e.target.selectedIndex];
    if (!selectedOption || !selectedOption.value) return;
    
    const type = selectedOption.getAttribute('data-type');
    const theoryInput = document.getElementById('theoryMaxMarks');
    const practicalInput = document.getElementById('practicalMaxMarks');
    
    if (type === 'THEORY_ONLY') {
        practicalInput.value = 0;
        practicalInput.readOnly = true;
        theoryInput.readOnly = false;
    } else if (type === 'PRACTICAL_ONLY') {
        theoryInput.value = 0;
        theoryInput.readOnly = true;
        practicalInput.readOnly = false;
    } else {
        theoryInput.readOnly = false;
        practicalInput.readOnly = false;
    }
}

async function openScheduleModal() {
    if (!currentExam) {
        showToast('Please select an exam first', 'warning');
        return;
    }

    const classId = currentExam.schoolClass ? currentExam.schoolClass.id : null;
    const subjectSelect = document.getElementById('subjectSelect');
    if (subjectSelect && subjectSelect.options.length <= 1) {
        await loadSubjectsDropdown(classId);
    }

    const modal = document.getElementById('scheduleModal');
    if (modal) {
        modal.style.display = 'flex';
        modal.classList.add('active');
    }
    document.getElementById('scheduleModalTitle').textContent = 'Add Schedule';
    document.getElementById('scheduleForm').reset();
    document.getElementById('scheduleId').value = '';
    
    if (currentExam.startDate) {
        document.getElementById('examDate').min = currentExam.startDate;
    }
    if (currentExam.endDate) {
        document.getElementById('examDate').max = currentExam.endDate;
    }
}

function closeScheduleModal() {
    const modal = document.getElementById('scheduleModal');
    if (modal) {
        modal.classList.remove('active');
        setTimeout(() => { modal.style.display = 'none'; }, 200);
    }
}

async function editSchedule(schedule) {
    await openScheduleModal();
    document.getElementById('scheduleModalTitle').textContent = 'Edit Schedule';
    document.getElementById('scheduleId').value = schedule.id;
    
    const targetSubjectId = schedule.subject ? schedule.subject.id : schedule.subjectId;
    if (targetSubjectId) {
        document.getElementById('subjectSelect').value = targetSubjectId;
    }
    document.getElementById('examDate').value = schedule.examDate || '';
    document.getElementById('startTime').value = schedule.startTime || '';
    document.getElementById('endTime').value = schedule.endTime || '';
    document.getElementById('maxMarks').value = schedule.maxMarks || 100;
    document.getElementById('passingMarks').value = schedule.passingMarks || 35;
    document.getElementById('theoryMaxMarks').value = schedule.theoryMaxMarks != null ? schedule.theoryMaxMarks : 0;
    document.getElementById('practicalMaxMarks').value = schedule.practicalMaxMarks != null ? schedule.practicalMaxMarks : 0;
    document.getElementById('room').value = schedule.room || '';
    document.getElementById('instructions').value = schedule.instructions || '';
}

async function saveSchedule(e) {
    if (e) e.preventDefault();
    if (!currentExam) {
        showToast('No exam selected', 'error');
        return;
    }
    
    const subjectId = document.getElementById('subjectSelect').value;
    const examDate = document.getElementById('examDate').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const maxMarks = parseFloat(document.getElementById('maxMarks').value);
    const passingMarks = parseFloat(document.getElementById('passingMarks').value);
    const theoryMaxMarks = parseFloat(document.getElementById('theoryMaxMarks').value) || 0;
    const practicalMaxMarks = parseFloat(document.getElementById('practicalMaxMarks').value) || 0;
    const room = document.getElementById('room').value;
    const instructions = document.getElementById('instructions').value;
    
    if (!subjectId) {
        showToast('Please select a subject', 'error');
        return;
    }
    if (maxMarks <= 0) {
        showToast('Maximum marks must be greater than 0', 'error');
        return;
    }
    if (passingMarks > maxMarks) {
        showToast('Passing marks cannot be greater than max marks', 'error');
        return;
    }
    if ((theoryMaxMarks > 0 || practicalMaxMarks > 0) && (theoryMaxMarks + practicalMaxMarks !== maxMarks)) {
        showToast('Theory + Practical max marks must equal Max Marks', 'error');
        return;
    }
    if (startTime && endTime && endTime <= startTime) {
        showToast('End time must be after start time', 'error');
        return;
    }

    const data = {
        examId: currentExam.id,
        subjectId: parseInt(subjectId),
        examDate: examDate,
        startTime: startTime ? startTime + (startTime.length === 5 ? ':00' : '') : null,
        endTime: endTime ? endTime + (endTime.length === 5 ? ':00' : '') : null,
        maxMarks: maxMarks,
        passingMarks: passingMarks,
        theoryMaxMarks: theoryMaxMarks,
        practicalMaxMarks: practicalMaxMarks,
        room: room,
        instructions: instructions
    };
    
    const id = document.getElementById('scheduleId').value;
    try {
        if (id) {
            await API.put(`/api/exam-schedules/${id}`, data);
            showToast('Schedule updated successfully');
        } else {
            await API.post('/api/exam-schedules', data);
            showToast('Schedule created successfully');
        }
        closeScheduleModal();
        loadSchedules();
    } catch (err) {
        showToast(err.message || 'Failed to save schedule', 'error');
    }
}

async function deleteSchedule(id) {
    if (await showConfirm('Delete Schedule', 'Are you sure you want to delete this schedule?')) {
        try {
            await API.delete(`/api/exam-schedules/${id}`);
            showToast('Schedule deleted successfully');
            loadSchedules();
        } catch (err) {
            showToast(err.message || 'Failed to delete schedule', 'error');
        }
    }
}
