package com.example.muebleria.service;

import com.example.muebleria.exception.StockInsuficienteException;
import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.model.CotizacionDetalle;
import com.example.muebleria.model.Mueble;
import com.example.muebleria.model.enums.EstadoCotizacion;
import com.example.muebleria.repository.CotizacionRepository;
import com.example.muebleria.repository.MuebleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final CotizacionRepository cotizacionRepository;
    private final MuebleRepository muebleRepository;



     // confirma una cotizacion como venta, descontando el stock
    @Transactional // ¡La anotación más importante!
    public Cotizacion confirmarVenta(Long cotizacionId) {

        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada: " + cotizacionId));

 
        if (cotizacion.getEstado() == EstadoCotizacion.VENDIDA) {
            throw new IllegalStateException("La cotización " + cotizacionId + " ya fue confirmada como venta.");
        }

        List<CotizacionDetalle> detalles = cotizacion.getDetalles();

        // Si falta stock en algún mueble, lanzamos excepción y se hace ROLLBACK.
        for (CotizacionDetalle detalle : detalles) {
            Mueble mueble = detalle.getMueble();
            int cantidadPedida = detalle.getCantidad();

            //Verifica el stock
            if (mueble.getStock() < cantidadPedida) {
                

                throw new StockInsuficienteException(
                        "Stock insuficiente para: " + mueble.getNombre() + 
                        ". Stock actual: " + mueble.getStock() + 
                        ", Cantidad pedida: " + cantidadPedida
                );
            }
        }

        // si llegamos hata aqui, entonces hay stock
        for (CotizacionDetalle detalle : detalles) {
            Mueble mueble = detalle.getMueble();
            int cantidadPedida = detalle.getCantidad();

            // descuenta el stock
            mueble.setStock(mueble.getStock() - cantidadPedida);
            
            // guardar el estado actualizado del mueble
            muebleRepository.save(mueble);
        }

        // se marcar la cotización como VENDIDA
        cotizacion.setEstado(EstadoCotizacion.VENDIDA);
        return cotizacionRepository.save(cotizacion);
    }
}