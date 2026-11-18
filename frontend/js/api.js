// Configuración y utilidades compartidas por todas las páginas
const API_URL = "http://localhost:8080";

// ---------- Sesión (el token se guarda solo mientras la pestaña esté abierta) ----------
function getSession() {
  try {
    return JSON.parse(sessionStorage.getItem("logitrack_session"));
  } catch (e) {
    return null;
  }
}

function saveSession(datos) {
  sessionStorage.setItem("logitrack_session", JSON.stringify(datos));
}

function clearSession() {
  sessionStorage.removeItem("logitrack_session");
}

// ---------- Errores de la API ----------
class ApiError extends Error {
  constructor(status, body) {
    super((body && body.message) || ("Error " + status));
    this.status = status;
    this.errores = (body && body.errores) || null;
  }
}

function mensajeDeError(err) {
  let mensaje = err.message;
  if (err.errores) {
    mensaje += ": " + Object.entries(err.errores).map(([campo, msg]) => campo + " → " + msg).join(" | ");
  }
  return mensaje;
}

// ---------- Petición genérica con el token JWT ----------
async function apiFetch(ruta, opciones = {}) {
  const session = getSession();
  const headers = Object.assign({ "Content-Type": "application/json" }, opciones.headers || {});
  if (session && session.token) {
    headers["Authorization"] = "Bearer " + session.token;
  }

  let respuesta;
  try {
    respuesta = await fetch(API_URL + ruta, Object.assign({}, opciones, { headers: headers }));
  } catch (e) {
    throw new Error("No se pudo conectar con el servidor. ¿Está encendido el backend en " + API_URL + "?");
  }

  if (respuesta.status === 204) {
    return null;
  }

  let cuerpo = null;
  const texto = await respuesta.text();
  if (texto) {
    try {
      cuerpo = JSON.parse(texto);
    } catch (e) {
      cuerpo = null;
    }
  }

  if (!respuesta.ok) {
    // Token vencido o inválido en una ruta protegida: volver al login
    if (respuesta.status === 401 && !ruta.startsWith("/auth/")) {
      clearSession();
      window.location.href = "index.html";
    }
    throw new ApiError(respuesta.status, cuerpo);
  }
  return cuerpo;
}

// ---------- Formato y seguridad ----------
// Escapa texto antes de ponerlo en el HTML (evita inyección de código)
function esc(valor) {
  if (valor === null || valor === undefined) {
    return "—";
  }
  return String(valor)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function formatoMoneda(numero) {
  return new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 }).format(numero);
}

function formatoFecha(iso) {
  return new Date(iso).toLocaleString("es-CO");
}