package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Cliente {

    public enum GeneroCliente {
        MASCULINO, FEMENINO, OTRO
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
    private GeneroCliente genero;

    @Column(length = 50)
    private String profesion;

    @Column(length = 100)
    private String procedencia;

    @Column(length = 100)
    private String destino;

    @Column(name = "fecha_llegada")
    private LocalDate fechaLlegada;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_habitacion") 
    private Habitacion habitacion;

    @Column(length = 100)
    private String correo;

    @Column(name = "telefono_emergencia", length = 20)
    private String telefonoEmergencia;

    @Column(length = 100)
    private String eps;


}