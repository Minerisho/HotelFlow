package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;
import entornos.hotelflow.hotel_flow.repositorio.IHabitacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionControlador {
    
    @Autowired
    private IHabitacionServicio habitacionServicio;
    
    @GetMapping
    public ResponseEntity<List<HabitacionDTO>> listarTodas() {
        return ResponseEntity.ok(habitacionServicio.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HabitacionDTO> obtenerPorId(@PathVariable Integer id) {
        HabitacionDTO habitacion = habitacionServicio.buscarPorId(id);
        if (habitacion != null) {
            return ResponseEntity.ok(habitacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/numero/{numero}")
    public ResponseEntity<HabitacionDTO> obtenerPorNumero(@PathVariable String numero) {
        HabitacionDTO habitacion = habitacionServicio.buscarPorNumero(numero);
        if (habitacion != null) {
            return ResponseEntity.ok(habitacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HabitacionDTO>> buscarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(habitacionServicio.buscarPorEstado(estado));
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<HabitacionDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(habitacionServicio.buscarPorTipo(tipo));
    }
    
    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<HabitacionDTO>> buscarPorCapacidadMinima(@PathVariable Integer capacidad) {
        return ResponseEntity.ok(habitacionServicio.buscarPorCapacidadMinima(capacidad));
    }
    
    @GetMapping("/tarifa/{tarifaMaxima}")
    public ResponseEntity<List<HabitacionDTO>> buscarPorTarifaMaxima(@PathVariable BigDecimal tarifaMaxima) {
        return ResponseEntity.ok(habitacionServicio.buscarPorTarifaMaxima(tarifaMaxima));
    }
    
    @GetMapping("/filtrar")
    public ResponseEntity<List<HabitacionDTO>> buscarPorEstadoYTipo(
            @RequestParam String estado, 
            @RequestParam String tipo) {
        return ResponseEntity.ok(habitacionServicio.buscarPorEstadoYTipo(estado, tipo));
    }
    
    @PostMapping
    public ResponseEntity<HabitacionDTO> crear(@RequestBody HabitacionDTO habitacionDTO) {
        try {
            HabitacionDTO nuevaHabitacion = habitacionServicio.guardar(habitacionDTO);
            return new ResponseEntity<>(nuevaHabitacion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionDTO> actualizar(
            @PathVariable Integer id, 
            @RequestBody HabitacionDTO habitacionDTO) {
        try {
            HabitacionDTO actualizada = habitacionServicio.actualizar(id, habitacionDTO);
            if (actualizada != null) {
                return ResponseEntity.ok(actualizada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (habitacionServicio.eliminar(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> estadoMap) {
        String nuevoEstado = estadoMap.get("estado");
        if (nuevoEstado == null) {
            return ResponseEntity.badRequest().body("El campo 'estado' es requerido");
        }
        
        if (habitacionServicio.cambiarEstado(id, nuevoEstado)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}