package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Habitaciones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class Habitacion {

    public enum TipoHabitacion {
        SOLA, DOBLE, MATRIMONIAL
    }

    public enum TipoClimatizacion {
        AIRE_ACONDICIONADO, VENTILADOR 
    }

    public enum EstadoHabitacion {
        LIBRE, OCUPADO, LIMPIEZA
    }

    @Id
    @Column(name = "numero_habitacion")
    private Integer numeroHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHabitacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoClimatizacion climatizacion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('LIBRE', 'OCUPADO', 'LIMPIEZA') DEFAULT 'LIBRE'")
    private EstadoHabitacion estado = EstadoHabitacion.LIBRE;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean disponible = true;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
}