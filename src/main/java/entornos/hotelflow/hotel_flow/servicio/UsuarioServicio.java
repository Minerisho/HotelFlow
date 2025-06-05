package entornos.hotelflow.hotel_flow.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entornos.hotelflow.hotel_flow.modelos.LoginDTO;
import entornos.hotelflow.hotel_flow.modelos.Usuario;
import entornos.hotelflow.hotel_flow.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioServicio implements IUsuarioServicio {
    
    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepositorio.findAll();
    }

    // ELIMINADO: getUsuarioHuesped()
    
    @Override
    public Usuario buscarUsuario(Long id) {
        // El método findById del repositorio ya espera el tipo de la PK de Usuario, que es Long.
        // El campo 'id' en la entidad Usuario es Long.
        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);
        // No es necesario comprobar 'if (usuario == null)' aquí, orElse(null) ya lo maneja.
        // El controlador o quien llame al servicio debería manejar el caso de 'null'.
        return usuario;
    }

    @Override
    public Usuario nuevoUsuario(Usuario usuario) {
        // La entidad Usuario que llega aquí ya debería tener los campos correctos
        // (id, username, password, nombre, apellido, cedula, rol).
        // Las validaciones de unicidad para 'username' y 'cedula' están en la BD
        // y en las anotaciones de la entidad. Si se viola, saltará una DataIntegrityViolationException.
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public int borrarUsuario(Long id) {
       usuarioRepositorio.deleteById(id);
       return 1; // Retornar 1 podría indicar éxito, aunque void sería más común si no hay estados de error complejos.
    }

    @Override
    public int login(String username, String password) { // Parámetros actualizados
        return usuarioRepositorio.login(username, password); // Argumentos actualizados
    }

    @Override
    public Usuario ingresar(LoginDTO loginDto) {
        // LoginDTO ahora tiene getUsername() en lugar de getCorreoElectronico()
        // y el repositorio espera 'username' y 'password'
        Optional<Usuario> optUsuario = usuarioRepositorio.ingresar(loginDto.getUsername(), loginDto.getContrasena());
        return optUsuario.orElse(null);
    
    }
}