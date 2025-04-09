package entornos.hotelflow.hotel_flow.servicio; // O repositorio si sigues ese patrón

import entornos.hotelflow.hotel_flow.modelos.ReservaDTO;
import entornos.hotelflow.hotel_flow.modelos.ReservaRequestDTO;

import java.util.List;

public interface IReservaServicio {

    ReservaDTO crearReserva(ReservaRequestDTO reservaRequest);

    ReservaDTO buscarReservaPorId(Integer idReserva);

    List<ReservaDTO> listarReservasPorUsuario(Long idUsuario);

    List<ReservaDTO> listarTodasLasReservas(); // Podría ser útil para admins/recepcionistas

    ReservaDTO cancelarReserva(Integer idReserva);

    ReservaDTO confirmarReserva(Integer idReserva);

}