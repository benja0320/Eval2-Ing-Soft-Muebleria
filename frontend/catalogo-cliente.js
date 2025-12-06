const URL_PRODUCTOS = 'http://localhost:8080/api/muebles/activos';

let productosGlobal = [];

document.addEventListener('DOMContentLoaded', cargarCatalogo);

async function cargarCatalogo() {
    const contenedor = document.getElementById('grid-productos');

    try {
        const response = await fetch(URL_PRODUCTOS);
        
        if (!response.ok) throw new Error('Error de conexión');
        
        const data = await response.json();
        productosGlobal = data;
        renderizarGrid(data);

    } catch (error) {
        console.error(error);
        contenedor.innerHTML = '<p class="error-msg">No se pudo cargar el catálogo.</p>';
    }
}

function renderizarGrid(lista) {
    const contenedor = document.getElementById('grid-productos');
    contenedor.innerHTML = '';

    if (lista.length === 0) {
        contenedor.innerHTML = '<p class="empty-msg" style="grid-column: 1/-1;">No se encontraron productos.</p>';
        return;
    }

    lista.forEach(prod => {
        const card = document.createElement('div');
        card.className = 'product-card';
        
        const precioFmt = new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(prod.precioBase);

        card.innerHTML = `
            <div class="card-image-placeholder">
                <span>${getIconoPorTipo(prod.tipo)}</span>
            </div>
            <div class="card-content">
                <span class="card-category">${prod.tipo}</span>
                <h3>${prod.nombre}</h3>
                <p class="card-price">${precioFmt}</p>
                <div class="card-footer">
                    <span class="stock-label">Disponibles: ${prod.stock}</span>
                    <button class="btn-cotizar" onclick="window.location.href='cotizar.html'">Cotizar</button>
                </div>
            </div>
        `;
        contenedor.appendChild(card);
    });
}

// implementamos una busqueda en tiempo real
function filtrarCatalogo() {
    const texto = document.getElementById('buscador').value.toLowerCase();
    const filtrados = productosGlobal.filter(p => 
        p.nombre.toLowerCase().includes(texto) || 
        p.tipo.toLowerCase().includes(texto)
    );
    renderizarGrid(filtrados);
}

function getIconoPorTipo(tipo) {
    const t = tipo.toLowerCase();
    if (t.includes('silla')) return '🪑';
    if (t.includes('mesa')) return '🪵';
    if (t.includes('sillon') || t.includes('sofá')) return '🛋️';
    if (t.includes('estante')) return '📚';
    return '📦'; 
}