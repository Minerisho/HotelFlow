package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    public enum MetodoPagoOpciones {
        EFECTIVO, NEQUI, BANCOLOMBIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_habitacion", nullable = false)
    private Habitacion habitacion;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPagoOpciones metodoPago;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;
}