package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.*; 
import entornos.hotelflow.hotel_flow.repositorio.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CheckServicio implements ICheckServicio {

    @Autowired
    private CheckLogRepositorio checkLogRepositorio; 

    @Autowired
    private ReservaRepositorio reservaRepositorio; 

    @Autowired
    private IHabitacionServicio habitacionServicio; 

    @Override
    @Transactional
    public CheckLogDTO registrarCheckIn(CheckInRequestDTO checkInRequest) {
        Reserva reserva = reservaRepositorio.findById(checkInRequest.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + checkInRequest.getIdReserva()));

        if (reserva.getEstado() != Reserva.EstadoReserva.CONFIRMADA) {
            throw new RuntimeException("La reserva " + reserva.getId_reserva() + " no está confirmada. Estado actual: " + reserva.getEstado());
        }

        Habitacion habitacion = reserva.getHabitacion(); 
        if (habitacion == null) {
            throw new RuntimeException("La reserva " + reserva.getId_reserva() + " no tiene una habitación asociada.");
        }
        if (habitacion.getEstado() != Habitacion.EstadoHabitacion.DISPONIBLE) {
            throw new RuntimeException("La habitación " + habitacion.getNumero() + " no está disponible para check-in. Estado actual: " + habitacion.getEstado());
        }

        checkLogRepositorio.findByReservaIdReservaAndCheckOutIsNull(reserva.getId_reserva())
                .ifPresent(log -> {
                    throw new RuntimeException("Ya existe un check-in activo para la reserva " + reserva.getId_reserva());
                });

 
        CheckLog nuevoCheckLog = new CheckLog();
        nuevoCheckLog.setReserva(reserva);

        CheckLog checkLogGuardado = checkLogRepositorio.save(nuevoCheckLog);

        boolean estadoCambiado = habitacionServicio.cambiarEstado(habitacion.getIdHabitacion(), Habitacion.EstadoHabitacion.OCUPADA.name());
        if (!estadoCambiado) {
             throw new RuntimeException("No se pudo cambiar el estado de la habitación a OCUPADA.");
        }

        return new CheckLogDTO(checkLogGuardado); 
    }

    @Override
    @Transactional
    public CheckLogDTO registrarCheckOut(Long idCheckLog) {
        CheckLog checkLog = checkLogRepositorio.findById(idCheckLog)
                .orElseThrow(() -> new RuntimeException("Registro de Check-in (Log) no encontrado con ID: " + idCheckLog));

        if (checkLog.getCheckOut() != null) {
            throw new RuntimeException("Ya se registró el check-out para este log (ID: " + idCheckLog + ") en fecha: " + checkLog.getCheckOut());
        }

        checkLog.setCheckOut(LocalDateTime.now());
        CheckLog checkLogActualizado = checkLogRepositorio.save(checkLog);

        Habitacion habitacion = checkLog.getReserva().getHabitacion();
        if (habitacion != null) {
            boolean estadoCambiado = habitacionServicio.cambiarEstado(habitacion.getIdHabitacion(), Habitacion.EstadoHabitacion.EN_LIMPIEZA.name());
             if (!estadoCambiado) {
                  System.err.println("Advertencia: No se pudo cambiar el estado de la habitación " + habitacion.getNumero() + " a EN_LIMPIEZA durante el checkout del log " + idCheckLog);
             }
        } else {
             System.err.println("Advertencia: No se encontró habitación asociada a la reserva del log " + idCheckLog + " durante el checkout.");
        }


        return new CheckLogDTO(checkLogActualizado); 
    }

     @Override
     public CheckLogDTO buscarCheckLogPorId(Long idCheckLog) {
         return checkLogRepositorio.findById(idCheckLog)
                 .map(CheckLogDTO::new) 
                 .orElse(null);
     }
}