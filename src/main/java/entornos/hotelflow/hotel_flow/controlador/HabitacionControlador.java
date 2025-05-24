package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;
import entornos.hotelflow.hotel_flow.servicio.IHabitacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "*") // Ajusta en producción
public class HabitacionControlador {

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @PostMapping
    public ResponseEntity<HabitacionDTO> crearHabitacion(@RequestBody Habitacion habitacion) {
        try {
            // El servicio ahora toma la entidad y devuelve DTO
            HabitacionDTO nuevaHabitacionDTO = habitacionServicio.crearHabitacion(habitacion);
            return new ResponseEntity<>(nuevaHabitacionDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<HabitacionDTO>> listarHabitaciones(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        
        Habitacion.TipoHabitacion tipoEnum = null;
        if (tipo != null) {
            try {
                tipoEnum = Habitacion.TipoHabitacion.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de habitación inválido: " + tipo);
            }
        }
        Habitacion.EstadoHabitacion estadoEnum = null;
        if (estado != null) {
             try {
                estadoEnum = Habitacion.EstadoHabitacion.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de habitación inválido: " + estado);
            }
        }
        
        List<HabitacionDTO> habitaciones = habitacionServicio.buscarHabitaciones(tipoEnum, estadoEnum, disponible, precioMin, precioMax);
        return ResponseEntity.ok(habitaciones);
    }
    
    @GetMapping("/disponibles-en-fechas")
    public ResponseEntity<List<HabitacionDTO>> listarHabitacionesDisponiblesParaFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLlegada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam(required = false) String tipo) {
        Habitacion.TipoHabitacion tipoEnum = null;
        if (tipo != null && !tipo.isBlank()) {
            try {
                tipoEnum = Habitacion.TipoHabitacion.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de habitación inválido: " + tipo);
            }
        }
        try {
            List<HabitacionDTO> habitaciones = habitacionServicio.buscarHabitacionesDisponiblesParaFechas(fechaLlegada, fechaSalida, tipoEnum);
            return ResponseEntity.ok(habitaciones);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping("/{numeroHabitacion}")
    public ResponseEntity<HabitacionDTO> obtenerHabitacionPorNumero(@PathVariable Integer numeroHabitacion) {
        return habitacionServicio.obtenerHabitacionDTOPorNumero(numeroHabitacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{numeroHabitacion}")
    public ResponseEntity<HabitacionDTO> actualizarHabitacion(@PathVariable Integer numeroHabitacion, @RequestBody Habitacion habitacionDetalles) {
        // El servicio toma la entidad y devuelve DTO
        try {
            HabitacionDTO habitacionActualizadaDTO = habitacionServicio.actualizarHabitacion(numeroHabitacion, habitacionDetalles);
            return ResponseEntity.ok(habitacionActualizadaDTO);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PatchMapping("/{numeroHabitacion}/estado")
    public ResponseEntity<HabitacionDTO> actualizarEstadoHabitacion(@PathVariable Integer numeroHabitacion, @RequestBody Map<String, String> payload) {
        String nuevoEstadoStr = payload.get("estado");
        if (nuevoEstadoStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo debe contener 'estado'.");
        }
        try {
            Habitacion.EstadoHabitacion nuevoEstado = Habitacion.EstadoHabitacion.valueOf(nuevoEstadoStr.toUpperCase());
            HabitacionDTO actualizada = habitacionServicio.actualizarEstadoHabitacion(numeroHabitacion, nuevoEstado);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido: " + nuevoEstadoStr);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{numeroHabitacion}/precio")
    public ResponseEntity<HabitacionDTO> actualizarPrecioHabitacion(@PathVariable Integer numeroHabitacion, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal nuevoPrecio = payload.get("precio");
        if (nuevoPrecio == null) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo debe contener 'precio'.");
        }
        try {
            HabitacionDTO actualizada = habitacionServicio.actualizarPrecioHabitacion(numeroHabitacion, nuevoPrecio);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{numeroHabitacion}")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Integer numeroHabitacion) {
        try {
            habitacionServicio.eliminarHabitacion(numeroHabitacion);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } 
        catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}