package entornos.hotelflow.hotel_flow.modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Habitaciones")
public class Habitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;
    
    @Column(name = "numero", unique = true, nullable = false, length = 10)
    private String numero;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoHabitacion tipo;
    
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;
    
    @Column(name = "tarifa_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoHabitacion estado = EstadoHabitacion.DISPONIBLE;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    public enum TipoHabitacion {
        INDIVIDUAL, DOBLE, MATRIMONIAL, SUITE
    }
    
    public enum EstadoHabitacion {
        DISPONIBLE, OCUPADA, EN_LIMPIEZA, MANTENIMIENTO
    }
    
    // Constructors
    public Habitacion() {
    }
    
    public Habitacion(String numero, TipoHabitacion tipo, Integer capacidad, 
                     BigDecimal tarifaBase, EstadoHabitacion estado, String descripcion) {
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.tarifaBase = tarifaBase;
        this.estado = estado;
        this.descripcion = descripcion;
    }
    
    // Getters and Setters
    public Integer getIdHabitacion() {
        return idHabitacion;
    }
    
    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public TipoHabitacion getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }
    
    public Integer getCapacidad() {
        return capacidad;
    }
    
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
    
    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }
    
    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }
    
    public EstadoHabitacion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}