const session = getSession();

const esAdmin = session && session.rol === "ADMIN";
const TITULOS = { resumen: "Resumen", bodegas: "Bodegas", productos: "Productos", movimientos: "Movimientos", auditorias: "Auditorías" };
const ICONOS = {
  home: '<path d="M3 11l9-8 9 8M5 10v10h14V10"/>', build: '<path d="M4 21V8l8-5 8 5v13M9 21v-6h6v6"/>',
  box: '<path d="M21 8l-9-5-9 5v8l9 5 9-5zM3 8l9 5 9-5M12 13v8"/>', swap: '<path d="M4 8h14l-4-4M20 16H6l4 4"/>',
  shield: '<path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="M9 12l2 2 4-4"/>',
  bell: '<path d="M6 16V11a6 6 0 0112 0v5l2 2H4zM10 21h4"/>', user: '<circle cx="12" cy="8" r="4"/><path d="M4 21c1-4 4-6 8-6s7 2 8 6"/>',
  alert: '<path d="M12 3l10 18H2zM12 10v5M12 18v.5"/>', edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/>'
};
const ico = (n) => '<svg class="i" viewBox="0 0 24 24">' + ICONOS[n] + "</svg>";

let bodegasCache = [], productosCache = [], stockPorBodega = [], notifs = [], filtroNotif = "todo";

function iniciar() {
  document.querySelectorAll("[data-i]").forEach((el) => el.insertAdjacentHTML("afterbegin", ico(el.dataset.i)));
  document.getElementById("usuario-nombre").textContent = session.username;
  document.getElementById("usuario-rol").textContent = esAdmin ? "Administrador" : "Empleado";
  document.getElementById("avatar").textContent = session.username.charAt(0).toUpperCase();
  if (!esAdmin) document.querySelectorAll(".solo-admin").forEach((el) => el.remove());

  document.getElementById("btn-logout").onclick = () => { clearSession(); window.location.href = "index.html"; };
  document.querySelectorAll(".nav-btn").forEach((b) => (b.onclick = () => mostrarTab(b.dataset.tab)));
  const on = (id, ev, fn) => { const el = document.getElementById(id); if (el) el.addEventListener(ev, fn); };
  on("btn-stock-bajo", "click", () => cargarProductos(true));
  on("btn-todos-productos", "click", () => cargarProductos(false));
  on("buscar-productos", "input", pintarProductos);
  on("buscar-bodegas", "input", pintarBodegas);
  on("btn-nueva-bodega", "click", () => formBodega());
  on("btn-nuevo-producto", "click", () => formProducto());
  on("btn-filtrar-fechas", "click", filtrarMovimientosPorFechas);
  on("btn-todos-mov", "click", cargarMovimientos);
  on("mov-tipo", "change", actualizarCamposBodega);
  on("btn-agregar-linea", "click", agregarLinea);
  on("form-movimiento", "submit", registrarMovimiento);
  on("btn-filtrar-aud", "click", cargarAuditorias);
  if (esAdmin) iniciarNotificaciones();
  mostrarTab("resumen");
}

// ---------- Interfaz ----------
function mostrarMensaje(texto, tipo) {
  const t = document.createElement("div");
  t.className = "toast " + (tipo === "error" ? "error" : "");
  t.textContent = texto;
  document.getElementById("toasts").appendChild(t);
  setTimeout(() => t.remove(), 4500);
}
function mostrarTab(nombre) {
  document.querySelectorAll(".nav-btn").forEach((b) => b.classList.toggle("active", b.dataset.tab === nombre));
  document.querySelectorAll("main > section").forEach((s) => s.classList.add("oculto"));
  document.getElementById("tab-" + nombre).classList.remove("oculto");
  document.getElementById("titulo-pagina").textContent = TITULOS[nombre];
  ({ resumen: cargarResumen, bodegas: cargarBodegas, productos: () => cargarProductos(false), movimientos: iniciarMovimientos, auditorias: cargarAuditorias })[nombre]();
}
function tabla(columnas, filas, vacio) {
  if (!filas || !filas.length) return '<p class="vacio">' + (vacio || "No hay datos para mostrar.") + "</p>";
  const cab = columnas.map((c) => "<th>" + esc(c.t) + "</th>").join("");
  const cuerpo = filas.map((f) => "<tr>" + columnas.map((c) => "<td>" + (c.h ? c.h(f) : esc(c.v(f))) + "</td>").join("") + "</tr>").join("");
  return '<div class="tabla-wrap"><table><thead><tr>' + cab + "</tr></thead><tbody>" + cuerpo + "</tbody></table></div>";
}
async function cargar(id, peticion, html) {
  const c = document.getElementById(id);
  c.innerHTML = '<p class="vacio">Cargando…</p>';
  try { c.innerHTML = html(await peticion()); } catch (err) { c.innerHTML = ""; mostrarMensaje(mensajeDeError(err), "error"); }
}
const badge = (t) => '<span class="badge ' + esc(t) + '">' + esc(t) + "</span>";
const barra = (nombre, valor, max, extra) => {
  const pct = max > 0 ? Math.min(100, Math.round((valor / max) * 100)) : 0;
  return '<div class="barra"><div><span>' + esc(nombre) + "</span><span>" + esc(extra) + '</span></div><div class="b"><i class="' + (pct >= 90 ? "llena" : "") + '" style="width:' + pct + '%"></i></div></div>';
};
// Modal con formulario. campos: [{k, l, t, max}]
function modal(titulo, campos, valores, guardar) {
  const bg = document.createElement("div");
  bg.className = "modal-bg";
  bg.innerHTML = '<form class="modal"><h3>' + esc(titulo) + "</h3>" +
    campos.map((c) => '<label for="m-' + c.k + '">' + esc(c.l) + '</label><input id="m-' + c.k + '" type="' + (c.t || "text") + '" ' + (c.t === "number" ? 'step="any" min="0.01"' : 'maxlength="' + (c.max || 100) + '"') + ' required value="' + esc(valores[c.k] === undefined ? "" : valores[c.k]).replace("—", "") + '">').join("") +
    '<div class="acts"><button type="button" class="btn btn-sec">Cancelar</button><button class="btn">Guardar</button></div></form>';
  document.body.appendChild(bg);
  bg.querySelector(".btn-sec").onclick = () => bg.remove();
  bg.querySelector("input").focus();
  bg.querySelector("form").onsubmit = async (e) => {
    e.preventDefault();
    const datos = {};
    campos.forEach((c) => { const v = document.getElementById("m-" + c.k).value.trim(); datos[c.k] = c.t === "number" ? Number(v) : v; });
    try { await guardar(datos); bg.remove(); } catch (err) { mostrarMensaje(mensajeDeError(err), "error"); }
  };
}

// ---------- Resumen ----------
async function cargarResumen() {
  try {
    const [res, bodegas, productos, bajo, movs] = await Promise.all([apiFetch("/reportes/resumen"), apiFetch("/bodegas"), apiFetch("/productos"), apiFetch("/productos/stock-bajo"), apiFetch("/movimientos")]);
    const total = res.stockPorBodega.reduce((s, b) => s + b.stockTotal, 0);
    const k = (t, v, c) => '<div class="kpi ' + (c || "") + '"><span>' + t + "</span><strong>" + v + "</strong></div>";
    document.getElementById("kpis").innerHTML = k("Bodegas", bodegas.length) + k("Productos", productos.length) + k("Unidades en stock", total.toLocaleString("es-CO")) + k("Con stock bajo", bajo.length, bajo.length ? "alerta" : "");
    const cap = Object.fromEntries(bodegas.map((b) => [b.id, b.capacidad]));
    document.getElementById("resumen-stock").innerHTML = res.stockPorBodega.length ? res.stockPorBodega.map((b) => barra(b.bodega, b.stockTotal, cap[b.bodegaId] || 0, b.stockTotal + " / " + (cap[b.bodegaId] || "?"))).join("") : '<p class="vacio">Sin datos.</p>';
    const max = Math.max(1, ...res.productosMasMovidos.map((p) => p.totalMovido));
    document.getElementById("resumen-movidos").innerHTML = res.productosMasMovidos.length ? res.productosMasMovidos.map((p) => barra(p.producto, p.totalMovido, max, p.totalMovido + " u.")).join("") : '<p class="vacio">Aún no hay movimientos.</p>';
    document.getElementById("resumen-ultimos").innerHTML = tablaMovimientos(movs.slice(0, 5));
  } catch (err) { mostrarMensaje(mensajeDeError(err), "error"); }
}

// ---------- Bodegas ----------
async function cargarBodegas() {
  try { [bodegasCache, stockPorBodega] = [await apiFetch("/bodegas"), (await apiFetch("/reportes/resumen")).stockPorBodega]; } catch (e) { mostrarMensaje(mensajeDeError(e), "error"); }
  pintarBodegas();
}
function pintarBodegas() {
  const q = document.getElementById("buscar-bodegas").value.toLowerCase();
  const filas = bodegasCache.filter((b) => (b.nombre + b.ubicacion + b.encargado).toLowerCase().includes(q));
  const stock = Object.fromEntries(stockPorBodega.map((s) => [s.bodegaId, s.stockTotal]));
  const cols = [
    { t: "Nombre", h: (f) => "<strong>" + esc(f.nombre) + "</strong>" }, { t: "Ubicación", v: (f) => f.ubicacion },
    { t: "Ocupación", h: (f) => barra("", stock[f.id] || 0, f.capacidad, (stock[f.id] || 0) + " / " + f.capacidad) }, { t: "Encargado", v: (f) => f.encargado }
  ];
  if (esAdmin) cols.push({ t: "", h: (f) => '<button class="btn btn-sec btn-sm" data-e="' + f.id + '">Editar</button> <button class="btn btn-bad btn-sm" data-d="' + f.id + '">Eliminar</button>' });
  const c = document.getElementById("lista-bodegas");
  c.innerHTML = tabla(cols, filas, "No hay bodegas que coincidan.");
  c.querySelectorAll("[data-e]").forEach((b) => (b.onclick = () => formBodega(bodegasCache.find((x) => x.id == b.dataset.e))));
  c.querySelectorAll("[data-d]").forEach((b) => (b.onclick = () => eliminar("/bodegas/" + b.dataset.d, "esta bodega", cargarBodegas)));
}
function formBodega(b) {
  modal(b ? "Editar bodega" : "Nueva bodega", [{ k: "nombre", l: "Nombre" }, { k: "ubicacion", l: "Ubicación", max: 150 }, { k: "capacidad", l: "Capacidad (unidades)", t: "number" }, { k: "encargado", l: "Encargado" }], b || {}, async (d) => {
    await apiFetch(b ? "/bodegas/" + b.id : "/bodegas", { method: b ? "PUT" : "POST", body: JSON.stringify(d) });
    mostrarMensaje(b ? "Bodega actualizada" : "Bodega creada"); cargarBodegas();
  });
}
async function eliminar(ruta, nombre, recargar) {
  if (!confirm("¿Eliminar " + nombre + "? Esta acción queda registrada en la auditoría.")) return;
  try { await apiFetch(ruta, { method: "DELETE" }); mostrarMensaje("Eliminado correctamente"); recargar(); } catch (e) { mostrarMensaje(mensajeDeError(e), "error"); }
}

// ---------- Productos ----------
async function cargarProductos(soloBajo) {
  document.getElementById("titulo-productos").textContent = soloBajo ? "Productos con stock bajo (< 10)" : "Productos";
  try { productosCache = await apiFetch(soloBajo ? "/productos/stock-bajo" : "/productos"); } catch (e) { mostrarMensaje(mensajeDeError(e), "error"); }
  pintarProductos();
}
function pintarProductos() {
  const q = document.getElementById("buscar-productos").value.toLowerCase();
  const filas = productosCache.filter((p) => (p.nombre + p.categoria).toLowerCase().includes(q));
  const cols = [
    { t: "Producto", h: (f) => "<strong>" + esc(f.nombre) + "</strong>" }, { t: "Categoría", v: (f) => f.categoria },
    { t: "Stock", h: (f) => '<span class="badge ' + (f.stock < 10 ? "bajo" : "normal") + '">' + esc(f.stock) + " u.</span>" }, { t: "Precio", v: (f) => formatoMoneda(f.precio) }
  ];
  if (esAdmin) cols.push({ t: "", h: (f) => '<button class="btn btn-sec btn-sm" data-e="' + f.id + '">Editar</button> <button class="btn btn-bad btn-sm" data-d="' + f.id + '">Eliminar</button>' });
  const c = document.getElementById("lista-productos");
  c.innerHTML = tabla(cols, filas, "No hay productos que coincidan.");
  c.querySelectorAll("[data-e]").forEach((b) => (b.onclick = () => formProducto(productosCache.find((x) => x.id == b.dataset.e))));
  c.querySelectorAll("[data-d]").forEach((b) => (b.onclick = () => eliminar("/productos/" + b.dataset.d, "este producto", () => cargarProductos(false))));
}
function formProducto(p) {
  modal(p ? "Editar producto" : "Nuevo producto", [{ k: "nombre", l: "Nombre" }, { k: "categoria", l: "Categoría", max: 60 }, { k: "precio", l: "Precio (COP)", t: "number" }], p || {}, async (d) => {
    await apiFetch(p ? "/productos/" + p.id : "/productos", { method: p ? "PUT" : "POST", body: JSON.stringify(d) });
    mostrarMensaje(p ? "Producto actualizado" : "Producto creado"); cargarProductos(false);
  });
}

// ---------- Movimientos ----------
function tablaMovimientos(movs) {
  return tabla([
    { t: "Fecha", v: (f) => formatoFecha(f.fecha) }, { t: "Tipo", h: (f) => badge(f.tipo) }, { t: "Usuario", v: (f) => f.usuario },
    { t: "Origen", v: (f) => f.bodegaOrigen }, { t: "Destino", v: (f) => f.bodegaDestino },
    { t: "Productos", v: (f) => f.detalles.map((d) => d.producto + " x" + d.cantidad).join(", ") }, { t: "Observación", v: (f) => f.observacion }
  ], movs, "Aún no hay movimientos.");
}
const cargarMovimientos = () => cargar("lista-movimientos", () => apiFetch("/movimientos"), tablaMovimientos);
function filtrarMovimientosPorFechas() {
  const d = document.getElementById("fecha-desde").value, h = document.getElementById("fecha-hasta").value;
  if (!d || !h) return mostrarMensaje("Selecciona las fechas Desde y Hasta", "error");
  cargar("lista-movimientos", () => apiFetch("/movimientos/rango?desde=" + encodeURIComponent(d) + "&hasta=" + encodeURIComponent(h)), tablaMovimientos);
}
async function iniciarMovimientos() {
  cargarMovimientos();
  try { [bodegasCache, productosCache] = await Promise.all([apiFetch("/bodegas"), apiFetch("/productos")]); } catch (e) { return mostrarMensaje(mensajeDeError(e), "error"); }
  const op = bodegasCache.map((b) => '<option value="' + esc(b.id) + '">' + esc(b.nombre) + "</option>").join("");
  document.getElementById("mov-origen").innerHTML = document.getElementById("mov-destino").innerHTML = op;
  actualizarCamposBodega();
  document.getElementById("lineas").innerHTML = "";
  agregarLinea();
}
function actualizarCamposBodega() {
  const t = document.getElementById("mov-tipo").value;
  document.getElementById("grupo-origen").classList.toggle("oculto", t === "ENTRADA");
  document.getElementById("grupo-destino").classList.toggle("oculto", t === "SALIDA");
}
function agregarLinea() {
  const op = productosCache.map((p) => '<option value="' + esc(p.id) + '">' + esc(p.nombre) + "</option>").join("");
  const l = document.createElement("div");
  l.className = "linea";
  l.innerHTML = '<select class="linea-producto">' + op + '</select><input class="linea-cantidad" type="number" min="1" value="1"><button type="button" title="Quitar">✕</button>';
  l.querySelector("button").onclick = () => l.remove();
  document.getElementById("lineas").appendChild(l);
}
async function registrarMovimiento(ev) {
  ev.preventDefault();
  const tipo = document.getElementById("mov-tipo").value;
  const cuerpo = {
    tipo, bodegaOrigenId: tipo === "ENTRADA" ? null : Number(document.getElementById("mov-origen").value),
    bodegaDestinoId: tipo === "SALIDA" ? null : Number(document.getElementById("mov-destino").value),
    observacion: document.getElementById("mov-observacion").value.trim() || null,
    detalles: [...document.querySelectorAll("#lineas .linea")].map((l) => ({ productoId: Number(l.querySelector(".linea-producto").value), cantidad: Number(l.querySelector(".linea-cantidad").value) }))
  };
  try {
    await apiFetch("/movimientos", { method: "POST", body: JSON.stringify(cuerpo) });
    mostrarMensaje("Movimiento registrado correctamente");
    document.getElementById("mov-observacion").value = ""; document.getElementById("lineas").innerHTML = ""; agregarLinea(); cargarMovimientos();
  } catch (e) { mostrarMensaje(mensajeDeError(e), "error"); }
}

// ---------- Auditorías ----------
const aObj = (s) => { try { return JSON.parse(s); } catch (e) { return null; } };
function cambios(a) {
  const antes = aObj(a.valoresAnteriores), nuevo = aObj(a.valoresNuevos);
  if (!antes && !nuevo) return "—";
  if (a.operacion === "UPDATE" && antes && nuevo) {
    const l = Object.keys(nuevo).filter((k) => String(antes[k]) !== String(nuevo[k])).map((k) => "<div><em>" + esc(k) + ":</em> <s>" + esc(antes[k]) + "</s> → <b>" + esc(nuevo[k]) + "</b></div>");
    return '<div class="cambio">' + (l.join("") || "Sin cambios de valor") + "</div>";
  }
  const o = nuevo || antes;
  return '<div class="cambio">' + Object.keys(o).map((k) => "<div><em>" + esc(k) + ":</em> " + esc(o[k]) + "</div>").join("") + "</div>";
}
function cargarAuditorias() {
  const p = new URLSearchParams(), u = document.getElementById("aud-usuario").value.trim(), o = document.getElementById("aud-operacion").value;
  if (u) p.set("usuario", u); if (o) p.set("operacion", o);
  return cargar("lista-auditorias", () => apiFetch("/auditorias" + (p.toString() ? "?" + p : "")), (as) => tabla([
    { t: "Fecha y hora", v: (f) => formatoFecha(f.fechaHora) }, { t: "Operación", h: (f) => badge(f.operacion) }, { t: "Usuario", v: (f) => f.usuario },
    { t: "Entidad", v: (f) => f.entidad + " #" + (f.entidadId === null ? "?" : f.entidadId) }, { t: "Detalle del cambio", h: cambios }
  ], as));
}

// ---------- Notificaciones (ADMIN) ----------
// El backend aún no tiene un estado "pendiente de aprobación": las notificaciones se arman con
// /auditorias, /movimientos, /usuarios y /productos/stock-bajo, y lo "visto/aprobado" se guarda en este navegador.
const CLAVE = "logitrack_visto_" + (session ? session.username : "");
let vistos = new Set();
try { vistos = new Set(JSON.parse(localStorage.getItem(CLAVE) || "[]")); } catch (e) {}
function marcarVisto(ids) {
  ids.forEach((i) => vistos.add(i));
  try { localStorage.setItem(CLAVE, JSON.stringify([...vistos].slice(-500))); } catch (e) {}
  pintarNotifs();
}
function iniciarNotificaciones() {
  const panel = document.getElementById("panel-notif");
  document.getElementById("btn-notif").onclick = (e) => { e.stopPropagation(); panel.classList.toggle("oculto"); };
  document.addEventListener("click", (e) => { if (!panel.contains(e.target)) panel.classList.add("oculto"); });
  document.getElementById("btn-aprobar-todo").onclick = () => marcarVisto(notifs.map((n) => n.id));
  document.querySelectorAll("#ntabs button").forEach((b) => (b.onclick = () => {
    filtroNotif = b.dataset.f;
    document.querySelectorAll("#ntabs button").forEach((x) => x.classList.toggle("on", x === b));
    pintarNotifs();
  }));
  cargarNotifs();
  setInterval(cargarNotifs, 45000);
}
async function cargarNotifs() {
  try {
    const [aud, movs, bajo, users] = await Promise.all([apiFetch("/auditorias"), apiFetch("/movimientos"), apiFetch("/productos/stock-bajo"), apiFetch("/usuarios")]);
    const limite = Date.now() - 7 * 864e5, lista = [];
    aud.filter((a) => new Date(a.fechaHora) > limite).forEach((a) => {
      const u = users.find((x) => x.id === a.entidadId);
      if (a.entidad === "Usuario" && a.operacion === "INSERT" && u && u.rol === "EMPLEADO")
        lista.push({ id: "u" + a.id, tipo: "aprobar", ico: "user", titulo: "Cuenta nueva: " + u.nombre, det: "@" + u.username + " se registró como empleado", fecha: a.fechaHora, userId: u.id });
      else if (a.usuario !== session.username && !["Movimiento", "DetalleMovimiento", "Inventario"].includes(a.entidad) && !(a.entidad === "Usuario" && a.operacion === "INSERT"))
        lista.push({ id: "a" + a.id, tipo: "cambio", ico: "edit", titulo: a.entidad + " #" + a.entidadId + ": " + a.operacion, det: "Hecho por " + a.usuario, fecha: a.fechaHora });
    });
    movs.filter((m) => m.usuario !== session.username && new Date(m.fecha) > limite).forEach((m) =>
      lista.push({ id: "m" + m.id, tipo: "aprobar", ico: "swap", titulo: m.tipo + " de " + m.usuario, det: m.detalles.map((d) => d.producto + " x" + d.cantidad).join(", "), fecha: m.fecha }));
    bajo.forEach((p) => lista.push({ id: "s" + p.id + "-" + p.stock, tipo: "alerta", ico: "alert", titulo: "Stock bajo: " + p.nombre, det: "Quedan " + p.stock + " unidades", fecha: new Date().toISOString() }));
    notifs = lista.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
    pintarNotifs();
  } catch (e) { /* si falla, el panel conserva lo último cargado */ }
}
function pintarNotifs() {
  const pend = notifs.filter((n) => !vistos.has(n.id));
  const cuenta = document.getElementById("notif-count");
  cuenta.textContent = pend.length > 99 ? "99+" : pend.length;
  cuenta.classList.toggle("oculto", !pend.length);
  const ver = pend.filter((n) => filtroNotif === "todo" || n.tipo === filtroNotif);
  const l = document.getElementById("nlist");
  l.innerHTML = ver.length ? ver.slice(0, 40).map((n) =>
    '<div class="nitem"><span class="nico ' + n.tipo + '">' + ico(n.ico) + "</span><div><p><strong>" + esc(n.titulo) + "</strong></p><p>" + esc(n.det) + "</p><small>" + (n.tipo === "alerta" ? "Requiere reposición" : formatoFecha(n.fecha)) + '</small><div class="nacts">' +
    (n.tipo === "aprobar" ? '<button class="btn btn-sm" data-ok="' + esc(n.id) + '">Aprobar</button>' : '<button class="btn btn-sec btn-sm" data-ok="' + esc(n.id) + '">Marcar visto</button>') +
    (n.userId ? '<button class="btn btn-sec btn-sm" data-adm="' + n.userId + '" data-nid="' + esc(n.id) + '">Hacer administrador</button>' : "") + "</div></div></div>").join("")
    : '<p class="vacio">Todo al día. No hay notificaciones pendientes.</p>';
  l.querySelectorAll("[data-ok]").forEach((b) => (b.onclick = () => marcarVisto([b.dataset.ok])));
  l.querySelectorAll("[data-adm]").forEach((b) => (b.onclick = async () => {
    if (!confirm("¿Dar rol de administrador a este usuario?")) return;
    try { await apiFetch("/usuarios/" + b.dataset.adm + "/rol", { method: "PUT", body: JSON.stringify({ rol: "ADMIN" }) }); mostrarMensaje("Rol actualizado a ADMIN"); marcarVisto([b.dataset.nid]); } catch (e) { mostrarMensaje(mensajeDeError(e), "error"); }
  }));
}

// Arrancar al final, cuando todas las constantes (esAdmin, ICONOS, CLAVE, vistos...) ya existen
if (!session) { window.location.href = "index.html"; } else { iniciar(); }