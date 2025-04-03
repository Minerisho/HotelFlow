package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Check_Logs") 
public class CheckLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_check_log") 
    private Long idCheckLog;

    @ManyToOne 
    @JoinColumn(name = "id_reserva", nullable = false) 
    private Reserva reserva;

    @Column(name = "check_in", nullable = false) 
    private LocalDateTime checkIn; 

    @Column(name = "check_out") 
    private LocalDateTime checkOut; 

    @PrePersist
    protected void onCreate() {
        if (checkIn == null) {
            checkIn = LocalDateTime.now(); 
        }
    }
}