const URL_COTIZACIONES = 'http://localhost:8080/api/cotizaciones';
const URL_VENTAS = 'http://localhost:8080/api/ventas/confirmar';

document.addEventListener('DOMContentLoaded', cargarCotizaciones);

async function cargarCotizaciones() {
    const tbody = document.getElementById('tabla-cotizaciones');
    tbody.innerHTML = '<tr><td colspan="5" class="loading-text">Consultando sistema...</td></tr>';

    try {
        const response = await fetch(URL_COTIZACIONES);
        
        if (!response.ok) throw new Error('Error al conectar con el servidor');
        
        const data = await response.json();
        tbody.innerHTML = ''; // Limpiar

        

        if (data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-msg">No hay cotizaciones registradas.</td></tr>';
            return;
        }

        data.forEach(cot => {
            const fila = document.createElement('tr');
            
            // Determinar estilo del estado
            const estadoClass = cot.estado === 'PENDIENTE' ? 'status-pending' : 'status-done';
            const btnDisabled = cot.estado !== 'PENDIENTE' ? 'disabled' : '';
            const btnText = cot.estado === 'PENDIENTE' ? 'Confirmar Venta' : 'Ya Procesada';
            const btnAction = cot.estado === 'PENDIENTE' ? `onclick="procesarVenta(${cot.id})"` : '';

            fila.innerHTML = `
                <td>#${cot.id}</td>
                <td>${new Date(cot.fecha).toLocaleDateString()}</td>
                <td><span class="badge ${estadoClass}">${cot.estado}</span></td>
                <td>${cot.detalles ? cot.detalles.length : 0} muebles</td>
                <td>
                    <button class="btn-small ${btnDisabled}" ${btnAction}>
                        ${btnText}
                    </button>
                </td>
            `;
            tbody.appendChild(fila);
        });

    } catch (error) {
        console.error(error);
        tbody.innerHTML = '<tr><td colspan="5" class="error-msg">Error al cargar datos. ¿El backend está corriendo?</td></tr>';
    }
}

async function procesarVenta(id) {
    if (!confirm(`¿Estás seguro de confirmar la cotización #${id} como venta final? Esto descontará stock.`)) {
        return;
    }

    try {
        const response = await fetch(`${URL_VENTAS}/${id}`, {
            method: 'POST'
        });

        if (response.ok) {
            alert('¡Venta confirmada exitosamente! El stock ha sido actualizado.');
            cargarCotizaciones(); // Recargar tabla
        } else {
            // Capturamos el mensaje de error del backend (ej: Stock insuficiente)
            const errorMsg = await response.text();
            alert('No se pudo confirmar la venta:\n' + errorMsg);
        }

    } catch (error) {
        console.error(error);
        alert('Error de conexión al intentar confirmar.');
    }
}