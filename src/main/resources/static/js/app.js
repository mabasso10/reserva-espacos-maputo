// ============================================================
//  app.js - Funções partilhadas de UI
// ============================================================

/** Verifica autenticação - redireciona para login se não autenticado */
function checkAuth() {
    if (!localStorage.getItem('token')) {
        window.location.href = '/index.html';
    }
}

/** Logout */
function logout() {
    localStorage.clear();
    window.location.href = '/index.html';
}

/** Inicializa UI comum (sidebar user info, data, role badge) */
function initUI() {
    const raw   = localStorage.getItem('user');
    const token = localStorage.getItem('token');

    // CORRIGIDO: validar token antes de mostrar dados de sessão anterior.
    // Se o token não existir ou estiver expirado (verificação de exp no payload),
    // limpar o localStorage e redirecionar — evita mostrar dados de sessão anterior.
    if (!raw || !token) { localStorage.clear(); return; }

    // Verificar expiração do JWT sem biblioteca — basta ler o payload (base64)
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp && payload.exp * 1000 < Date.now()) {
            // Token expirado — forçar novo login
            localStorage.clear();
            window.location.href = '/index.html';
            return;
        }
    } catch (_) {
        // Token malformado — limpar igualmente
        localStorage.clear();
        window.location.href = '/index.html';
        return;
    }

    const user = JSON.parse(raw);

    // Sidebar
    const avatar = document.getElementById('sidebar-avatar');
    const name = document.getElementById('sidebar-name');
    const role = document.getElementById('sidebar-role');
    if (avatar) avatar.textContent = (user.nome || '?')[0].toUpperCase();
    if (name) name.textContent = user.nome || user.email;
    if (role) role.textContent = user.role;

    // Topbar data
    const dateEl = document.getElementById('topbar-date');
    if (dateEl) {
        dateEl.textContent = new Date().toLocaleDateString('pt-MZ', {
            weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
        });
    }

    // Topbar role badge
    const badgeEl = document.getElementById('topbar-role');
    if (badgeEl) {
        badgeEl.textContent = user.role;
        badgeEl.className = 'badge-role ' + user.role;
    }
}

/** Abre sidebar (mobile) */
function openSidebar() {
    document.getElementById('sidebar')?.classList.add('open');
    document.getElementById('overlay')?.classList.add('open');
}

/** Fecha sidebar (mobile) */
function closeSidebar() {
    document.getElementById('sidebar')?.classList.remove('open');
    document.getElementById('overlay')?.classList.remove('open');
}

/** Fecha modal ao clicar no backdrop */
function closeOnBackdrop(event, modalId) {
    const id = modalId || 'modal';
    if (event.target.id === id) {
        document.getElementById(id)?.classList.remove('show');
    }
}

/** Fecha modal principal */
function closeModal() {
    document.getElementById('modal')?.classList.remove('show');
    hideModalAlert();
}

/** Limpa campos de formulário */
function clearForm(ids) {
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

/** Preenche um select com lista de objectos */
function populateSelect(selectId, items, labelFn) {
    const sel = document.getElementById(selectId);
    if (!sel) return;
    const current = sel.value;
    sel.innerHTML = items.map(item =>
        `<option value="${item.id}">${labelFn(item)}</option>`
    ).join('');
    if (current) sel.value = current;
}

/** Mostra alerta dentro do modal */
function showModalAlert(msg) {
    const el = document.getElementById('modal-alert');
    if (el) { el.textContent = msg; el.className = 'alert error'; }
}

/** Esconde alerta do modal */
function hideModalAlert() {
    const el = document.getElementById('modal-alert');
    if (el) el.className = 'alert hidden';
}

/** Mostra erro na tabela */
function showTableError(tbodyId, cols, msg) {
    const tbody = document.getElementById(tbodyId);
    if (tbody) tbody.innerHTML = `<tr><td colspan="${cols}" class="loading-cell" style="color:var(--danger)">${msg}</td></tr>`;
}

/** Formata valor em MZN */
function formatMZN(val) {
    if (val === null || val === undefined) return '-';
    return new Intl.NumberFormat('pt-MZ', {
        style: 'currency', currency: 'MZN', minimumFractionDigits: 2
    }).format(parseFloat(val));
}

/** Formata data */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('pt-MZ', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

/**
 * Devolve o role do utilizador autenticado ('ADMIN', 'PROPRIETARIO', 'CLIENTE')
 * a partir do localStorage. Usado para controlo condicional de UI por role.
 */
function getCurrentRole() {
    try {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        return (user.role || '').replace('ROLE_', '').toUpperCase();
    } catch (e) {
        return '';
    }
}
