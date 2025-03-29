package entornos.hotelflow.hotel_flow.servicio;

import java.util.List;

import entornos.hotelflow.hotel_flow.modelos.LoginDTO;
import entornos.hotelflow.hotel_flow.modelos.Usuario;

public interface IUsuarioServicio {
    

    List<Usuario> getUsuarios();

    Usuario buscarUsuario(Long id);

    Usuario nuevoUsuario(Usuario usuario);

    int borrarUsuario(Long id);

    int login(String correo, String contrasena);
    Usuario ingresar(LoginDTO loginDto);
}
