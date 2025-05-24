package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ReservaRequestDTO {
    private Integer idCliente;
    private Integer numeroHabitacion;
    private LocalDate fechaLlegadaEstadia; // Fecha de inicio de la estadía
    private LocalDate fechaSalidaEstadia;  // Fecha de fin de la estadía
    private String tipoPago;              // String para el Enum Reserva.TipoPagoReserva
    private LocalDate fechaReserva;        // Opcional: Fecha en que se realiza la reserva (puede ser auto-asignada)
    private String estado;                // Opcional, para cuando se actualiza el estado (ej. PAGADA)
}