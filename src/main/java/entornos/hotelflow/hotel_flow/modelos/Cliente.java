package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Clientes") // O "clientes" si decides mantener minúsculas en BD y ajustar aquí
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    public enum GeneroCliente { // Esta definición ya coincide con tu BD corregida
        MASCULINO, 
        FEMENINO, 
        OTRO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(length = 100)
    private String nombres;

    @Column(length = 100)
    private String apellidos;

    @Column(length = 20, unique = true, nullable = false)
    private String cedula;

    @Column(length = 50)
    private String nacionalidad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private GeneroCliente genero; // Mapeará directamente si la BD y el enum Java coinciden

    @Column(length = 50)
    private String profesion;

    @Column(length = 100)
    private String procedencia;

    @Column(length = 100)
    private String destino;

    // Campos fechaLlegada, fechaSalida y habitacion eliminados

    @Column(length = 100)
    private String correo;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(length = 100)
    private String eps;
}