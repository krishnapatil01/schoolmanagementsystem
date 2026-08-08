document.addEventListener('DOMContentLoaded', async () => {
    const userStr = sessionStorage.getItem('user');
    if (userStr) {
        try {
            const user = JSON.parse(userStr);
            const welcomeEl = document.getElementById('welcomeName');
            if (welcomeEl && user.fullName) welcomeEl.textContent = user.fullName;
        } catch(e){}
    }

    await loadDashboardStats();
});

async function loadDashboardStats() {
    try {
        const stats = await API.get('/api/dashboard/stats');
        
        const elTotalExams = document.getElementById('statTotalExams');
        const elUpcoming = document.getElementById('statUpcoming');
        const elOngoing = document.getElementById('statOngoing');
        const elCompleted = document.getElementById('statCompleted');
        const elResultsPending = document.getElementById('statResultsPending');
        const elPublished = document.getElementById('statPublished');
        const elStudentsEvaluated = document.getElementById('statStudentsEvaluated');
        
        if (elTotalExams) elTotalExams.textContent = stats.totalExams != null ? stats.totalExams : 0;
        if (elUpcoming) elUpcoming.textContent = stats.upcomingExams != null ? stats.upcomingExams : 0;
        if (elOngoing) elOngoing.textContent = stats.ongoingExams != null ? stats.ongoingExams : 0;
        if (elCompleted) elCompleted.textContent = stats.completedExams != null ? stats.completedExams : 0;
        if (elResultsPending) elResultsPending.textContent = stats.resultsPending != null ? stats.resultsPending : 0;
        if (elPublished) elPublished.textContent = stats.publishedResults != null ? stats.publishedResults : 0;
        if (elStudentsEvaluated) elStudentsEvaluated.textContent = stats.studentsEvaluated != null ? stats.studentsEvaluated : 0;

        renderRecentExams(stats.recentExams || []);
    } catch (e) {
        console.error('Error loading dashboard stats:', e);
        showToast('Failed to load dashboard statistics', 'error');
    }
}

function renderRecentExams(exams) {
    const tbody = document.querySelector('#recentExamsTable tbody');
    if (!tbody) return;
    
    if (!exams || exams.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-secondary">No recent exams found</td></tr>';
        return;
    }
    
    tbody.innerHTML = exams.map(exam => `
        <tr>
            <td><strong>${exam.name}</strong></td>
            <td><span class="badge badge-blue">${exam.examType || '-'}</span></td>
            <td>${exam.className || '-'}</td>
            <td>${formatDate(exam.startDate)} to ${formatDate(exam.endDate)}</td>
            <td>${statusBadge(exam.status)}</td>
            <td>
                <a href="exams.html" class="btn btn-sm btn-secondary">View Exam</a>
            </td>
        </tr>
    `).join('');
}
