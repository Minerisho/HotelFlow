package entornos.hotelflow.hotel_flow.modelos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor 
public class HabitacionDTO {
    private Integer numeroHabitacion;
    private String tipo;
    private String climatizacion;
    private String estado;
    private Boolean disponible;
    private BigDecimal precio;

    // **** ESTE ES EL CONSTRUCTOR CRUCIAL ****
    // Permite crear un HabitacionDTO a partir de una entidad Habitacion
    public HabitacionDTO(Habitacion habitacion) {
        this.numeroHabitacion = habitacion.getNumeroHabitacion();
        if (habitacion.getTipo() != null) {
            this.tipo = habitacion.getTipo().name(); // Convierte el Enum a String
        }
        if (habitacion.getClimatizacion() != null) {
            this.climatizacion = habitacion.getClimatizacion().name(); // Convierte el Enum a String
        }
        if (habitacion.getEstado() != null) {
            this.estado = habitacion.getEstado().name(); // Convierte el Enum a String
        }
        this.disponible = habitacion.getDisponible();
        this.precio = habitacion.getPrecio();
    }

}