package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    
   // @Column(name = "fecha_creacion", updatable = false)
   // private LocalDateTime fechaCreacion;
    
   // @Enumerated(EnumType.STRING)
   // @Column(nullable = false)
    //private Estado estado;
    
   // @PrePersist
    //public void prePersist() {
     //   this.fechaCreacion = LocalDateTime.now();
      //  if (this.estado == null) {
       //     this.estado = Estado.ACTIVO;
        //}
   // }
    
    public static enum Rol {
        ADMIN,
        RECEPCIONISTA,
        CAJERO,
        HUESPED
    }
    
  //  public static enum Estado {
    //    ACTIVO,
     //   INACTIVO
   // }
}
