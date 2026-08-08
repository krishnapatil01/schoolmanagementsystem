let activeTab = 'summary';

document.addEventListener('DOMContentLoaded', async () => {
    await loadExamsDropdown();
    await loadClassesDropdown();
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

async function loadClassesDropdown() {
    try {
        const classes = await API.get('/api/classes');
        populateSelect('classSelect', classes, 'id', 'name', 'All Classes');
    } catch (e) {
        console.error('Error loading classes:', e);
    }
}

async function loadSectionsAndSubjects() {
    const classId = document.getElementById('classSelect').value;
    const sectionSelect = document.getElementById('sectionSelect');
    const subjectSelect = document.getElementById('subjectSelect');

    if (classId) {
        try {
            const sections = await API.get(`/api/sections?classId=${classId}`);
            populateSelect('sectionSelect', sections, 'id', 'name', 'All Sections');
        } catch (e) {
            populateSelect('sectionSelect', [], 'id', 'name', 'All Sections');
        }
        try {
            const subjects = await API.get(`/api/subjects?classId=${classId}`);
            populateSelect('subjectSelect', subjects, 'id', 'name', 'All Subjects');
        } catch (e) {
            populateSelect('subjectSelect', [], 'id', 'name', 'All Subjects');
        }
    } else {
        populateSelect('sectionSelect', [], 'id', 'name', 'All Sections');
        populateSelect('subjectSelect', [], 'id', 'name', 'All Subjects');
    }
}

function switchTab(tabName) {
    activeTab = tabName;
    
    document.querySelectorAll('.tabs .tab').forEach(tab => {
        tab.classList.remove('active');
    });
    const selectedTabEl = document.getElementById(`tab-${tabName}`);
    if (selectedTabEl) selectedTabEl.classList.add('active');

    const classFilter = document.querySelector('.class-filter');
    const sectionFilter = document.querySelector('.section-filter');
    const subjectFilter = document.querySelector('.subject-filter');

    if (classFilter) classFilter.classList.add('hidden');
    if (sectionFilter) sectionFilter.classList.add('hidden');
    if (subjectFilter) subjectFilter.classList.add('hidden');

    if (tabName === 'class-wise' || tabName === 'toppers' || tabName === 'pass-fail') {
        if (classFilter) classFilter.classList.remove('hidden');
        if (sectionFilter) sectionFilter.classList.remove('hidden');
    } else if (tabName === 'subject-wise') {
        if (classFilter) classFilter.classList.remove('hidden');
        if (subjectFilter) subjectFilter.classList.remove('hidden');
    }

    loadData();
}

async function loadFiltersAndData() {
    await loadSectionsAndSubjects();
    await loadData();
}

async function loadData() {
    const examId = document.getElementById('examSelect').value;
    const classId = document.getElementById('classSelect').value;
    const sectionId = document.getElementById('sectionSelect').value;
    const subjectId = document.getElementById('subjectSelect').value;

    const reportTitle = document.getElementById('reportTitle');
    const currentReportTitle = document.getElementById('currentReportTitle');
    const reportTable = document.getElementById('reportTable');
    const tableHead = document.getElementById('reportTableHead');
    const tableBody = document.getElementById('reportTableBody');
    const noDataMessage = document.getElementById('noDataMessage');

    if (!examId) {
        if (reportTitle) reportTitle.classList.add('hidden');
        if (reportTable) reportTable.classList.add('hidden');
        if (noDataMessage) noDataMessage.style.display = 'block';
        return;
    }

    if (reportTitle) reportTitle.classList.remove('hidden');
    if (noDataMessage) noDataMessage.style.display = 'none';

    showLoading(tableBody);
    if (reportTable) reportTable.classList.remove('hidden');

    try {
        if (activeTab === 'summary') {
            currentReportTitle.textContent = 'Exam Performance Summary';
            const data = await API.get(`/api/reports/summary?examId=${examId}`);
            renderSummaryReport(data, tableHead, tableBody);
        } else if (activeTab === 'class-wise') {
            currentReportTitle.textContent = 'Class-wise Performance Report';
            let url = `/api/reports/class-wise?examId=${examId}`;
            if (classId) url += `&classId=${classId}`;
            if (sectionId) url += `&sectionId=${sectionId}`;
            const data = await API.get(url);
            renderClassWiseReport(data, tableHead, tableBody);
        } else if (activeTab === 'subject-wise') {
            currentReportTitle.textContent = 'Subject-wise Performance Report';
            if (!subjectId) {
                showEmpty(tableBody, 'Please select a class and a subject to view the subject report.');
                tableHead.innerHTML = '';
                return;
            }
            const data = await API.get(`/api/reports/subject-wise?examId=${examId}&subjectId=${subjectId}`);
            renderSubjectWiseReport(data, tableHead, tableBody);
        } else if (activeTab === 'pass-fail') {
            currentReportTitle.textContent = 'Pass / Fail Statistics Report';
            let url = `/api/reports/pass-fail?examId=${examId}`;
            if (classId) url += `&classId=${classId}`;
            const data = await API.get(url);
            renderPassFailReport(data, tableHead, tableBody);
        } else if (activeTab === 'toppers') {
            currentReportTitle.textContent = 'Class Toppers List';
            let url = `/api/reports/topper-list?examId=${examId}&limit=10`;
            const data = await API.get(url);
            renderToppersReport(data, tableHead, tableBody);
        } else if (activeTab === 'absent') {
            currentReportTitle.textContent = 'Absentees Report';
            const data = await API.get(`/api/reports/absent?examId=${examId}`);
            renderAbsentReport(data, tableHead, tableBody);
        }
    } catch (e) {
        showEmpty(tableBody, 'Failed to generate report data');
    }
}

function renderSummaryReport(data, thead, tbody) {
    thead.innerHTML = `
        <tr>
            <th>Metric</th>
            <th>Value</th>
        </tr>
    `;
    tbody.innerHTML = `
        <tr><td>Total Students Evaluated</td><td><strong>${data.totalStudents || 0}</strong></td></tr>
        <tr><td>Students Passed</td><td><span class="badge badge-success">${data.passedStudents || 0}</span></td></tr>
        <tr><td>Students Failed</td><td><span class="badge badge-danger">${data.failedStudents || 0}</span></td></tr>
        <tr><td>Overall Pass Percentage</td><td><strong class="text-primary">${(data.passPercentage || 0).toFixed(2)}%</strong></td></tr>
    `;
}

function renderClassWiseReport(data, thead, tbody) {
    if (!data || !data.length) {
        showEmpty(tbody, 'No class result data found for this exam.');
        thead.innerHTML = '';
        return;
    }
    thead.innerHTML = `
        <tr>
            <th>Roll No</th>
            <th>Student Name</th>
            <th>Grand Total</th>
            <th>Percentage</th>
            <th>Grade</th>
            <th>Status</th>
            <th>Rank</th>
        </tr>
    `;
    tbody.innerHTML = data.map(d => `
        <tr>
            <td><strong>${d.studentRollNumber || '-'}</strong></td>
            <td>${d.studentName || '-'}</td>
            <td>${d.totalMarksObtained} / ${d.totalMaxMarks}</td>
            <td><strong>${d.percentage ? d.percentage.toFixed(2) + '%' : '0%'}</strong></td>
            <td><span class="badge badge-purple">${d.grade || '-'}</span></td>
            <td>${statusBadge(d.resultStatus || (d.passed ? 'PASS' : 'FAIL'))}</td>
            <td>${d.rank ? '#' + d.rank : '-'}</td>
        </tr>
    `).join('');
}

function renderSubjectWiseReport(data, thead, tbody) {
    if (!data || !data.length) {
        showEmpty(tbody, 'No subject marks recorded for this subject.');
        thead.innerHTML = '';
        return;
    }
    thead.innerHTML = `
        <tr>
            <th>Roll No</th>
            <th>Student Name</th>
            <th>Theory Marks</th>
            <th>Practical Marks</th>
            <th>Total Marks</th>
            <th>Status</th>
        </tr>
    `;
    tbody.innerHTML = data.map(d => `
        <tr>
            <td><strong>${d.studentRollNumber || '-'}</strong></td>
            <td>${d.studentName || '-'}</td>
            <td>${d.absent ? 'AB' : (d.theoryMarks != null ? d.theoryMarks : '-')}</td>
            <td>${d.absent ? 'AB' : (d.practicalMarks != null ? d.practicalMarks : '-')}</td>
            <td><strong>${d.absent ? 'AB' : d.totalMarks}</strong></td>
            <td>${d.absent ? statusBadge('FAIL') : statusBadge(d.pass ? 'PASS' : 'FAIL')}</td>
        </tr>
    `).join('');
}

function renderPassFailReport(data, thead, tbody) {
    thead.innerHTML = `
        <tr>
            <th>Category</th>
            <th>Count</th>
            <th>Percentage</th>
        </tr>
    `;
    const total = data.totalStudents || 1;
    const passPct = data.passPercentage || 0;
    const failPct = 100 - passPct;

    tbody.innerHTML = `
        <tr><td><strong>Passed Students</strong></td><td><span class="badge badge-success">${data.passedStudents || 0}</span></td><td>${passPct.toFixed(2)}%</td></tr>
        <tr><td><strong>Failed Students</strong></td><td><span class="badge badge-danger">${data.failedStudents || 0}</span></td><td>${failPct.toFixed(2)}%</td></tr>
        <tr class="table-active"><td><strong>Total Evaluated</strong></td><td><strong>${data.totalStudents || 0}</strong></td><td>100.00%</td></tr>
    `;
}

function renderToppersReport(data, thead, tbody) {
    if (!data || !data.length) {
        showEmpty(tbody, 'No topper data available.');
        thead.innerHTML = '';
        return;
    }
    thead.innerHTML = `
        <tr>
            <th>Rank</th>
            <th>Roll No</th>
            <th>Student Name</th>
            <th>Grand Total</th>
            <th>Percentage</th>
            <th>Grade</th>
        </tr>
    `;
    tbody.innerHTML = data.map(d => `
        <tr>
            <td><strong class="text-primary">#${d.rank || 1}</strong></td>
            <td><strong>${d.studentRollNumber || '-'}</strong></td>
            <td>${d.studentName || '-'}</td>
            <td>${d.totalMarksObtained} / ${d.totalMaxMarks}</td>
            <td><strong>${d.percentage ? d.percentage.toFixed(2) + '%' : '0%'}</strong></td>
            <td><span class="badge badge-purple">${d.grade || '-'}</span></td>
        </tr>
    `).join('');
}

function renderAbsentReport(data, thead, tbody) {
    if (!data || !data.length) {
        showEmpty(tbody, 'No absentees reported for this exam!');
        thead.innerHTML = '';
        return;
    }
    thead.innerHTML = `
        <tr>
            <th>Roll No</th>
            <th>Student Name</th>
            <th>Subject</th>
            <th>Exam Date</th>
        </tr>
    `;
    tbody.innerHTML = data.map(d => `
        <tr>
            <td><strong>${d.rollNumber || '-'}</strong></td>
            <td>${d.studentName || '-'}</td>
            <td>${d.subjectName || '-'}</td>
            <td>${formatDate(d.examDate)}</td>
        </tr>
    `).join('');
}

function printReport() {
    window.print();
}
