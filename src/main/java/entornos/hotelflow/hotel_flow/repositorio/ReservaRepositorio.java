package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByClienteIdCliente(Integer idCliente);
    List<Reserva> findByHabitacionNumeroHabitacion(Integer numeroHabitacion);
    List<Reserva> findByFechaReservaBetween(LocalDate fechaInicio, LocalDate fechaFin); // Fecha de creación
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);

    // Método para encontrar reservas activas (no canceladas) que se solapan con un rango de fechas para una habitación
    @Query("SELECT r FROM Reserva r WHERE r.habitacion.numeroHabitacion = :numeroHabitacion " +
           "AND r.estado <> entornos.hotelflow.hotel_flow.modelos.Reserva.EstadoReserva.CANCELADA " +
           "AND r.fechaLlegadaEstadia < :fechaSalidaDeseada " +
           "AND r.fechaSalidaEstadia > :fechaLlegadaDeseada")
    List<Reserva> findReservasActivasSolapadas(
            @Param("numeroHabitacion") Integer numeroHabitacion,
            @Param("fechaLlegadaDeseada") LocalDate fechaLlegadaDeseada,
            @Param("fechaSalidaDeseada") LocalDate fechaSalidaDeseada);
}