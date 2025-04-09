package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Integer> {

    // Encontrar reservas por ID de usuario
    List<Reserva> findByUsuarioIdUsuario(Long idUsuario);

    // Encontrar reservas por ID de habitación
    List<Reserva> findByHabitacionIdHabitacion(Integer idHabitacion);

    // Encontrar reservas activas (CONFIRMADA o PENDIENTE) para una habitación que se interpongan con un rango de fechas
    @Query("SELECT r FROM Reserva r WHERE r.habitacion.idHabitacion = :idHabitacion " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE') " +
           "AND r.fechaEntrada < :fechaSalida AND r.fechaSalida > :fechaEntrada")
    List<Reserva> findReservasActivasSolapadas(
            @Param("idHabitacion") Integer idHabitacion,
            @Param("fechaEntrada") LocalDateTime fechaEntrada,
            @Param("fechaSalida") LocalDateTime fechaSalida);

}