package entornos.hotelflow.hotel_flow.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entornos.hotelflow.hotel_flow.modelos.LoginDTO;
import entornos.hotelflow.hotel_flow.modelos.Usuario;
import entornos.hotelflow.hotel_flow.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;


/**
 *
 * @author Usuario
 */
@Service
@Transactional
public class UsuarioServicio implements IUsuarioServicio {
    
    //Atributo del repositorio
    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public List<Usuario> getUsuarioHuesped(){
        return usuarioRepositorio.obtenerUsuariosHuesped();
    }
    
    @Override
    public Usuario buscarUsuario(Long id) {
        Usuario usuario = null;
        usuario = usuarioRepositorio.findById(id).orElse(null);
        if (usuario == null) {
            return null;
        }
        return usuario;
    }

    @Override
    public Usuario nuevoUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public int borrarUsuario(Long id) {
       usuarioRepositorio.deleteById(id);
       return 1;
    }

    @Override
    public int login(String username, String password) {
        return usuarioRepositorio.login(username, password);
    }

    @Override
    public Usuario ingresar(LoginDTO loginDto) {
        Optional<Usuario> optUsuario = usuarioRepositorio.ingresar(loginDto.getUsername(), loginDto.getPassword());
        return optUsuario.orElse(null);
    
    }
}
