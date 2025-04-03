package entornos.hotelflow.hotel_flow.modelos;

import jakarta.validation.constraints.Future; // Importa para validación
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservaRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El ID de habitación es obligatorio")
    private Integer idHabitacion;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @FutureOrPresent(message = "La fecha de entrada no puede ser pasada")
    private LocalDateTime fechaEntrada;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura a la fecha de entrada")
    private LocalDateTime fechaSalida;
}