let currentSchedules = [];
let selectedSchedule = null;

document.addEventListener('DOMContentLoaded', async () => {
    await loadExamsDropdown();
});

async function loadExamsDropdown() {
    try {
        const exams = await API.get('/api/exams/all');
        const select = document.getElementById('examSelect');
        if (!select) return;
        select.innerHTML = '<option value="">-- Select Exam --</option>';
        exams.forEach(exam => {
            const className = exam.schoolClass ? exam.schoolClass.name : '';
            const opt = document.createElement('option');
            opt.value = exam.id;
            opt.textContent = `${exam.name} (${className})`;
            select.appendChild(opt);
        });
    } catch (e) {
        showToast('Error loading exams', 'error');
    }
}

async function loadExamSubjects() {
    const examId = document.getElementById('examSelect').value;
    const subjectSelect = document.getElementById('subjectSelect');
    const classNameInput = document.getElementById('className');
    const marksEntryArea = document.getElementById('marksEntryArea');
    const noSelectionState = document.getElementById('noSelectionState');

    if (!examId) {
        subjectSelect.innerHTML = '<option value="">-- Select Subject --</option>';
        subjectSelect.disabled = true;
        if (classNameInput) classNameInput.value = '';
        if (marksEntryArea) marksEntryArea.classList.add('hidden');
        if (noSelectionState) noSelectionState.style.display = 'block';
        selectedSchedule = null;
        return;
    }

    try {
        currentSchedules = await API.get(`/api/exam-schedules?examId=${examId}`);
        if (!currentSchedules.length) {
            showToast('No scheduled subjects found for this exam', 'info');
            subjectSelect.innerHTML = '<option value="">No Subjects Scheduled</option>';
            subjectSelect.disabled = true;
            return;
        }

        subjectSelect.disabled = false;
        subjectSelect.innerHTML = '<option value="">-- Select Subject --</option>';
        currentSchedules.forEach(sched => {
            const subjectName = sched.subject ? sched.subject.name : sched.subjectName;
            const opt = document.createElement('option');
            opt.value = sched.id;
            opt.textContent = subjectName;
            subjectSelect.appendChild(opt);
        });

        if (currentSchedules[0].exam && currentSchedules[0].exam.schoolClass) {
            classNameInput.value = currentSchedules[0].exam.schoolClass.name;
        }
    } catch (e) {
        showToast('Error loading subjects for exam', 'error');
    }
}

async function loadMarksEntry() {
    const scheduleId = document.getElementById('subjectSelect').value;
    const marksEntryArea = document.getElementById('marksEntryArea');
    const noSelectionState = document.getElementById('noSelectionState');
    const tbody = document.querySelector('#marksTable tbody');

    if (!scheduleId) {
        if (marksEntryArea) marksEntryArea.classList.add('hidden');
        if (noSelectionState) noSelectionState.style.display = 'block';
        selectedSchedule = null;
        return;
    }

    selectedSchedule = currentSchedules.find(s => s.id == scheduleId);
    if (!selectedSchedule) return;

    if (marksEntryArea) marksEntryArea.classList.remove('hidden');
    if (noSelectionState) noSelectionState.style.display = 'none';

    document.getElementById('lblMaxMarks').textContent = selectedSchedule.maxMarks || 0;
    document.getElementById('lblTheoryMax').textContent = selectedSchedule.theoryMaxMarks || 0;
    document.getElementById('lblPracticalMax').textContent = selectedSchedule.practicalMaxMarks || 0;
    document.getElementById('lblPassingMarks').textContent = selectedSchedule.passingMarks || 0;

    showLoading(tbody);

    try {
        const response = await API.get(`/api/marks/entry?examScheduleId=${scheduleId}`);
        const students = response.students || [];

        if (!students.length) {
            showEmpty(tbody, 'No active students found in this class/section');
            return;
        }

        const isLocked = students.every(s => s.status === 'LOCKED');
        const badge = document.getElementById('marksStatusBadge');
        const btnSaveDraft = document.getElementById('btnSaveDraft');
        const btnSubmitMarks = document.getElementById('btnSubmitMarks');

        if (badge) badge.innerHTML = statusBadge(isLocked ? 'LOCKED' : (students[0].status || 'DRAFT'));
        
        if (isLocked) {
            if (btnSaveDraft) btnSaveDraft.style.display = 'none';
            if (btnSubmitMarks) btnSubmitMarks.style.display = 'none';
            showToast('Marks for this subject are LOCKED and read-only.', 'info');
        } else {
            if (btnSaveDraft) btnSaveDraft.style.display = 'inline-block';
            if (btnSubmitMarks) btnSubmitMarks.style.display = 'inline-block';
        }

        renderMarksTable(students, isLocked);
    } catch (e) {
        showEmpty(tbody, 'Failed to load marks entry list');
    }
}

function renderMarksTable(students, isLocked) {
    const tbody = document.querySelector('#marksTable tbody');
    if (!tbody) return;

    const theoryMax = selectedSchedule.theoryMaxMarks || 0;
    const practicalMax = selectedSchedule.practicalMaxMarks || 0;

    tbody.innerHTML = students.map((s, idx) => {
        const disabled = (isLocked || s.absent) ? 'disabled' : '';
        const theoryVal = s.theoryMarks != null ? s.theoryMarks : '';
        const practicalVal = s.practicalMarks != null ? s.practicalMarks : '';
        const totalVal = s.absent ? 0 : (s.totalMarks != null ? s.totalMarks : 0);

        return `
        <tr data-student-id="${s.studentId}">
            <td><strong>${s.rollNumber || '-'}</strong></td>
            <td>${s.name || s.studentName}</td>
            <td class="text-center">
                <input type="number" class="form-control mark-input theory-mark text-center" 
                    style="width: 100px; margin: 0 auto;"
                    value="${theoryVal}" min="0" max="${theoryMax}" step="0.5"
                    oninput="calculateRowTotal(this)" ${disabled} ${theoryMax === 0 ? 'disabled placeholder="N/A"' : ''}>
            </td>
            <td class="text-center">
                <input type="number" class="form-control mark-input practical-mark text-center" 
                    style="width: 100px; margin: 0 auto;"
                    value="${practicalVal}" min="0" max="${practicalMax}" step="0.5"
                    oninput="calculateRowTotal(this)" ${disabled} ${practicalMax === 0 ? 'disabled placeholder="N/A"' : ''}>
            </td>
            <td class="text-center font-weight-bold total-mark">${totalVal}</td>
            <td class="text-center">
                <input type="checkbox" class="absent-cb" style="transform: scale(1.2);" onchange="toggleAbsent(this)" ${s.absent ? 'checked' : ''} ${isLocked ? 'disabled' : ''}>
            </td>
            <td>
                <input type="text" class="form-control remarks-input" value="${s.remarks || ''}" placeholder="Remarks" ${isLocked ? 'disabled' : ''}>
            </td>
        </tr>
    `}).join('');
}

function toggleAbsent(checkbox) {
    const row = checkbox.closest('tr');
    const theoryInput = row.querySelector('.theory-mark');
    const practicalInput = row.querySelector('.practical-mark');
    const totalCell = row.querySelector('.total-mark');

    if (checkbox.checked) {
        if (theoryInput) { theoryInput.value = '0'; theoryInput.disabled = true; }
        if (practicalInput) { practicalInput.value = '0'; practicalInput.disabled = true; }
        if (totalCell) totalCell.textContent = '0';
    } else {
        if (theoryInput && (selectedSchedule.theoryMaxMarks || 0) > 0) theoryInput.disabled = false;
        if (practicalInput && (selectedSchedule.practicalMaxMarks || 0) > 0) practicalInput.disabled = false;
        calculateRowTotal(row);
    }
}

function calculateRowTotal(element) {
    const row = element.closest ? element.closest('tr') : element;
    const theoryInput = row.querySelector('.theory-mark');
    const practicalInput = row.querySelector('.practical-mark');
    const totalCell = row.querySelector('.total-mark');
    const absentCb = row.querySelector('.absent-cb');

    if (absentCb && absentCb.checked) {
        totalCell.textContent = '0';
        return;
    }

    const theoryMax = selectedSchedule.theoryMaxMarks || 0;
    const practicalMax = selectedSchedule.practicalMaxMarks || 0;
    const maxMarks = selectedSchedule.maxMarks || 100;

    let tVal = theoryInput && theoryInput.value !== '' ? parseFloat(theoryInput.value) : 0;
    let pVal = practicalInput && practicalInput.value !== '' ? parseFloat(practicalInput.value) : 0;

    if (tVal < 0) { tVal = 0; if (theoryInput) theoryInput.value = 0; }
    if (pVal < 0) { pVal = 0; if (practicalInput) practicalInput.value = 0; }

    if (theoryMax > 0 && tVal > theoryMax) {
        tVal = theoryMax;
        if (theoryInput) theoryInput.value = theoryMax;
        showToast(`Theory marks cannot exceed ${theoryMax}`, 'warning');
    }

    if (practicalMax > 0 && pVal > practicalMax) {
        pVal = practicalMax;
        if (practicalInput) practicalInput.value = practicalMax;
        showToast(`Practical marks cannot exceed ${practicalMax}`, 'warning');
    }

    let total = tVal + pVal;
    if (total > maxMarks) {
        total = maxMarks;
        showToast(`Total marks cannot exceed ${maxMarks}`, 'warning');
    }

    if (totalCell) totalCell.textContent = total;
}

function collectMarksData() {
    const rows = document.querySelectorAll('#marksTable tbody tr');
    const marks = [];

    rows.forEach(row => {
        const studentId = parseInt(row.getAttribute('data-student-id'));
        const absent = row.querySelector('.absent-cb').checked;
        const theoryInput = row.querySelector('.theory-mark');
        const practicalInput = row.querySelector('.practical-mark');
        const remarksInput = row.querySelector('.remarks-input');

        let theoryMarks = (theoryInput && theoryInput.value !== '') ? parseFloat(theoryInput.value) : 0.0;
        let practicalMarks = (practicalInput && practicalInput.value !== '') ? parseFloat(practicalInput.value) : 0.0;

        if (absent) {
            theoryMarks = 0.0;
            practicalMarks = 0.0;
        }

        marks.push({
            studentId: studentId,
            examScheduleId: selectedSchedule.id,
            theoryMarks: theoryMarks,
            practicalMarks: practicalMarks,
            absent: absent,
            remarks: remarksInput ? remarksInput.value : ''
        });
    });

    return {
        examScheduleId: selectedSchedule.id,
        marks: marks
    };
}

async function saveMarks(targetStatus = 'DRAFT') {
    if (!selectedSchedule) return;
    const payload = collectMarksData();

    try {
        await API.post('/api/marks/save', payload);
        showToast('Marks saved successfully');
        loadMarksEntry();
    } catch (e) {
        showToast(e.message || 'Failed to save marks', 'error');
    }
}

async function submitMarks() {
    if (!selectedSchedule) return;
    if (await showConfirm('Submit & Lock Marks', 'Are you sure you want to submit and lock marks for this subject? Once submitted, marks cannot be edited further.')) {
        const payload = collectMarksData();
        try {
            await API.post('/api/marks/save', payload);
            await API.post(`/api/marks/submit?examScheduleId=${selectedSchedule.id}`, {});
            showToast('Marks submitted and locked successfully');
            loadMarksEntry();
        } catch (e) {
            showToast(e.message || 'Failed to submit marks', 'error');
        }
    }
}
