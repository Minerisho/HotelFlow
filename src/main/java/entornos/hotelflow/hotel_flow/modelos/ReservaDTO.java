package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservaDTO {
    private Integer idReserva;
    private Long idUsuario; 
    private String nombreUsuario; 
    private Integer idHabitacion; 
    private String numeroHabitacion; 
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private String estado; 
    private BigDecimal totalReserva;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public ReservaDTO() {}

    // Constructor completo 
    public ReservaDTO(Integer idReserva, Long idUsuario, String nombreUsuario, Integer idHabitacion, String numeroHabitacion, LocalDateTime fechaEntrada, LocalDateTime fechaSalida, String estado, BigDecimal totalReserva, LocalDateTime fechaCreacion) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idHabitacion = idHabitacion;
        this.numeroHabitacion = numeroHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
        this.totalReserva = totalReserva;
        this.fechaCreacion = fechaCreacion;
    }
}