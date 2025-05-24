package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; 
import org.springframework.stereotype.Repository;
import java.util.List; 
import java.util.Optional; 
@Repository
public interface HabitacionRepositorio extends JpaRepository<Habitacion, Integer>, JpaSpecificationExecutor<Habitacion> { // <-- IMPORTANTE: Añade JpaSpecificationExecutor
    
    Optional<Habitacion> findByNumeroHabitacion(Integer numeroHabitacion);                                                             
    List<Habitacion> findByTipo(Habitacion.TipoHabitacion tipo);
    List<Habitacion> findByEstado(Habitacion.EstadoHabitacion estado);
    List<Habitacion> findByDisponibleTrue();
}