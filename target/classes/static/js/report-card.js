document.addEventListener('DOMContentLoaded', async () => {
    await loadExamsDropdown();
    
    const urlParams = new URLSearchParams(window.location.search);
    const paramStudentId = urlParams.get('studentId');
    const paramExamId = urlParams.get('examId');

    if (paramExamId) {
        const examSelect = document.getElementById('examSelect');
        if (examSelect) {
            examSelect.value = paramExamId;
            await loadStudents();
            if (paramStudentId) {
                const studentSelect = document.getElementById('studentSelect');
                if (studentSelect) {
                    studentSelect.value = paramStudentId;
                    await generateReportCard();
                }
            }
        }
    }
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

async function loadStudents() {
    const examId = document.getElementById('examSelect').value;
    const studentSelect = document.getElementById('studentSelect');
    const reportCardArea = document.getElementById('reportCardArea');
    const noDataState = document.getElementById('noDataState');
    const btnPrint = document.getElementById('btnPrint');

    if (!examId) {
        if (studentSelect) {
            studentSelect.innerHTML = '<option value="">-- Select Student --</option>';
            studentSelect.disabled = true;
        }
        if (reportCardArea) reportCardArea.classList.add('hidden');
        if (noDataState) noDataState.style.display = 'block';
        if (btnPrint) btnPrint.disabled = true;
        return;
    }

    try {
        const results = await API.get(`/api/results?examId=${examId}`);
        if (!results.length) {
            showToast('No calculated results found for this exam', 'warning');
            studentSelect.innerHTML = '<option value="">No Evaluated Students Found</option>';
            studentSelect.disabled = true;
            return;
        }

        studentSelect.disabled = false;
        studentSelect.innerHTML = '<option value="">-- Select Student --</option>';
        results.forEach(r => {
            const student = r.student || {};
            const name = student.firstName ? `${student.firstName} ${student.lastName}` : (r.studentName || 'Student');
            const roll = student.rollNumber || r.studentRollNumber || '';
            const studentId = student.id || r.studentId;
            
            const opt = document.createElement('option');
            opt.value = studentId;
            opt.textContent = `${roll} - ${name}`;
            studentSelect.appendChild(opt);
        });
    } catch (e) {
        showToast('Failed to load students for report card', 'error');
    }
}

async function generateReportCard() {
    const examId = document.getElementById('examSelect').value;
    const studentSelect = document.getElementById('studentSelect');
    const studentId = studentSelect ? studentSelect.value : null;

    const reportCardArea = document.getElementById('reportCardArea');
    const noDataState = document.getElementById('noDataState');
    const btnPrint = document.getElementById('btnPrint');

    if (!examId || !studentId) {
        if (reportCardArea) reportCardArea.classList.add('hidden');
        if (noDataState) noDataState.style.display = 'block';
        if (btnPrint) btnPrint.disabled = true;
        return;
    }

    try {
        const rc = await API.get(`/api/report-card?studentId=${studentId}&examId=${examId}`);

        if (reportCardArea) reportCardArea.classList.remove('hidden');
        if (noDataState) noDataState.style.display = 'none';
        if (btnPrint) btnPrint.disabled = false;

        document.getElementById('rcStudentName').textContent = rc.studentName || '-';
        document.getElementById('rcRollNo').textContent = rc.studentRollNumber || '-';
        document.getElementById('rcClass').textContent = `${rc.className || ''} ${rc.sectionName ? '(' + rc.sectionName + ')' : ''}`;
        document.getElementById('rcExamName').textContent = rc.examName || '-';

        const tbody = document.getElementById('rcSubjectsBody');
        if (tbody) {
            const subjects = rc.subjectMarks || [];
            tbody.innerHTML = subjects.map(sm => {
                const theoryObt = sm.absent ? 'AB' : (sm.theoryMarksObtained != null ? sm.theoryMarksObtained : '-');
                const practicalObt = sm.absent ? 'AB' : (sm.practicalMarksObtained != null ? sm.practicalMarksObtained : '-');
                const totalObt = sm.absent ? 'AB' : (sm.totalMarksObtained != null ? sm.totalMarksObtained : 0);
                const passBadge = sm.absent ? statusBadge('FAIL') : statusBadge(sm.pass ? 'PASS' : 'FAIL');

                return `
                <tr>
                    <td><strong>${sm.subjectName || '-'}</strong></td>
                    <td>${sm.maxMarks}</td>
                    <td>${theoryObt}</td>
                    <td>${practicalObt}</td>
                    <td><strong>${totalObt}</strong></td>
                    <td>${passBadge}</td>
                </tr>
            `}).join('');
        }

        document.getElementById('rcGrandTotal').textContent = rc.totalMarksObtained != null ? rc.totalMarksObtained : 0;
        document.getElementById('rcMaxTotal').textContent = rc.totalMaxMarks != null ? rc.totalMaxMarks : 0;
        document.getElementById('rcPercentage').textContent = rc.percentage ? rc.percentage.toFixed(2) : '0';
        document.getElementById('rcGrade').textContent = rc.grade || '-';

        const rcResult = document.getElementById('rcResult');
        if (rcResult) {
            rcResult.innerHTML = statusBadge(rc.resultStatus || 'FAIL');
        }

        const rcRank = document.getElementById('rcRank');
        if (rcRank) {
            rcRank.textContent = rc.rank ? `#${rc.rank}` : 'N/A (Failed/Absent)';
        }

        const rcRemarks = document.getElementById('rcRemarks');
        if (rcRemarks) {
            rcRemarks.textContent = rc.remarks || (rc.resultStatus === 'PASS' ? 'Passed with Good Standing' : 'Needs Improvement');
        }

    } catch (e) {
        showToast(e.message || 'Failed to generate report card', 'error');
    }
}
