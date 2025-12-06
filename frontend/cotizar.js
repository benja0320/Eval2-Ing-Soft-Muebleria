// URLs de tu API (Asegúrate que coincidan con tus Controllers)
const URL_MUEBLES_ACTIVOS = 'http://localhost:8080/api/muebles/activos';
const URL_VARIACIONES = 'http://localhost:8080/api/variaciones';
const URL_CREAR_COTIZACION = 'http://localhost:8080/api/cotizaciones';

let mueblesDisponibles = [];
let variacionesDisponibles = [];
let carrito = []; 

document.addEventListener('DOMContentLoaded', iniciarPagina);

async function iniciarPagina() {
    try {
        const [resMuebles, resVariaciones] = await Promise.all([
            fetch(URL_MUEBLES_ACTIVOS),
            fetch(URL_VARIACIONES)
        ]);

        if (resMuebles.ok) {
            mueblesDisponibles = await resMuebles.json();
            llenarSelectMuebles();
        }
        
        if (resVariaciones.ok) {
            variacionesDisponibles = await resVariaciones.json();
            llenarSelectVariaciones();
        }

    } catch (error) {
        console.error("Error cargando datos:", error);
        alert("Error al conectar con el servidor.");
    }
}

function llenarSelectMuebles() {
    const select = document.getElementById('select-mueble');
    select.innerHTML = '<option value="" disabled selected>Seleccione un mueble...</option>';
    
    mueblesDisponibles.forEach(m => {
        const option = document.createElement('option');
        option.value = m.id;
        option.textContent = `${m.nombre} ($${m.precioBase})`;
        select.appendChild(option);
    });
}

function llenarSelectVariaciones() {
    const select = document.getElementById('select-variacion');
    
    variacionesDisponibles.forEach(v => {
        const option = document.createElement('option');
        option.value = v.id;
        option.textContent = `${v.nombre} (+$${v.aumentoPrecio})`;
        select.appendChild(option);
    });
}

window.actualizarPrecioEstimado = function() {
    const idMueble = document.getElementById('select-mueble').value;
    const idVariacion = document.getElementById('select-variacion').value;
    const infoMueble = document.getElementById('info-mueble');
    const infoVariacion = document.getElementById('info-variacion');
    const displayPrecio = document.getElementById('precio-estimado');

    let precioBase = 0;
    let costoExtra = 0;

    const mueble = mueblesDisponibles.find(m => m.id == idMueble);
    if (mueble) {
        precioBase = mueble.precioBase;
        infoMueble.textContent = `Precio Base: $${precioBase}`;
    }

    if (idVariacion) {
        const variacion = variacionesDisponibles.find(v => v.id == idVariacion);
        if (variacion) {
            costoExtra = variacion.aumentoPrecio;
            infoVariacion.textContent = `Costo extra: $${costoExtra}`;
        }
    } else {
        infoVariacion.textContent = `Costo extra: $0`;
    }

    const totalUnitario = precioBase + costoExtra;
    displayPrecio.textContent = `$${totalUnitario}`;
}

document.getElementById('form-agregar-item').addEventListener('submit', function(e) {
    e.preventDefault();

    const idMueble = document.getElementById('select-mueble').value;
    const idVariacion = document.getElementById('select-variacion').value || null; 
    const cantidad = parseInt(document.getElementById('cantidad').value);

    
    if (!idMueble) return alert("Selecciona un mueble");
    if (cantidad < 1) return alert("Cantidad inválida");

    const muebleObj = mueblesDisponibles.find(m => m.id == idMueble);
    const variacionObj = variacionesDisponibles.find(v => v.id == idVariacion);

    const nuevoItem = {
        muebleId: parseInt(idMueble),
        nombreMueble: muebleObj.nombre,
        variacionId: idVariacion ? parseInt(idVariacion) : null,
        nombreVariacion: variacionObj ? variacionObj.nombre : "Estándar",
        cantidad: cantidad
    };

    carrito.push(nuevoItem);
    renderizarCarrito();
    
    document.getElementById('cantidad').value = 1;
    actualizarPrecioEstimado();
});

function renderizarCarrito() {
    const tbody = document.getElementById('tabla-carrito');
    const btnFinalizar = document.getElementById('btn-finalizar');
    const contador = document.getElementById('contador-items');

    tbody.innerHTML = '';
    contador.textContent = `${carrito.length} Ítems`;

    if (carrito.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty-msg">Tu cotización está vacía.</td></tr>';
        btnFinalizar.disabled = true;
        return;
    }

    btnFinalizar.disabled = false;

    carrito.forEach((item, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.nombreMueble}</td>
            <td><span class="badge ${item.variacionId ? 'status-pending' : 'status-done'}">${item.nombreVariacion}</span></td>
            <td>${item.cantidad}</td>
            <td>
                <button class="btn-small btn-danger" onclick="eliminarItem(${index})">X</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

window.eliminarItem = function(index) {
    carrito.splice(index, 1);
    renderizarCarrito();
}

window.enviarCotizacion = async function() {
    if (!confirm("¿Confirmar y enviar esta solicitud de cotización?")) return;

    
    const requestBody = {
        detalles: carrito.map(item => ({
            muebleId: item.muebleId,
            variacionId: item.variacionId,
            cantidad: item.cantidad
        }))
    };

    try {
        const response = await fetch(URL_CREAR_COTIZACION, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            const cotizacionCreada = await response.json();
            alert(`¡Cotización #${cotizacionCreada.id} creada con éxito!\nUn vendedor revisará tu solicitud.`);
            carrito = []; 
            renderizarCarrito();
            window.location.href = 'index.html'; 
        } else {
            alert("Error al crear la cotización.");
            console.error(await response.text());
        }

    } catch (error) {
        console.error("Error de red:", error);
        alert("No se pudo conectar con el servidor.");
    }
}