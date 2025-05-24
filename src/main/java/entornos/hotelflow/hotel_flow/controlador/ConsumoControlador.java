package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.Consumo;
import entornos.hotelflow.hotel_flow.modelos.ConsumoRequestDTO;
import entornos.hotelflow.hotel_flow.modelos.Cliente; // Para construir el consumo
import entornos.hotelflow.hotel_flow.modelos.Inventario; // Para construir el consumo
import entornos.hotelflow.hotel_flow.servicio.IConsumoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consumos")
@CrossOrigin(origins = "*")
public class ConsumoControlador {

    @Autowired
    private IConsumoServicio consumoServicio;

    @PostMapping
    public ResponseEntity<Consumo> registrarConsumo(@RequestBody ConsumoRequestDTO consumoRequest) {
        try {
            Consumo nuevoConsumo = new Consumo();
            Cliente clienteRef = new Cliente();
            clienteRef.setIdCliente(consumoRequest.getIdCliente());
            nuevoConsumo.setCliente(clienteRef);

            Inventario productoRef = new Inventario();
            productoRef.setIdProducto(consumoRequest.getIdProducto());
            nuevoConsumo.setProducto(productoRef);
            
            nuevoConsumo.setCantidad(consumoRequest.getCantidad());
            nuevoConsumo.setFechaConsumo(consumoRequest.getFechaConsumo() != null ? consumoRequest.getFechaConsumo() : LocalDate.now());
            nuevoConsumo.setCargadoAHabitacion(consumoRequest.getCargadoAHabitacion());

            Consumo consumoGuardado = consumoServicio.registrarConsumo(nuevoConsumo);
            return new ResponseEntity<>(consumoGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
             if (e.getMessage().contains("no encontrado") || e.getMessage().contains("No hay suficiente stock")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar consumo", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Consumo>> listarConsumos(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        if (idCliente != null) {
            return ResponseEntity.ok(consumoServicio.buscarConsumosPorCliente(idCliente));
        } else if (idProducto != null) {
            return ResponseEntity.ok(consumoServicio.buscarConsumosPorProducto(idProducto));
        } else if (fecha != null) {
            return ResponseEntity.ok(consumoServicio.buscarConsumosPorFecha(fecha));
        }
        return ResponseEntity.ok(consumoServicio.listarTodosLosConsumos());
    }
    
    @GetMapping("/{idConsumo}")
    public ResponseEntity<Consumo> obtenerConsumoPorId(@PathVariable Integer idConsumo) {
        return consumoServicio.buscarConsumoPorId(idConsumo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}/pendientes")
    public ResponseEntity<List<Consumo>> obtenerConsumosPendientesPorCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(consumoServicio.buscarConsumosPendientesPorCliente(idCliente));
    }
    
    @PatchMapping("/{idConsumo}/cargar")
    public ResponseEntity<Consumo> marcarConsumoComoCargado(@PathVariable Integer idConsumo) {
        try {
            Consumo consumo = consumoServicio.marcarConsumoComoCargado(idConsumo);
            return ResponseEntity.ok(consumo);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{idConsumo}")
    public ResponseEntity<Void> eliminarConsumo(@PathVariable Integer idConsumo) {
        try {
            consumoServicio.eliminarConsumo(idConsumo);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}