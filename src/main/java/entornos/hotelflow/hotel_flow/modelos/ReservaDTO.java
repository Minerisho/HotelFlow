package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReservaDTO {
    private Integer idReserva;
    private Integer idCliente;
    private String nombreCliente; // Para fácil visualización
    private Integer numeroHabitacion;
    private String tipoHabitacion; // Para fácil visualización
    private LocalDate fechaLlegadaEstadia; // Directo de la reserva
    private LocalDate fechaSalidaEstadia;  // Directo de la reserva
    private String estadoReserva;
    private String tipoPago;
    private LocalDate fechaReserva; // Fecha de creación de la reserva

    // Constructor mejorado
    public ReservaDTO(Reserva reserva, String nombreCliente, String tipoHabitacion) {
        this.idReserva = reserva.getIdReserva();
        this.idCliente = reserva.getCliente().getIdCliente();
        this.nombreCliente = nombreCliente; // Se pasa externamente tras cargar el cliente
        this.numeroHabitacion = reserva.getHabitacion().getNumeroHabitacion();
        this.tipoHabitacion = tipoHabitacion; // Se pasa externamente tras cargar la habitación
        this.fechaLlegadaEstadia = reserva.getFechaLlegadaEstadia();
        this.fechaSalidaEstadia = reserva.getFechaSalidaEstadia();
        this.estadoReserva = reserva.getEstado().name();
        this.tipoPago = reserva.getTipoPago().name();
        this.fechaReserva = reserva.getFechaReserva();
    }
    
    // Constructor simple si solo tienes la reserva (cliente y habitacion se obtendrán por ID si es necesario)
    public ReservaDTO(Reserva reserva) {
        this.idReserva = reserva.getIdReserva();
        this.idCliente = reserva.getCliente().getIdCliente();
        this.numeroHabitacion = reserva.getHabitacion().getNumeroHabitacion();
        this.fechaLlegadaEstadia = reserva.getFechaLlegadaEstadia();
        this.fechaSalidaEstadia = reserva.getFechaSalidaEstadia();
        this.estadoReserva = reserva.getEstado().name();
        this.tipoPago = reserva.getTipoPago().name();
        this.fechaReserva = reserva.getFechaReserva();
    }
}