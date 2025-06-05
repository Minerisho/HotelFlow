package entornos.hotelflow.hotel_flow.controlador;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entornos.hotelflow.hotel_flow.modelos.Usuario;
import entornos.hotelflow.hotel_flow.servicio.IUsuarioServicio; // Usar la interfaz

@RestController
@RequestMapping("/api/usuarios") // Ruta base consistente con el PDF
public class UsuarioControlador {
    
    @Autowired
    private IUsuarioServicio usuarioService; // Inyectar la interfaz
    
    // Si mantenemos /list, sería /api/usuarios/list
    @GetMapping // Cambiado de "/list" a "/" para ser /api/usuarios
    public List<Usuario> cargarUsuarios(){
        return usuarioService.getUsuarios();
    }
    
    // Según el PDF, la búsqueda por ID es /api/usuarios/{idUsuario}
    @GetMapping("/{id}") // El path variable 'id' coincide con el parámetro del método
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id){
        Usuario usuario = usuarioService.buscarUsuario(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ELIMINADO: @GetMapping("/list/huespedes")
    
    @PostMapping 
    public ResponseEntity<Usuario> agregar(@RequestBody Usuario usuario){
        // El 'usuario' recibido aquí debe tener los campos:
        // username, password, nombre, apellido, cedula, rol.
        // El ID se autogenerará.
        try {
            Usuario nuevoUsuario = usuarioService.nuevoUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED); // Correcto HttpStatus para creación
        } catch (Exception e) { // Captura básica para errores de duplicados, etc.
            // Podría ser más específico con DataIntegrityViolationException
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> editar(@PathVariable Long id, @RequestBody Usuario usuarioDetalles){
        Usuario usuarioExistente = usuarioService.buscarUsuario(id);
        if (usuarioExistente == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos permitidos. No se debería permitir cambiar el username fácilmente si es un identificador.
        // El ID no se cambia. El password debería tener un endpoint/proceso separado.
        if (usuarioDetalles.getNombre() != null) {
            usuarioExistente.setNombre(usuarioDetalles.getNombre());
        }
        if (usuarioDetalles.getApellido() != null) {
            usuarioExistente.setApellido(usuarioDetalles.getApellido());
        }
        if (usuarioDetalles.getCedula() != null) {
            // Considerar validación si la cédula ya existe para OTRO usuario
            usuarioExistente.setCedula(usuarioDetalles.getCedula());
        }
        if (usuarioDetalles.getRol() != null) {
            usuarioExistente.setRol(usuarioDetalles.getRol());
        }

        try {
            Usuario usuarioActualizado = usuarioService.nuevoUsuario(usuarioExistente); // El método save de JPARepository sirve para crear y actualizar.
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    // Según el PDF, es DELETE /api/usuarios/{idUsuario}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){ // Cambiado el tipo de retorno para NO_CONTENT
        Usuario usuarioExistente = usuarioService.buscarUsuario(id);
        if (usuarioExistente != null) {
            usuarioService.borrarUsuario(id);
            return ResponseEntity.noContent().build(); // Correcto HttpStatus para eliminación exitosa
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}