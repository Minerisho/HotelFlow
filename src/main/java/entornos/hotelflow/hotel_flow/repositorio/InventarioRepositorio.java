package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Integer> {
    Optional<Inventario> findByNombre(String nombre);
    List<Inventario> findByNombreContainingIgnoreCase(String nombre);
}