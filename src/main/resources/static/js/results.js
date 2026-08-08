let currentExamId = null;

document.addEventListener('DOMContentLoaded', async () => {
    await loadExamsDropdown();
});

async function loadExamsDropdown() {
    try {
        const exams = await API.get('/api/exams/all');
        const select = document.getElementById('examSelect');
        if (!select) return;
        select.innerHTML = '<option value="">-- Select an Exam --</option>';
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

async function loadResults() {
    const examSelect = document.getElementById('examSelect');
    currentExamId = examSelect ? examSelect.value : null;

    const actionButtons = document.getElementById('resultsActionBar');
    const resultsCard = document.getElementById('resultsCard');
    const noExamSelected = document.getElementById('noExamSelected');
    const tbody = document.querySelector('#resultsTable tbody');

    if (!currentExamId) {
        if (actionButtons) actionButtons.classList.add('hidden');
        if (resultsCard) resultsCard.classList.add('hidden');
        if (noExamSelected) noExamSelected.style.display = 'block';
        return;
    }

    if (actionButtons) actionButtons.classList.remove('hidden');
    if (resultsCard) resultsCard.classList.remove('hidden');
    if (noExamSelected) noExamSelected.style.display = 'none';

    showLoading(tbody);
    
    try {
        const results = await API.get(`/api/results?examId=${currentExamId}`);
        renderResults(results);
    } catch (e) {
        showEmpty(tbody, 'Failed to load results');
    }
}

function renderResults(results) {
    const tbody = document.querySelector('#resultsTable tbody');
    const badge = document.getElementById('resultsStatusBadge');
    const btnPublish = document.getElementById('btnPublish');
    const btnUnpublish = document.getElementById('btnUnpublish');

    if (!results || !results.length) {
        showEmpty(tbody, 'No results generated yet. Click "Calculate Results" above to process student marks.');
        if (badge) badge.innerHTML = statusBadge('DRAFT');
        if (btnPublish) btnPublish.style.display = 'none';
        if (btnUnpublish) btnUnpublish.style.display = 'none';
        return;
    }

    const isPublished = results[0].publishStatus === 'PUBLISHED';
    if (badge) badge.innerHTML = statusBadge(isPublished ? 'PUBLISHED' : 'DRAFT');
    
    if (btnPublish) btnPublish.style.display = isPublished ? 'none' : 'inline-block';
    if (btnUnpublish) btnUnpublish.style.display = isPublished ? 'inline-block' : 'none';

    tbody.innerHTML = results.map(r => {
        const student = r.student || {};
        const studentName = student.firstName ? `${student.firstName} ${student.lastName}` : (r.studentName || '-');
        const rollNumber = student.rollNumber || r.studentRollNumber || '-';
        const rankDisplay = r.passed && r.rank != null ? `<strong>#${r.rank}</strong>` : '-';
        const passBadge = statusBadge(r.passed ? 'PASS' : 'FAIL');
        const examId = r.exam ? r.exam.id : currentExamId;
        const studentId = student.id || r.studentId;

        return `
        <tr>
            <td>${rankDisplay}</td>
            <td><strong>${rollNumber}</strong></td>
            <td>${studentName}</td>
            <td>${r.grandTotal} / ${r.maxTotal}</td>
            <td><strong>${r.percentage ? r.percentage.toFixed(2) + '%' : '0%'}</strong></td>
            <td><span class="badge badge-purple">${r.grade || '-'}</span></td>
            <td>${passBadge}</td>
            <td>
                <a href="report-card.html?studentId=${studentId}&examId=${examId}" class="btn btn-sm btn-primary" target="_blank"><i>🎓</i> Report Card</a>
            </td>
        </tr>
    `}).join('');
}

async function calculateResults() {
    if (!currentExamId) return;
    try {
        await API.post(`/api/results/calculate?examId=${currentExamId}`, {});
        showToast('Results calculated successfully');
        loadResults();
    } catch (e) {
        showToast(e.message || 'Failed to calculate results', 'error');
    }
}

async function publishResults() {
    if (!currentExamId) return;
    if (await showConfirm('Publish Results', 'Are you sure you want to publish the results? Published results will be visible on student report cards.')) {
        try {
            await API.post(`/api/results/publish?examId=${currentExamId}`, {});
            showToast('Results published successfully');
            loadResults();
        } catch (e) {
            showToast(e.message || 'Failed to publish results', 'error');
        }
    }
}

async function unpublishResults() {
    if (!currentExamId) return;
    if (await showConfirm('Unpublish Results', 'Are you sure you want to unpublish the results?')) {
        try {
            await API.post(`/api/results/unpublish?examId=${currentExamId}`, {});
            showToast('Results unpublished successfully');
            loadResults();
        } catch (e) {
            showToast(e.message || 'Failed to unpublish results', 'error');
        }
    }
}
