package entornos.hotelflow.hotel_flow.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entornos.hotelflow.hotel_flow.modelos.Usuario;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);


    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.username  = :username AND u.password = :password")
    int login(@Param("username") String username, @Param("password") String password);

    @Query("SELECT u FROM Usuario u WHERE u.username  = :username AND u.password = :password")
    Optional<Usuario> ingresar(@Param("username") String username, @Param("password") String password);


    @Query("SELECT u FROM Usuario u WHERE u.rol = 'HUESPED'")
    List<Usuario> obtenerUsuariosHuesped();
}
