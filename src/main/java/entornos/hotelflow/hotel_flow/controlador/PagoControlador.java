package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.Pago;
import entornos.hotelflow.hotel_flow.modelos.PagoRequestDTO;
import entornos.hotelflow.hotel_flow.modelos.Cliente; // Para referencia
import entornos.hotelflow.hotel_flow.modelos.Habitacion; // Para referencia
import entornos.hotelflow.hotel_flow.servicio.IPagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoControlador {

    @Autowired
    private IPagoServicio pagoServicio;

    @PostMapping
    public ResponseEntity<Pago> registrarPago(@RequestBody PagoRequestDTO pagoRequest) {
        try {
            Pago nuevoPago = new Pago();
            Cliente clienteRef = new Cliente();
            clienteRef.setIdCliente(pagoRequest.getIdCliente());
            nuevoPago.setCliente(clienteRef);

            Habitacion habitacionRef = new Habitacion();
            habitacionRef.setNumeroHabitacion(pagoRequest.getNumeroHabitacion());
            nuevoPago.setHabitacion(habitacionRef);
            
            nuevoPago.setMonto(pagoRequest.getMonto());
            nuevoPago.setMetodoPago(pagoRequest.getMetodoPago());
            nuevoPago.setFechaPago(pagoRequest.getFechaPago() != null ? pagoRequest.getFechaPago() : LocalDate.now());

            Pago pagoGuardado = pagoServicio.registrarPago(nuevoPago);
            return new ResponseEntity<>(pagoGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
             if (e.getMessage().contains("no encontrado") || e.getMessage().contains("monto")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar el pago", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarPagos(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer numeroHabitacion,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        if (idCliente != null) {
            return ResponseEntity.ok(pagoServicio.buscarPagosPorCliente(idCliente));
        } else if (numeroHabitacion != null) {
            return ResponseEntity.ok(pagoServicio.buscarPagosPorHabitacion(numeroHabitacion));
        } else if (fecha != null) {
            return ResponseEntity.ok(pagoServicio.buscarPagosPorFecha(fecha));
        }
        return ResponseEntity.ok(pagoServicio.listarTodosLosPagos());
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Integer idPago) {
        return pagoServicio.buscarPagoPorId(idPago)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{idPago}") // O podría ser un PATCH para anular con estado
    public ResponseEntity<Void> anularPago(@PathVariable Integer idPago) {
        try {
            pagoServicio.anularPago(idPago);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}