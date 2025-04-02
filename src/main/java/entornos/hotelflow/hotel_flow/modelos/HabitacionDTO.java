package entornos.hotelflow.hotel_flow.modelos;
import java.math.BigDecimal;

public class HabitacionDTO {
    private Integer idHabitacion;
    private String numero;
    private String tipo;
    private Integer capacidad;
    private BigDecimal tarifaBase;
    private String estado;
    private String descripcion;
    
    // Constructors
    public HabitacionDTO() {
    }
    
    public HabitacionDTO(Integer idHabitacion, String numero, String tipo, Integer capacidad,
                       BigDecimal tarifaBase, String estado, String descripcion) {
        this.idHabitacion = idHabitacion;
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
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
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
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}