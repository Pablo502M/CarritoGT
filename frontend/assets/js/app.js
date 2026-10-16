const API = 'http://localhost:8080/api';

// Identificador del visitante (equivale al codUsuario que generaba Auth.php en PHP)
function obtenerCodUsuario() {
    let cod = null;
    try { cod = localStorage.getItem('codUsuario'); } catch (e) { cod = null; }
    if (!cod) {
        cod = (window.crypto && crypto.randomUUID)
            ? crypto.randomUUID()
            : 'u-' + Date.now() + '-' + Math.random().toString(16).slice(2);
        try { localStorage.setItem('codUsuario', cod); } catch (e) { /* sin almacenamiento */ }
    }
    return cod;
}
const codUsuario = obtenerCodUsuario();

// Evita que texto de la base se interprete como HTML
function esc(texto) {
    return String(texto == null ? '' : texto)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

const dinero = (n) => Number(n).toFixed(2);

let temporizador;
function mostrarMensaje(texto, tipo) {
    const el = document.getElementById('mensaje');
    el.textContent = texto;
    el.className = 'mensaje ' + (tipo || 'exito');
    el.style.display = 'block';
    clearTimeout(temporizador);
    temporizador = setTimeout(() => { el.style.display = 'none'; }, 3000);
}

function mostrarVista(nombre) {
    document.getElementById('vista-catalogo').style.display = nombre === 'catalogo' ? '' : 'none';
    document.getElementById('vista-carrito').style.display = nombre === 'carrito' ? '' : 'none';
}

// Petición a la API: devuelve el JSON, o lanza un error con un mensaje claro
async function llamar(ruta, opciones) {
    let resp;
    try {
        resp = await fetch(API + ruta, opciones);
    } catch (e) {
        throw new Error('No se pudo conectar con la API. ¿Está encendido el backend?');
    }
    if (!resp.ok) {
        let mensaje = 'Error ' + resp.status;
        try {
            const cuerpo = await resp.json();
            if (cuerpo.message) mensaje = cuerpo.message;
        } catch (e) { /* la respuesta no trae JSON */ }
        throw new Error(mensaje);
    }
    return resp.status === 204 ? null : resp.json();
}

const rutaCarrito = () => '/carrito/' + encodeURIComponent(codUsuario);

function pintarContador(items) {
    const total = items.reduce((suma, i) => suma + (i.cantidad || 0), 0);
    document.getElementById('contador').textContent = total;
}

async function actualizarContador() {
    try {
        pintarContador(await llamar(rutaCarrito()));
    } catch (e) {
        console.error(e.message);
    }
}

async function cargarProductos() {
    try {
        const productos = await llamar('/productos');
        document.getElementById('lista-productos').innerHTML = productos.map((p) => `
            <div class="producto-item">
                <div class="producto-imagen"><img src="${esc(p.imagen)}" alt="${esc(p.nombre)}"></div>
                <div class="producto-pie-mosaico">
                    <div class="producto-titulo">${esc(p.nombre)}</div>
                    <div class="producto-precio">${dinero(p.precio)}</div>
                    <div class="producto-acciones">
                        <input type="number" class="qty input-grande apa" id="cant-${p.idProducto}"
                               value="1" min="1" size="3" />
                        <button class="ripple" data-agregar="${p.idProducto}">Agregar</button>
                    </div>
                </div>
            </div>`).join('');
    } catch (e) {
        mostrarMensaje('No se pudieron cargar los productos: ' + e.message, 'error');
    }
}

async function agregarAlCarrito(idProducto) {
    const cantidad = parseInt(document.getElementById('cant-' + idProducto).value, 10);
    if (!Number.isInteger(cantidad) || cantidad < 1) {
        mostrarMensaje('La cantidad debe ser un número entero mayor o igual a 1', 'error');
        return;
    }
    try {
        await llamar(rutaCarrito(), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idProducto: Number(idProducto), cantidad: cantidad })
        });
        mostrarMensaje('Producto agregado');
        actualizarContador();
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
}

async function mostrarCarrito() {
    try {
        const items = await llamar(rutaCarrito());
        pintarContador(items);
        const cont = document.getElementById('contenido-carrito');

        if (items.length === 0) {
            cont.innerHTML = `
                <div class="div-tit-vacio apa"><p>Tu carrito de compra está vacío</p></div>
                <div class="body-balloon">
                    <div class="balloon"></div><div class="balloon"></div><div class="balloon"></div>
                    <div class="balloon"></div><div class="balloon"></div>
                </div>`;
        } else {
            const total = items.reduce((suma, i) => suma + Number(i.subTotal), 0);
            cont.innerHTML = `
                <div style="position: absolute; right: 0; top: 0;">
                    <button class="btn-vaciar-carrito apa bor-rad3" data-accion="vaciar">Vaciar carrito</button>
                </div>
                <table class="tbl-agl-v2 table-hover">
                    <thead>
                        <tr>
                            <th></th><th>Nombre</th><th>Código</th><th>Cantidad</th>
                            <th>Precio</th><th>Sub Total</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${items.map((i) => `
                        <tr>
                            <td data-label="Producto"><img src="${esc(i.producto.imagen)}" alt="" /></td>
                            <td data-label="Nombre">${esc(i.producto.nombre)}</td>
                            <td data-label="Código">${esc(i.producto.codigo)}</td>
                            <td data-label="Cantidad">${i.cantidad}</td>
                            <td data-label="Precio">${dinero(i.producto.precio)}</td>
                            <td data-label="Sub Total">${dinero(i.subTotal)}</td>
                            <td><button class="ripple" data-eliminar="${i.idShoppingCart}">Eliminar</button></td>
                        </tr>`).join('')}
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="5"><strong>Total</strong></td>
                            <td colspan="2"><strong>${dinero(total)}</strong></td>
                        </tr>
                    </tfoot>
                </table>`;
        }
        mostrarVista('carrito');
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
}

async function eliminarItem(idShoppingCart) {
    try {
        await llamar('/carrito/item/' + encodeURIComponent(idShoppingCart), { method: 'DELETE' });
        mostrarMensaje('Item eliminado');
        mostrarCarrito();
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
}

async function vaciarCarrito() {
    if (!confirm('¿Vaciar todo el carrito?')) return;
    try {
        await llamar(rutaCarrito(), { method: 'DELETE' });
        mostrarMensaje('Carrito vaciado');
        mostrarCarrito();
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
}

// Un solo "oyente" para los botones que se generan dinámicamente
document.addEventListener('click', (ev) => {
    const el = ev.target.closest('[data-agregar],[data-eliminar],[data-accion]');
    if (!el) return;
    if (el.dataset.agregar) agregarAlCarrito(el.dataset.agregar);
    else if (el.dataset.eliminar) eliminarItem(el.dataset.eliminar);
    else if (el.dataset.accion === 'vaciar') vaciarCarrito();
});

document.getElementById('btn-ver-carrito').addEventListener('click', mostrarCarrito);
document.getElementById('btn-volver').addEventListener('click', () => {
    mostrarVista('catalogo');
    actualizarContador();
});

cargarProductos();
actualizarContador();
