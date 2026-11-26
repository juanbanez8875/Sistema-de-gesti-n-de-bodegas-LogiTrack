/**
 * LogiTrack - Capa de comunicación con la API REST.
 * Gestiona el token JWT en localStorage y añade el header Authorization.
 */
const API = (() => {
  const BASE_URL = 'http://localhost:8080';
  const TOKEN_KEY = 'logitrack_token';
  const USER_KEY = 'logitrack_user';
  const ROLE_KEY = 'logitrack_role';

  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  function setToken(token, username, role) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, username);
    localStorage.setItem(ROLE_KEY, role);
  }

  function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(ROLE_KEY);
  }

  function isLoggedIn() {
    return !!getToken();
  }

  function getUser() {
    return localStorage.getItem(USER_KEY);
  }

  function getRole() {
    return localStorage.getItem(ROLE_KEY);
  }

  async function request(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }

    const opts = { method, headers };
    if (body) {
      opts.body = JSON.stringify(body);
    }

    const res = await fetch(BASE_URL + path, opts);

    if (res.status === 401) {
      clearToken();
      window.location.href = 'index.html';
      throw new Error('Sesión expirada');
    }

    if (res.status === 204) {
      return null;
    }

    const data = await res.json();

    if (!res.ok) {
      const msg = data.message || 'Error en la petición';
      const details = data.details ? '\n' + data.details.join('\n') : '';
      throw new Error(msg + details);
    }

    return data;
  }

  function qs(params) {
    return '?' + Object.entries(params)
      .map(([k, v]) => k + '=' + encodeURIComponent(v))
      .join('&');
  }

  // ---- Auth ----
  const auth = {
    login: (username, password) => request('POST', '/auth/login', { username, password }),
    register: (data) => request('POST', '/auth/register', data),
    perfil: () => request('GET', '/auth/perfil'),
    cambiarPassword: (data) => request('PUT', '/auth/password', data)
  };

  // ---- Bodegas ----
  const bodegas = {
    list: () => request('GET', '/bodegas'),
    get: (id) => request('GET', '/bodegas/' + id),
    create: (data) => request('POST', '/bodegas', data),
    update: (id, data) => request('PUT', '/bodegas/' + id, data),
    remove: (id) => request('DELETE', '/bodegas/' + id),
    buscar: (q) => request('GET', '/bodegas/buscar' + qs({ q })),
    paginado: (page, size, sort, dir) =>
      request('GET', '/bodegas/paginado' + qs({ page, size, sort, dir }))
  };

  // ---- Productos ----
  const productos = {
    list: () => request('GET', '/productos'),
    get: (id) => request('GET', '/productos/' + id),
    create: (data) => request('POST', '/productos', data),
    update: (id, data) => request('PUT', '/productos/' + id, data),
    remove: (id) => request('DELETE', '/productos/' + id),
    buscarPorNombre: (nombre) => request('GET', '/productos/buscar' + qs({ nombre })),
    buscarPorCategoria: (categoria) => request('GET', '/productos/categoria' + qs({ categoria })),
    paginado: (page, size, sort, dir) =>
      request('GET', '/productos/paginado' + qs({ page, size, sort, dir })),
    historial: (id) => request('GET', '/productos/' + id + '/historial')
  };

  // ---- Movimientos ----
  const movimientos = {
    list: () => request('GET', '/movimientos'),
    get: (id) => request('GET', '/movimientos/' + id),
    create: (data) => request('POST', '/movimientos', data),
    byFecha: (inicio, fin) => request('GET', '/movimientos/fecha?inicio=' + inicio + '&fin=' + fin),
    paginado: (page, size, sort, dir) =>
      request('GET', '/movimientos/paginado' + qs({ page, size, sort, dir }))
  };

  // ---- Reportes ----
  const reportes = {
    resumen: () => request('GET', '/reportes/resumen'),
    estadisticas: () => request('GET', '/reportes/estadisticas'),
    stockBajo: () => request('GET', '/reportes/stock-bajo')
  };

  // ---- Auditorias ----
  const auditorias = {
    list: () => request('GET', '/auditorias'),
    byUsuario: (u) => request('GET', '/auditorias/usuario/' + u),
    byTipo: (t) => request('GET', '/auditorias/tipo/' + t),
    byEntidad: (e) => request('GET', '/auditorias/entidad/' + e)
  };

  return { getToken, setToken, clearToken, isLoggedIn, getUser, getRole,
           auth, bodegas, productos, movimientos, reportes, auditorias };
})();
