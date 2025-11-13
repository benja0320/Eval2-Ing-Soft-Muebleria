package com.example.muebleria;

import com.example.muebleria.dto.CotizacionDetalleDTO;
import com.example.muebleria.dto.CotizacionRequestDTO;
import com.example.muebleria.model.Cotizacion;
import com.example.muebleria.model.Mueble;
// ... (imports de Mueble, Cotizacion, etc.)
import com.example.muebleria.model.Variacion; // <-- Importar Variacion
import com.example.muebleria.model.enums.EstadoMueble;
import com.example.muebleria.model.enums.TamanoMueble;
import com.example.muebleria.service.CotizacionService;
import com.example.muebleria.service.MuebleService;
import com.example.muebleria.service.VariacionService; // <-- Importar el nuevo servicio
import com.example.muebleria.service.VentaService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class MenuRunner implements CommandLineRunner {

    // 1. Inyectamos TODOS los servicios
    private final MuebleService muebleService;
    private final CotizacionService cotizacionService;
    private final VentaService ventaService;
    private final VariacionService variacionService; // <-- NUEVO SERVICIO INYECTADO

    // Constructor actualizado
    public MenuRunner(MuebleService muebleService, CotizacionService cotizacionService,
                      VentaService ventaService, VariacionService variacionService) {
        this.muebleService = muebleService;
        this.cotizacionService = cotizacionService;
        this.ventaService = ventaService;
        this.variacionService = variacionService; // <-- ASIGNACIÓN
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("   BIENVENIDO A MUEBLERÍA HNOS. S.A. (CLI)  ");
        System.out.println("==========================================");
        System.out.println("INFO: La API REST está corriendo en http://localhost:8080");

        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        listarMueblesActivos();
                        pausa(scanner);
                        break;
                    case 2:
                        crearMueble(scanner);
                        break;
                    case 3:
                        crearCotizacion(scanner); // <-- ESTE MÉTODO FUE MODIFICADO
                        break;
                    case 4:
                        confirmarVenta(scanner);
                        break;
                    case 5:
                        gestionarVariaciones(scanner); // <-- NUEVA OPCIÓN
                        break;
                    case 6: // <-- Opción de salir actualizada
                        salir = true;
                        System.out.println("Gracias por usar el sistema. Saliendo...");
                        break;
                    default:
                        System.err.println("Opción no válida. Intente de nuevo.");
                        pausa(scanner);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe ingresar un número.");
                pausa(scanner);
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                pausa(scanner);
            }
        }
        
        scanner.close();
        System.exit(0); 
    }

    // --- MENÚS ---

    private void mostrarMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Listar muebles activos");
        System.out.println("2. Crear nuevo mueble");
        System.out.println("3. Crear cotización");
        System.out.println("4. Confirmar venta (por ID de cotización)");
        System.out.println("5. Gestionar Variaciones"); // <-- NUEVO
        System.out.println("6. Salir"); // <-- NUEVO
        System.out.print("Seleccione una opción: ");
    }

    private void gestionarVariaciones(Scanner scanner) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestión de Variaciones ---");
            System.out.println("1. Listar todas las variaciones");
            System.out.println("2. Registrar nueva variación");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        listarVariaciones();
                        pausa(scanner);
                        break;
                    case 2:
                        crearVariacion(scanner);
                        break;
                    case 3:
                        volver = true;
                        break;
                    default:
                        System.err.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // --- MÉTODOS DE LÓGICA (ACTUALIZADOS) ---

    private void crearCotizacion(Scanner scanner) {
        System.out.println("\n--- Crear Nueva Cotización ---");
        listarMueblesActivos(); // Muestra muebles
        
        List<CotizacionDetalleDTO> detallesDto = new ArrayList<>();
        boolean agregando = true;

        while (agregando) {
            System.out.print("Ingrese ID del Mueble a cotizar (o 'fin' para terminar): ");
            String idMuebleStr = scanner.nextLine();
            if (idMuebleStr.equalsIgnoreCase("fin")) {
                agregando = false;
                continue;
            }

            CotizacionDetalleDTO detalle = new CotizacionDetalleDTO();
            detalle.setMuebleId(Long.parseLong(idMuebleStr));

            System.out.print("Cantidad: ");
            detalle.setCantidad(Integer.parseInt(scanner.nextLine()));

            // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
            System.out.print("¿Desea agregar una variación a este mueble? (s/n): ");
            String usaVariacion = scanner.nextLine();
            
            if (usaVariacion.equalsIgnoreCase("s")) {
                System.out.println("--- Variaciones Disponibles ---");
                listarVariaciones(); // Muestra las variaciones
                System.out.print("Ingrese el ID de la Variación: ");
                detalle.setVariacionId(Long.parseLong(scanner.nextLine()));
            } else {
                // Si dice "n" o cualquier otra cosa, es "normal" (precio base)
                detalle.setVariacionId(null); 
                System.out.println("...Mueble agregado como 'normal' (precio base).");
            }
            
            detallesDto.add(detalle);
            System.out.println("...Mueble (ID: " + idMuebleStr + ") agregado a la cotización.");
        }

        if (detallesDto.isEmpty()) {
            System.out.println("Cotización cancelada.");
            return;
        }

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setDetalles(detallesDto);

        var cotizacionGuardada = cotizacionService.crearCotizacion(request);
        System.out.println("¡Cotización creada con éxito! ID: " + cotizacionGuardada.getId());
    }

    // --- NUEVOS MÉTODOS AUXILIARES ---

    private void listarVariaciones() {
        List<Variacion> variaciones = variacionService.listarVariaciones();
        if (variaciones.isEmpty()) {
            System.out.println("No hay variaciones registradas.");
            return;
        }
        variaciones.forEach(v -> {
            System.out.printf("ID: %d | Nombre: %s | Aumento de Precio: $%.0f\n",
                    v.getId(),
                    v.getNombre(),
                    v.getAumentoPrecio().doubleValue());
        });
    }

    private void crearVariacion(Scanner scanner) {
        System.out.println("\n--- Registrar Nueva Variación ---");
        Variacion v = new Variacion();
        
        System.out.print("Nombre (ej. Barniz Premium): ");
        v.setNombre(scanner.nextLine());
        
        System.out.print("Aumento de Precio (ej. 15000): ");
        v.setAumentoPrecio(new BigDecimal(scanner.nextLine()));
        
        Variacion variacionGuardada = variacionService.crearVariacion(v);
        System.out.println("¡Variación registrada con éxito! ID: " + variacionGuardada.getId());
    }

    // --- MÉTODOS SIN CAMBIOS (PERO NECESARIOS) ---
    
    private void pausa(Scanner scanner) {
        System.out.println("\n(Presione Enter para continuar...)");
        scanner.nextLine();
    }
    
    private void listarMueblesActivos() {
        System.out.println("\n--- Listado de Muebles Activos ---");
        List<Mueble> muebles = muebleService.listarMueblesActivos();
        if (muebles.isEmpty()) {
            System.out.println("No hay muebles activos en el catálogo.");
            return;
        }
        muebles.forEach(mueble -> {
            System.out.printf("ID: %d | Nombre: %s | Precio: $%.0f | Stock: %d\n",
                    mueble.getId(),
                    mueble.getNombre(),
                    mueble.getPrecioBase().doubleValue(),
                    mueble.getStock());
        });
    }

    private void crearMueble(Scanner scanner) {
        System.out.println("\n--- Crear Nuevo Mueble ---");
        Mueble mueble = new Mueble();
        
        System.out.print("Nombre: ");
        mueble.setNombre(scanner.nextLine());
        
        System.out.print("Tipo (ej. Silla, Mesa): ");
        mueble.setTipo(scanner.nextLine());
        
        System.out.print("Precio Base (ej. 50000): ");
        mueble.setPrecioBase(new BigDecimal(scanner.nextLine()));
        
        System.out.print("Stock inicial: ");
        mueble.setStock(Integer.parseInt(scanner.nextLine()));
        
        mueble.setTamano(TamanoMueble.MEDIANO);
        mueble.setEstado(EstadoMueble.ACTIVO);
        
        Mueble muebleGuardado = muebleService.crearMueble(mueble);
        System.out.println("¡Mueble creado con éxito! ID: " + muebleGuardado.getId());
    }

    private void confirmarVenta(Scanner scanner) {
        System.out.println("\n--- Confirmar Venta ---");
        System.out.print("Ingrese el ID de la cotización a confirmar: ");
        Long cotizacionId = Long.parseLong(scanner.nextLine());
        
        var cotizacionVendida = ventaService.confirmarVenta(cotizacionId);
        
        System.out.println("¡Venta confirmada!");
        System.out.println("Cotización ID " + cotizacionVendida.getId() + " ahora está " + cotizacionVendida.getEstado());
    }
}