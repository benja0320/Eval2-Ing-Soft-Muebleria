
const URL_API = 'http://localhost:8080/api/muebles';

document.getElementById('form-crear-mueble').addEventListener('submit', async function(event) {
    event.preventDefault(); 

    //capturamos los datos del formulario
    const formData = {
        nombre: document.getElementById('nombre').value,
        tipo: document.getElementById('tipo').value,
        material: document.getElementById('material').value,
        tamano: document.getElementById('tamano').value,
        precioBase: parseFloat(document.getElementById('precio').value),
        stock: parseInt(document.getElementById('stock').value),
        estado: "ACTIVO" 
    };

    try {
        // se enviar petición POST al Backend
        const response = await fetch(URL_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        // se maneja la respuesta
        if (response.ok) {
            const muebleCreado = await response.json();
            alert(`¡Éxito! Mueble "${muebleCreado.nombre}" registrado con ID: ${muebleCreado.id}`);
            window.location.href = 'index.html'; // Volver al menú
        } else {
            alert('Error al guardar. Verifica que el servidor esté corriendo.');
            console.error('Error:', response.status);
        }

    } catch (error) {
        console.error('Error de red:', error);
        alert('No se pudo conectar con el servidor. ¿Está corriendo el Backend en el puerto 8080?');
    }
});