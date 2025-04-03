package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CheckLogDTO { 
    private Long idCheckLog;
    private Integer idReserva; 
    private Integer idHabitacion;
    private String numeroHabitacion;
    private Long idHuesped;
    private String nombreHuesped;
    // ---
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public CheckLogDTO(CheckLog checkLog) {
        this.idCheckLog = checkLog.getIdCheckLog();
        this.checkIn = checkLog.getCheckIn();
        this.checkOut = checkLog.getCheckOut();
        if (checkLog.getReserva() != null) {
            this.idReserva = checkLog.getReserva().getId_reserva();
             if (checkLog.getReserva().getHabitacion() != null) {
                 this.idHabitacion = checkLog.getReserva().getHabitacion().getIdHabitacion();
                 this.numeroHabitacion = checkLog.getReserva().getHabitacion().getNumero();
             }
             if (checkLog.getReserva().getUsuario() != null) {
                 this.idHuesped = checkLog.getReserva().getUsuario().getIdUsuario();
                 this.nombreHuesped = checkLog.getReserva().getUsuario().getNombre() + " " + checkLog.getReserva().getUsuario().getApellido();
             }
        }
    }

     public CheckLogDTO() { }
}