package entornos.hotelflow.hotel_flow.controlador;

import entornos.hotelflow.hotel_flow.modelos.AsignacionHabitacionRequest;
import entornos.hotelflow.hotel_flow.modelos.Cliente;
import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.servicio.IClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // Considera orígenes específicos en producción
public class ClienteControlador {

    @Autowired
    private IClienteServicio clienteServicio;

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteServicio.guardarCliente(cliente);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el cliente", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
            @RequestParam(required = false) String apellidos) {
        List<Cliente> clientes;
        if (apellidos != null && !apellidos.isEmpty()) {
            clientes = clienteServicio.buscarPorApellidos(apellidos);
        } else {
            clientes = clienteServicio.listarTodosLosClientes();
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Integer idCliente) {
        return clienteServicio.buscarClientePorId(idCliente)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Cliente> obtenerClientePorCedula(@PathVariable String cedula) {
        return clienteServicio.buscarClientePorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Integer idCliente, @RequestBody Cliente clienteDetalles) {
        try {
            Cliente clienteActualizado = clienteServicio.actualizarCliente(idCliente, clienteDetalles);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) { // Ser más específico con las excepciones
             if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer idCliente) {
        try {
            clienteServicio.eliminarCliente(idCliente);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{idCliente}/asignar-habitacion")
public ResponseEntity<?> asignarHabitacion(
        @PathVariable Integer idCliente,
        @RequestBody AsignacionHabitacionRequest request
) {
    Cliente cliente = clienteServicio.buscarClientePorId(idCliente)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

    Habitacion habitacion = new Habitacion();
    habitacion.setNumeroHabitacion(request.getNumeroHabitacion());

    cliente.setHabitacion(habitacion); // <- AQUÍ está la relación
    clienteServicio.actualizarCliente(idCliente, cliente);

    return ResponseEntity.ok("Habitación asignada correctamente");
}


}

