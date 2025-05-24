package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(length = 50, unique = true, nullable = false)
    private String nombre;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer cantidad = 0;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;
}