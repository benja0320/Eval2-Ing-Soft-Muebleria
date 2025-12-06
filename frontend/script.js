// referencias a los botones y vistas
const btnVendedor = document.getElementById('btn-vendedor');
const btnCliente = document.getElementById('btn-cliente');
const vistaVendedor = document.getElementById('vista-vendedor');
const vistaCliente = document.getElementById('vista-cliente');

// eventos para los botones
function cambiarModo(modo) {
    if (modo === 'vendedor') {

        btnVendedor.classList.add('active');
        btnCliente.classList.remove('active');

        vistaVendedor.classList.remove('hidden');
        vistaCliente.classList.add('hidden');
        
    } else if (modo === 'cliente') {

        btnCliente.classList.add('active');
        btnVendedor.classList.remove('active');


        vistaCliente.classList.remove('hidden');
        vistaVendedor.classList.add('hidden');
    }
}
//inicializar en modo vendedor
document.addEventListener('DOMContentLoaded', () => {
    cambiarModo('vendedor');
});