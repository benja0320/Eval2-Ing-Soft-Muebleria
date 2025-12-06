const URL_API = 'http://localhost:8080/api/variaciones';

// cargamos las variaciones al cargar la página
document.addEventListener('DOMContentLoaded', cargarVariaciones);

async function cargarVariaciones() {
    const contenedor = document.getElementById('lista-variaciones');
    
    try {
        const response = await fetch(URL_API);
        const variaciones = await response.json();

        contenedor.innerHTML = ''; 

        if (variaciones.length === 0) {
            contenedor.innerHTML = '<p class="empty-msg">No hay variaciones registradas.</p>';
            return;
        }

        variaciones.forEach(v => {
            // usamos una tarjeta simple para cada item

            const item = document.createElement('div');
            item.className = 'variacion-item';
            item.innerHTML = `
                <div class="v-info">
                    <span class="v-name">${v.nombre}</span>
                    <span class="v-price">+$${v.aumentoPrecio}</span>
                </div>
            `;
            contenedor.appendChild(item);
        });

    } catch (error) {
        console.error('Error:', error);
        contenedor.innerHTML = '<p class="error-msg">Error al cargar datos del servidor.</p>';
    }
}

// guardar nueva variación
document.getElementById('form-variacion').addEventListener('submit', async function(e) {
    e.preventDefault();

    const nuevaVariacion = {
        nombre: document.getElementById('nombre').value,
        aumentoPrecio: parseFloat(document.getElementById('aumento').value)
    };

    try {
        const response = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nuevaVariacion)
        });

        if (response.ok) {
            alert('Variación guardada correctamente');
            document.getElementById('form-variacion').reset(); // limpiar inputs
            cargarVariaciones(); // recargamos la lista para ver el cambio
        } else {
            alert('Error al guardar.');
        }

    } catch (error) {
        console.error(error);
        alert('Error de conexión.');
    }
});