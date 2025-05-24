package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCedula(String cedula);
    List<Cliente> findByApellidosContainingIgnoreCase(String apellidos);
    
}