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
import entornos.hotelflow.hotel_flow.servicio.UsuarioServicio;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {
    
    //Atributo es un objeto de la clase servicio
    @Autowired
    UsuarioServicio usuarioService;
    
    //Listar los Usuarios
    @GetMapping("/list")
    public List<Usuario> cargarUsuarios(){
        return usuarioService.getUsuarios();
    }
    
    //Buscar por Id
    @GetMapping("/list/{id}")
    public Usuario buscarPorId(@PathVariable Long id){
        return usuarioService.buscarUsuario(id);
    }

    // Obtener únicamente los usuarios con rol HUESPED
    @GetMapping("/list/huespedes")
    public List<Usuario> cargarUsuariosHuesped(){
        return usuarioService.getUsuarioHuesped();
    }
    
    //Agregar un Usuario
    @PostMapping("/")
    public ResponseEntity<Usuario> agregar(@RequestBody Usuario usuario){
        Usuario obj = usuarioService.nuevoUsuario(usuario);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
    
    //Actualizar un Usuario
    @PutMapping("/")
    public ResponseEntity<Usuario> editar(@RequestBody Usuario usuario){
        Usuario obj = usuarioService.buscarUsuario(usuario.getId());
        if (obj != null) {
            obj.setUsername(usuario.getUsername());
            obj.setPassword(usuario.getPassword());
        }else{
            return new ResponseEntity<>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
    
    //Eliminar el Usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> eliminar(@PathVariable Long id){
        Usuario obj = usuarioService.buscarUsuario(id);
        if (obj != null) {
            usuarioService.borrarUsuario(id);
        }else{
            return new ResponseEntity<>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}

