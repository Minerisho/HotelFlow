package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.Inventario;
import entornos.hotelflow.hotel_flow.servicio.IInventarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map; // Para el PATCH

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioControlador {

    @Autowired
    private IInventarioServicio inventarioServicio;

    @PostMapping
    public ResponseEntity<Inventario> crearProducto(@RequestBody Inventario producto) {
        try {
            Inventario nuevoProducto = inventarioServicio.guardarProducto(producto);
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listarInventario() {
        return ResponseEntity.ok(inventarioServicio.listarTodoElInventario());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<Inventario> obtenerProductoPorId(@PathVariable Integer idProducto) {
        return inventarioServicio.buscarProductoPorId(idProducto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Inventario> obtenerProductoPorNombre(@PathVariable String nombre) {
        return inventarioServicio.buscarProductoPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<Inventario> actualizarProducto(@PathVariable Integer idProducto, @RequestBody Inventario productoDetalles) {
        try {
            Inventario productoActualizado = inventarioServicio.actualizarProducto(idProducto, productoDetalles);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PatchMapping("/{idProducto}/ajustar-cantidad")
    public ResponseEntity<Inventario> ajustarCantidad(@PathVariable Integer idProducto, @RequestBody Map<String, Integer> payload) {
        Integer ajuste = payload.get("ajuste");
        if (ajuste == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo debe contener la clave 'ajuste' con un valor numérico.");
        }
        try {
            Inventario productoActualizado = inventarioServicio.ajustarCantidadProducto(idProducto, ajuste);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
             if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer idProducto) {
        try {
            inventarioServicio.eliminarProducto(idProducto);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}