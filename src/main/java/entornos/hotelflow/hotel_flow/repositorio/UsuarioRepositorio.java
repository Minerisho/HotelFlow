package entornos.hotelflow.hotel_flow.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entornos.hotelflow.hotel_flow.modelos.Usuario;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoElectronico(String correo);


    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.correoElectronico = :correo AND u.contrasena = :contrasena")
    int login(@Param("correo") String correo, @Param("contrasena") String contrasena);
    
    @Query("SELECT u FROM Usuario u WHERE u.correoElectronico = :correo AND u.contrasena = :contrasena")
    Optional<Usuario> ingresar(@Param("correo") String correo, @Param("contrasena") String contrasena);
}
