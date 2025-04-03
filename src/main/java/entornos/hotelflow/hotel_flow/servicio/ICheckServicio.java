package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.CheckLogDTO; 
import entornos.hotelflow.hotel_flow.modelos.CheckInRequestDTO;

public interface ICheckServicio {

    /**
     Registra el check-in para una reserva específica.
     Cambia el estado de la habitación asociada a OCUPADA.
     @param checkInRequest 
     @return 
     @throws RuntimeException si la reserva no es válida, la habitación no está disponible o ya hay check-in.
     */
    CheckLogDTO registrarCheckIn(CheckInRequestDTO checkInRequest);

    /**
     Registra el check-out para un check-in activo (log).
     Cambia el estado de la habitación asociada a EN_LIMPIEZA o DISPONIBLE.
     @param idCheckLog 
     @return
     @throws RuntimeException si el CheckLog no existe o ya tiene check-out.
     */
    CheckLogDTO registrarCheckOut(Long idCheckLog);

    /**
     Busca un CheckLog por su ID.
     @param idCheckLog 
     @return 
     */
    CheckLogDTO buscarCheckLogPorId(Long idCheckLog); 
}