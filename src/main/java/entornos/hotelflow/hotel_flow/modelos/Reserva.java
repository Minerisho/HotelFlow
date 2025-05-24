package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    public enum EstadoReserva {
        PAGADA, NO_PAGADA, CANCELADA, ACTIVA 
    }

    public enum TipoPagoReserva {
        EFECTIVO, NEQUI, BANCOLOMBIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_habitacion", nullable = false)
    private Habitacion habitacion;

    @Column(name = "fecha_llegada_estadia", nullable = false) 
    private LocalDate fechaLlegadaEstadia;

    @Column(name = "fecha_salida_estadia", nullable = false)
    private LocalDate fechaSalidaEstadia;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PAGADA', 'NO_PAGADA', 'CANCELADA', 'ACTIVA') DEFAULT 'NO_PAGADA'")
    private EstadoReserva estado = EstadoReserva.NO_PAGADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPagoReserva tipoPago;

    @Column(name = "fecha_reserva", nullable = false) 
    private LocalDate fechaReserva;
}