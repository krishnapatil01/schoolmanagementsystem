// API Helper
const API = {
    async request(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };
        const fetchOptions = { ...defaultOptions, ...options };
        if (options.headers) {
            fetchOptions.headers = { ...defaultOptions.headers, ...options.headers };
        }
        
        try {
            const response = await fetch(url, fetchOptions);
            if (response.status === 401 && !window.location.pathname.endsWith('index.html')) {
                window.location.href = 'index.html';
                return;
            }
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || errorData.error || `HTTP error! status: ${response.status}`);
            }
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error("API Request Failed:", error);
            throw error;
        }
    },
    async get(url) {
        return this.request(url);
    },
    async post(url, data) {
        return this.request(url, { method: 'POST', body: JSON.stringify(data) });
    },
    async put(url, data) {
        return this.request(url, { method: 'PUT', body: JSON.stringify(data) });
    },
    async delete(url) {
        return this.request(url, { method: 'DELETE' });
    },
    async patch(url, data) {
        return this.request(url, { method: 'PATCH', body: data ? JSON.stringify(data) : undefined });
    }
};

async function checkAuth() {
    if (window.location.pathname.endsWith('index.html') || window.location.pathname === '/') return;
    try {
        const user = await API.get('/api/auth/me');
        if (user && user.fullName) {
            const userNameEl = document.getElementById('userName');
            if (userNameEl) userNameEl.textContent = user.fullName;
        }
    } catch (e) {
        window.location.href = 'index.html';
    }
}

function initSidebar() {
    const currentPath = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.sidebar-nav a');
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (currentPath === '' && href === 'dashboard.html')) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) sidebar.classList.toggle('active');
}

async function logout() {
    try {
        await API.post('/api/auth/logout', {});
        sessionStorage.clear();
        window.location.href = 'index.html';
    } catch (e) {
        showToast('Logout failed', 'error');
    }
}

function showToast(message, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <div class="toast-content">${message}</div>
        <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
    `;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'fadeOut 0.3s forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function showConfirm(title, message) {
    return new Promise((resolve) => {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay active';
        overlay.innerHTML = `
            <div class="modal" style="max-width: 400px;">
                <div class="modal-header">
                    <h3 class="modal-title">${title}</h3>
                </div>
                <div class="modal-body">
                    <p>${message}</p>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" id="confirm-no">Cancel</button>
                    <button class="btn btn-primary" id="confirm-yes">Confirm</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);
        
        const yesBtn = overlay.querySelector('#confirm-yes');
        const noBtn = overlay.querySelector('#confirm-no');
        
        yesBtn.onclick = () => { overlay.remove(); resolve(true); };
        noBtn.onclick = () => { overlay.remove(); resolve(false); };
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        return new Date(dateStr).toLocaleDateString('en-IN', {day:'2-digit', month:'short', year:'numeric'});
    } catch(e) {
        return dateStr;
    }
}

function formatTime(timeStr) {
    if (!timeStr) return '-';
    if (timeStr.length === 5 || timeStr.length === 8) {
        const parts = timeStr.split(':');
        const date = new Date();
        date.setHours(parseInt(parts[0]), parseInt(parts[1]), 0);
        return date.toLocaleTimeString('en-IN', {hour: '2-digit', minute:'2-digit'});
    }
    return timeStr;
}

function statusBadge(status) {
    if (!status) return '<span class="badge badge-draft">-</span>';
    const s = status.toUpperCase();
    const classMap = {
        DRAFT: 'badge-draft',
        SCHEDULED: 'badge-scheduled',
        ONGOING: 'badge-ongoing',
        COMPLETED: 'badge-completed',
        PUBLISHED: 'badge-published',
        PASS: 'badge-pass',
        FAIL: 'badge-fail',
        LOCKED: 'badge-draft'
    };
    const cls = classMap[s] || 'badge-draft';
    return `<span class="badge ${cls}">${s}</span>`;
}

function showLoading(container) {
    if (container) {
        container.innerHTML = '<tr><td colspan="10" class="loader-container"><div class="spinner"></div></td></tr>';
    }
}

function showEmpty(container, message = 'No data found') {
    if (container) {
        container.innerHTML = `<tr><td colspan="10" class="empty-state"><i>📭</i><h3>${message}</h3></td></tr>`;
    }
}

function populateSelect(selectId, options, valueKey, textKey, defaultText = 'Select...') {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = `<option value="">${defaultText}</option>`;
    if (Array.isArray(options)) {
        options.forEach(opt => {
            const option = document.createElement('option');
            option.value = opt[valueKey];
            option.textContent = opt[textKey];
            select.appendChild(option);
        });
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await checkAuth();
    initSidebar();
});
