package entornos.hotelflow.hotel_flow.servicio;

import java.util.List;

import entornos.hotelflow.hotel_flow.modelos.LoginDTO;
import entornos.hotelflow.hotel_flow.modelos.Usuario;

public interface IUsuarioServicio {
    
    List<Usuario> getUsuarios();
    Usuario buscarUsuario(Long id); // El nombre del parámetro 'id' ya coincide con el campo en Usuario
    Usuario nuevoUsuario(Usuario usuario);
    int borrarUsuario(Long id); // El nombre del parámetro 'id' ya coincide
    int login(String username, String password); // Cambiado 'correo' a 'username' y 'contrasena' a 'password'
    Usuario ingresar(LoginDTO loginDto); // LoginDTO ahora usa 'username'
    // ELIMINADO: getUsuarioHuesped() ya que no aplica a la entidad Usuario del sistema
}