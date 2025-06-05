package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Usuarios") // Asegúrate que el nombre de la tabla coincida con tu BD; en el script es 'Usuarios'
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Mapeado a la columna 'id' de la BD
    private Long id;

    @Column(name = "username", nullable = false, unique = true) // Mapeado a 'username'
    private String username;

    @Column(name = "password", nullable = false) // Mapeado a 'password'
    private String password;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "cedula", unique = true) // Mapeado a 'cedula', asumiendo que debe ser único si existe
    private String cedula;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false) // Mapeado a 'rol'
    private Rol rol;

    // Enum Rol limitado a los valores de la BD
    public static enum Rol {
        ADMIN,
        RECEPCIONISTA
    }

    // Campos 'fechaCreacion', 'estado' y método 'prePersist' eliminados
}