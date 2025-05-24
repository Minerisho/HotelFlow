package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ConsumoRequestDTO {
    private Integer idCliente;
    private Integer idProducto;
    private Integer cantidad;
    private LocalDate fechaConsumo; // Opcional, se puede tomar la actual
    private Boolean cargadoAHabitacion = false;
}