// Si ya hay sesión, ir directo al panel
if (getSession()) {
  window.location.href = "dashboard.html";
}

const formLogin = document.getElementById("form-login");
const formRegistro = document.getElementById("form-registro");
const mensaje = document.getElementById("mensaje");
const btnLogin = document.getElementById("btn-mostrar-login");
const btnRegistro = document.getElementById("btn-mostrar-registro");

function mostrarMensaje(texto, tipo) {
  mensaje.textContent = texto;
  mensaje.className = "mensaje " + tipo;
}

function ocultarMensaje() {
  mensaje.className = "mensaje oculto";
}

function cambiarFormulario(mostrarLogin) {
  formLogin.classList.toggle("oculto", !mostrarLogin);
  formRegistro.classList.toggle("oculto", mostrarLogin);
  btnLogin.classList.toggle("activo", mostrarLogin);
  btnRegistro.classList.toggle("activo", !mostrarLogin);
  ocultarMensaje();
}

btnLogin.addEventListener("click", () => cambiarFormulario(true));
btnRegistro.addEventListener("click", () => cambiarFormulario(false));

async function enviar(ruta, cuerpo) {
  ocultarMensaje();
  try {
    const datos = await apiFetch(ruta, { method: "POST", body: JSON.stringify(cuerpo) });
    saveSession(datos);
    window.location.href = "dashboard.html";
  } catch (err) {
    mostrarMensaje(mensajeDeError(err), "error");
  }
}

formLogin.addEventListener("submit", (e) => {
  e.preventDefault();
  enviar("/auth/login", {
    username: document.getElementById("login-username").value.trim(),
    password: document.getElementById("login-password").value
  });
});

formRegistro.addEventListener("submit", (e) => {
  e.preventDefault();
  enviar("/auth/register", {
    nombre: document.getElementById("reg-nombre").value.trim(),
    username: document.getElementById("reg-username").value.trim(),
    password: document.getElementById("reg-password").value
  });
});