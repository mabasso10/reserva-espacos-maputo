// ============================================================
//  api.js - Utilitário de chamadas à REST API
// ============================================================

const API_BASE = '';  // Mesmo host (Spring serve tudo na porta 8080)

const API = {
    _headers() {
        const token = localStorage.getItem('token');
        const h = { 'Content-Type': 'application/json' };
        if (token) h['Authorization'] = 'Bearer ' + token;
        return h;
    },

    async _handle(res) {
        if (res.status === 401) {
            localStorage.clear();
            window.location.href = '/index.html';
            throw new Error('Sessão expirada. Faça login novamente.');
        }
        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }
        if (!res.ok) {
            const msg = data?.erro || data?.message || data?.error || JSON.stringify(data);
            throw new Error(msg || 'Erro ' + res.status);
        }
        return data;
    },

    async login(email, senha) {
        const res = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });
        return this._handle(res);
    },

    async register(nome, email, senha, role) {
        const res = await fetch('/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha, role })
        });
        return this._handle(res);
    },

    async get(path) {
        const res = await fetch(API_BASE + path, { headers: this._headers() });
        return this._handle(res);
    },

    async post(path, body) {
        const res = await fetch(API_BASE + path, {
            method: 'POST',
            headers: this._headers(),
            body: JSON.stringify(body)
        });
        return this._handle(res);
    },

    async put(path, body) {
        const res = await fetch(API_BASE + path, {
            method: 'PUT',
            headers: this._headers(),
            body: JSON.stringify(body)
        });
        return this._handle(res);
    },

    async del(path) {
        const res = await fetch(API_BASE + path, {
            method: 'DELETE',
            headers: this._headers()
        });
        if (res.status === 204) return null;
        return this._handle(res);
    },

    // Enviar FormData (multipart/form-data) — sem Content-Type manual,
    // o browser define a boundary automaticamente
    async upload(path, formData) {
        const token = localStorage.getItem('token');
        const headers = {};
        if (token) headers['Authorization'] = 'Bearer ' + token;
        const res = await fetch(API_BASE + path, {
            method: 'POST',
            headers,
            body: formData
        });
        return this._handle(res);
    }
};
