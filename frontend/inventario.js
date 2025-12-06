
const URL_MUEBLES = 'http://localhost:8080/api/muebles'; 

document.addEventListener('DOMContentLoaded', cargarInventario);

let mueblesGlobal = []; 

async function cargarInventario() {
    const tbody = document.getElementById('tabla-muebles');
    
    try {
        const response = await fetch(URL_MUEBLES);
        if (!response.ok) throw new Error('Error de conexión');
        
        const data = await response.json();
        mueblesGlobal = data; 
        renderizarTabla(data);

    } catch (error) {
        console.error(error);
        tbody.innerHTML = '<tr><td colspan="7" class="error-msg">No se pudo cargar el inventario.</td></tr>';
    }
}

function renderizarTabla(listaMuebles) {
    const tbody = document.getElementById('tabla-muebles');
    tbody.innerHTML = '';

    if (listaMuebles.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-msg">No se encontraron muebles.</td></tr>';
        return;
    }

    listaMuebles.forEach(mueble => {
        const tr = document.createElement('tr');
        
        const estadoClass = mueble.estado === 'ACTIVO' ? 'status-done' : 'status-pending'; 
        const estiloStock = mueble.stock < 3 ? 'color: #ef4444; font-weight: bold;' : ''; 

        
        let accionBtn = '';
        if (mueble.estado === 'ACTIVO') {
            accionBtn = `<button class="btn-small btn-danger" onclick="desactivarMueble(${mueble.id})">Desactivar</button>`;
        } else {
            accionBtn = `<span style="color: #9ca3af; font-size: 0.9rem;">Deshabilitado</span>`;
        }

        tr.innerHTML = `
            <td>#${mueble.id}</td>
            <td style="font-weight: 600;">${mueble.nombre}</td>
            <td>${mueble.tipo}</td>
            <td>$${mueble.precioBase.toLocaleString()}</td>
            <td style="${estiloStock}">${mueble.stock} u.</td>
            <td><span class="badge ${estadoClass}">${mueble.estado}</span></td>
            <td>${accionBtn}</td>
        `;
        tbody.appendChild(tr);
    });
}


function filtrarTabla() {
    const texto = document.getElementById('buscador').value.toLowerCase();
    const filtrados = mueblesGlobal.filter(m => 
        m.nombre.toLowerCase().includes(texto) || 
        m.tipo.toLowerCase().includes(texto)
    );
    renderizarTabla(filtrados);
}


async function desactivarMueble(id) {
    if (!confirm(`¿Estás seguro de que quieres desactivar el mueble #${id}? Ya no aparecerá para cotizaciones.`)) {
        return;
    }

    try {
        
        const response = await fetch(`${URL_MUEBLES}/${id}/desactivar`, {
            method: 'PATCH'
        });

        if (response.ok) {
            alert('Mueble desactivado correctamente.');
            cargarInventario(); 
        } else {
            alert('Error al desactivar el mueble.');
        }

    } catch (error) {
        console.error(error);
        alert('Error de red.');
    }
}