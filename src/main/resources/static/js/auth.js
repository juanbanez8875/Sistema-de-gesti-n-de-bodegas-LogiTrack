/**
 * LogiTrack - Lógica de la página de login/registro.
 */
document.addEventListener('DOMContentLoaded', () => {

  // Si ya hay sesión, ir al dashboard
  if (API.isLoggedIn()) {
    window.location.href = 'dashboard.html';
    return;
  }

  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');
  const showRegister = document.getElementById('showRegister');
  const showLogin = document.getElementById('showLogin');
  const loginSection = document.getElementById('loginSection');
  const registerSection = document.getElementById('registerSection');
  const alertBox = document.getElementById('alertBox');

  function showAlert(msg) {
    alertBox.textContent = msg;
    alertBox.style.display = 'block';
  }

  // ---- Login ----
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    alertBox.style.display = 'none';
    const username = document.getElementById('loginUser').value.trim();
    const password = document.getElementById('loginPass').value;

    try {
      const res = await API.auth.login(username, password);
      API.setToken(res.token, res.username, res.rol);
      window.location.href = 'dashboard.html';
    } catch (err) {
      showAlert(err.message || 'Credenciales inválidas');
    }
  });

  // ---- Registro ----
  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    alertBox.style.display = 'none';
    const data = {
      username: document.getElementById('regUser').value.trim(),
      password: document.getElementById('regPass').value,
      email: document.getElementById('regEmail').value.trim(),
      nombre: document.getElementById('regName').value.trim(),
      rol: document.getElementById('regRole').value
    };
    try {
      const res = await API.auth.register(data);
      API.setToken(res.token, res.username, res.rol);
      window.location.href = 'dashboard.html';
    } catch (err) {
      showAlert(err.message || 'No se pudo registrar');
    }
  });

  // ---- Toggle login/register ----
  showRegister.addEventListener('click', (e) => {
    e.preventDefault();
    loginSection.style.display = 'none';
    registerSection.style.display = 'block';
    alertBox.style.display = 'none';
  });

  showLogin.addEventListener('click', (e) => {
    e.preventDefault();
    registerSection.style.display = 'none';
    loginSection.style.display = 'block';
    alertBox.style.display = 'none';
  });
});
