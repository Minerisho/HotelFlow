package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.Reserva;
import entornos.hotelflow.hotel_flow.modelos.ReservaDTO;
import entornos.hotelflow.hotel_flow.modelos.ReservaRequestDTO;
import entornos.hotelflow.hotel_flow.servicio.IReservaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // Para el PATCH de estado

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*") // Ajusta en producción
public class ReservaControlador {

    @Autowired
    private IReservaServicio reservaServicio;

    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(@RequestBody ReservaRequestDTO reservaRequestDTO) {
        try {
            ReservaDTO nuevaReserva = reservaServicio.crearReserva(reservaRequestDTO);
            return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer numeroHabitacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicioCreacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinCreacion
            // Podrías añadir filtros por fechas de estadía si el servicio lo soporta bien
    ) {
        if (idCliente != null) {
            return ResponseEntity.ok(reservaServicio.obtenerReservasPorCliente(idCliente));
        }
        if (numeroHabitacion != null) {
            return ResponseEntity.ok(reservaServicio.obtenerReservasPorHabitacion(numeroHabitacion));
        }
        if (fechaInicioCreacion != null && fechaFinCreacion != null) {
            return ResponseEntity.ok(reservaServicio.obtenerReservasPorRangoDeFechaCreacion(fechaInicioCreacion, fechaFinCreacion));
        }
        return ResponseEntity.ok(reservaServicio.obtenerTodasLasReservas());
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDTO> obtenerReservaPorId(@PathVariable Integer idReserva) {
        return reservaServicio.obtenerReservaDTOPorId(idReserva)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/habitacion/{numeroHabitacion}/activas-en-fechas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasActivasParaHabitacionEnFechas(
            @PathVariable Integer numeroHabitacion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida) {
        try {
            List<ReservaDTO> reservas = reservaServicio.obtenerReservasActivasParaHabitacionEnFechas(numeroHabitacion, fechaLlegada, fechaSalida);
            return ResponseEntity.ok(reservas);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PutMapping("/{idReserva}")
    public ResponseEntity<ReservaDTO> actualizarReserva(@PathVariable Integer idReserva, @RequestBody ReservaRequestDTO reservaRequestDTO) {
        try {
            ReservaDTO reservaActualizada = reservaServicio.actualizarReserva(idReserva, reservaRequestDTO);
            return ResponseEntity.ok(reservaActualizada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) { // Captura genérica para "no encontrada"
            if (e.getMessage().toLowerCase().contains("no encontrada")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar reserva.", e);
        }
    }

    @PatchMapping("/{idReserva}/estado")
    public ResponseEntity<ReservaDTO> actualizarEstadoReserva(@PathVariable Integer idReserva, @RequestBody Map<String, String> payload) {
        String nuevoEstadoStr = payload.get("estado");
        String nuevoTipoPagoStr = payload.get("tipoPago"); // Opcional, solo si se paga

        if (nuevoEstadoStr == null || nuevoEstadoStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo debe contener 'estado'.");
        }

        try {
            Reserva.EstadoReserva nuevoEstado = Reserva.EstadoReserva.valueOf(nuevoEstadoStr.toUpperCase());
            
            Reserva.TipoPagoReserva tipoPagoEnum = null; // Variable para el Enum
            if (nuevoTipoPagoStr != null && !nuevoTipoPagoStr.isBlank()) {
                try {
                    tipoPagoEnum = Reserva.TipoPagoReserva.valueOf(nuevoTipoPagoStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de pago inválido: " + nuevoTipoPagoStr);
                }
            }
            
            // Ahora llamas al servicio con el tipo Enum correcto (o null si no se proporcionó)
            ReservaDTO reservaActualizada = reservaServicio.cambiarEstadoReserva(idReserva, nuevoEstado, tipoPagoEnum);
            
            return ResponseEntity.ok(reservaActualizada);
        } catch (IllegalArgumentException e) { // Captura la conversión de EstadoReserva y errores del servicio
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) { // Captura otras excepciones del servicio (ej. "no encontrada")
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("no encontrada")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            // Log el error e para depuración si es necesario
            // logger.error("Error al cambiar estado de reserva: {}", idReserva, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al cambiar estado de reserva.", e);
        }
    }

    @DeleteMapping("/{idReserva}") // Esto ahora llama a cancelarReserva
    public ResponseEntity<Void> cancelarReserva(@PathVariable Integer idReserva) {
        try {
            reservaServicio.cancelarReserva(idReserva); // Cambia estado a CANCELADA
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
             if (e.getMessage().toLowerCase().contains("no encontrada")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al cancelar reserva.", e);
        }
    }
}