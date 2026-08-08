let currentPage = 0;

document.addEventListener('DOMContentLoaded', async () => {
    await loadFilterOptions();
    await loadExams();

    const searchInput = document.getElementById('filterSearch');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(() => {
            loadExams(0);
        }, 300));
    }
    
    const filterStatus = document.getElementById('filterStatus');
    if (filterStatus) filterStatus.addEventListener('change', () => loadExams(0));

    const filterType = document.getElementById('filterType');
    if (filterType) filterType.addEventListener('change', () => loadExams(0));

    const filterClass = document.getElementById('filterClass');
    if (filterClass) filterClass.addEventListener('change', () => loadExams(0));

    const examForm = document.getElementById('examForm');
    if (examForm) {
        examForm.addEventListener('submit', saveExam);
    }
});

function debounce(func, timeout = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => { func.apply(this, args); }, timeout);
    };
}

async function loadFilterOptions() {
    try {
        const classes = await API.get('/api/classes');
        const academicYears = await API.get('/api/academic-years');
        
        populateSelect('filterClass', classes, 'id', 'name', 'All Classes');
        populateSelect('examClass', classes, 'id', 'name', 'Select Class');
        populateSelect('academicYear', academicYears, 'id', 'name', 'Select Academic Year');
    } catch (e) {
        showToast('Error loading filter options', 'error');
    }
}

async function loadSectionsForModal() {
    const classId = document.getElementById('examClass').value;
    if (classId) {
        try {
            const sections = await API.get(`/api/sections?classId=${classId}`);
            populateSelect('examSection', sections, 'id', 'name', 'All Sections');
        } catch (e) {
            populateSelect('examSection', [], 'id', 'name', 'All Sections');
        }
    } else {
        populateSelect('examSection', [], 'id', 'name', 'All Sections');
    }
}

async function loadExams(page = 0) {
    currentPage = page;
    const tbody = document.querySelector('#examsTable tbody');
    if (!tbody) return;
    showLoading(tbody);

    const search = document.getElementById('filterSearch') ? document.getElementById('filterSearch').value.trim() : '';
    const status = document.getElementById('filterStatus') ? document.getElementById('filterStatus').value : '';
    const examType = document.getElementById('filterType') ? document.getElementById('filterType').value : '';
    const classId = document.getElementById('filterClass') ? document.getElementById('filterClass').value : '';
    
    try {
        let queryParams = `?page=${page}&size=10`;
        if (search) queryParams += `&search=${encodeURIComponent(search)}`;
        if (status) queryParams += `&status=${status}`;
        if (examType) queryParams += `&examType=${examType}`;
        if (classId) queryParams += `&classId=${classId}`;
        
        const response = await API.get(`/api/exams${queryParams}`);
        renderExams(response.content || response);
        renderPagination(response.totalPages || 1, page);
    } catch (e) {
        showEmpty(tbody, 'Failed to load exams');
    }
}

function renderExams(exams) {
    const tbody = document.querySelector('#examsTable tbody');
    if (!tbody) return;
    if (!exams || !exams.length) {
        showEmpty(tbody, 'No exams found');
        return;
    }
    
    tbody.innerHTML = exams.map(exam => {
        const className = exam.schoolClass ? exam.schoolClass.name : '-';
        const sectionName = exam.section ? exam.section.name : '';
        const classFull = sectionName ? `${className} (${sectionName})` : className;
        const examTypeStr = exam.examType || '-';
        
        return `
        <tr>
            <td><strong>${exam.name}</strong></td>
            <td><span class="badge badge-blue">${examTypeStr}</span></td>
            <td>${classFull}</td>
            <td>${exam.term || '-'}</td>
            <td>${formatDate(exam.startDate)} to ${formatDate(exam.endDate)}</td>
            <td>${statusBadge(exam.status)}</td>
            <td>
                <div class="flex gap-1 align-center">
                    <button class="btn btn-sm btn-primary" onclick='editExam(${JSON.stringify(exam).replace(/'/g, "&apos;")})'>Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteExam(${exam.id})">Delete</button>
                    <select class="form-control form-control-sm" style="width: auto; padding: 2px 5px;" onchange="changeStatus(${exam.id}, this.value)">
                        <option value="">Status...</option>
                        <option value="DRAFT" ${exam.status === 'DRAFT' ? 'selected' : ''}>Draft</option>
                        <option value="SCHEDULED" ${exam.status === 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
                        <option value="ONGOING" ${exam.status === 'ONGOING' ? 'selected' : ''}>Ongoing</option>
                        <option value="COMPLETED" ${exam.status === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    </select>
                </div>
            </td>
        </tr>
    `}).join('');
}

function renderPagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    let html = '';
    for (let i = 0; i < totalPages; i++) {
        html += `<button class="btn btn-sm ${i === currentPage ? 'btn-primary' : 'btn-secondary'}" onclick="loadExams(${i})">${i + 1}</button> `;
    }
    pagination.innerHTML = html;
}

function openExamModal() {
    const modal = document.getElementById('examModal');
    if (modal) {
        modal.style.display = 'flex';
        modal.classList.add('active');
    }
    document.getElementById('examModalTitle').textContent = 'Create Exam';
    document.getElementById('examForm').reset();
    document.getElementById('examId').value = '';
    document.getElementById('examStatus').disabled = true;
    document.getElementById('examStatus').value = 'DRAFT';
}

function closeExamModal() {
    const modal = document.getElementById('examModal');
    if (modal) {
        modal.classList.remove('active');
        setTimeout(() => { modal.style.display = 'none'; }, 200);
    }
}

async function editExam(exam) {
    openExamModal();
    document.getElementById('examModalTitle').textContent = 'Edit Exam';
    document.getElementById('examId').value = exam.id;
    document.getElementById('examName').value = exam.name || '';
    document.getElementById('examType').value = exam.examType || '';
    document.getElementById('examTerm').value = exam.term || '';
    document.getElementById('startDate').value = exam.startDate || '';
    document.getElementById('endDate').value = exam.endDate || '';
    document.getElementById('examDescription').value = exam.description || '';
    
    if (exam.academicYear) {
        document.getElementById('academicYear').value = exam.academicYear.id;
    } else if (exam.academicYearId) {
        document.getElementById('academicYear').value = exam.academicYearId;
    }
    
    const classId = exam.schoolClass ? exam.schoolClass.id : exam.classId;
    if (classId) {
        document.getElementById('examClass').value = classId;
        await loadSectionsForModal();
        const sectionId = exam.section ? exam.section.id : exam.sectionId;
        if (sectionId) {
            document.getElementById('examSection').value = sectionId;
        }
    }
    
    const statusSelect = document.getElementById('examStatus');
    statusSelect.disabled = false;
    statusSelect.value = exam.status || 'DRAFT';
}

async function saveExam(e) {
    if (e) e.preventDefault();
    const id = document.getElementById('examId').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    if (startDate && endDate && new Date(endDate) < new Date(startDate)) {
        showToast('End date must be after or equal to start date', 'error');
        return;
    }

    const name = document.getElementById('examName').value;
    const examType = document.getElementById('examType').value;
    const academicYearId = document.getElementById('academicYear').value;
    const classId = document.getElementById('examClass').value;

    if (!name || !examType || !academicYearId || !classId) {
        showToast('Please fill in all required fields (Name, Type, Academic Year, Class)', 'error');
        return;
    }
    
    const data = {
        name: name,
        examType: examType,
        term: document.getElementById('examTerm').value,
        academicYearId: parseInt(academicYearId),
        classId: parseInt(classId),
        sectionId: document.getElementById('examSection').value ? parseInt(document.getElementById('examSection').value) : null,
        startDate: startDate || null,
        endDate: endDate || null,
        description: document.getElementById('examDescription').value,
        status: document.getElementById('examStatus').value || 'DRAFT'
    };
    
    try {
        if (id) {
            await API.put(`/api/exams/${id}`, data);
            showToast('Exam updated successfully');
        } else {
            await API.post('/api/exams', data);
            showToast('Exam created successfully');
        }
        closeExamModal();
        loadExams(currentPage);
    } catch (err) {
        showToast(err.message || 'Failed to save exam', 'error');
    }
}

async function deleteExam(id) {
    if (await showConfirm('Delete Exam', 'Are you sure you want to delete this exam? This will remove associated schedules and marks.')) {
        try {
            await API.delete(`/api/exams/${id}`);
            showToast('Exam deleted successfully');
            loadExams(currentPage);
        } catch (err) {
            showToast(err.message || 'Failed to delete exam', 'error');
        }
    }
}

async function changeStatus(id, status) {
    if (!status) return;
    try {
        await API.patch(`/api/exams/${id}/status?status=${status}`);
        showToast(`Exam status updated to ${status}`);
        loadExams(currentPage);
    } catch (err) {
        showToast(err.message || 'Failed to update status', 'error');
    }
}
