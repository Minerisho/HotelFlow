package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PagoRequestDTO {
    private Integer idCliente;
    private Integer numeroHabitacion;
    private BigDecimal monto;
    private Pago.MetodoPagoOpciones metodoPago; // Usa el enum de la entidad Pago
    private LocalDate fechaPago; // Opcional
}