package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Reserva;
import entornos.hotelflow.hotel_flow.modelos.ReservaDTO;
import entornos.hotelflow.hotel_flow.modelos.ReservaRequestDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IReservaServicio {
    // Métodos que el compilador parece esperar
    ReservaDTO cambiarEstadoReserva(Integer idReserva, Reserva.EstadoReserva nuevoEstado, Reserva.TipoPagoReserva tipoPagoSiAplica); // Nota: TipoPagoReserva enum
    ReservaDTO obtenerReservaPorId(Integer idReserva); // Error indicaba este, devolviendo DTO directamente
    List<ReservaDTO> obtenerReservasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin); // Asumimos que es por fecha de creación de reserva

    // Métodos que hemos estado refinando
    ReservaDTO crearReserva(ReservaRequestDTO reservaRequestDTO);
    List<ReservaDTO> obtenerTodasLasReservas();
    Optional<ReservaDTO> obtenerReservaDTOPorId(Integer idReserva); // Versión Optional
    Optional<Reserva> obtenerReservaEntidadPorId(Integer idReserva); // Para uso interno
    ReservaDTO actualizarReserva(Integer idReserva, ReservaRequestDTO reservaRequestDTO);
    void cancelarReserva(Integer idReserva); // Cambia estado a CANCELADA
    List<ReservaDTO> obtenerReservasPorCliente(Integer idCliente);
    List<ReservaDTO> obtenerReservasPorHabitacion(Integer numeroHabitacion);
    List<ReservaDTO> obtenerReservasActivasParaHabitacionEnFechas(
            Integer numeroHabitacion,
            LocalDate fechaLlegada,
            LocalDate fechaSalida
    );
    // Renombrado para claridad, coincide con lo que pide el error si se interpreta como fecha de creación
    List<ReservaDTO> obtenerReservasPorRangoDeFechaCreacion(LocalDate fechaInicio, LocalDate fechaFin);

}