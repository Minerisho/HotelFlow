package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.Habitacion.EstadoHabitacion;
import entornos.hotelflow.hotel_flow.modelos.Habitacion.TipoHabitacion;
import entornos.hotelflow.hotel_flow.modelos.*;
import entornos.hotelflow.hotel_flow.modelos.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepositorio extends JpaRepository<Habitacion, Integer> {
    
    Optional<Habitacion> findByNumero(String numero);
    
    List<Habitacion> findByEstado(EstadoHabitacion estado);
    
    List<Habitacion> findByTipo(TipoHabitacion tipo);
    
    List<Habitacion> findByCapacidadGreaterThanEqual(Integer capacidad);
    
    List<Habitacion> findByTarifaBaseLessThanEqual(BigDecimal tarifaMaxima);
    
    @Query("SELECT h FROM Habitacion h WHERE h.estado = :estado AND h.tipo = :tipo")
    List<Habitacion> buscarPorEstadoYTipo(EstadoHabitacion estado, TipoHabitacion tipo);
    
    boolean existsByNumero(String numero);
}