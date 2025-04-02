package entornos.hotelflow.hotel_flow.repositorio;
import entornos.hotelflow.hotel_flow.modelos.Habitacion;

import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;
import java.math.BigDecimal;
import java.util.List;

public interface IHabitacionServicio {
    
    List<HabitacionDTO> listarTodas();
    
    HabitacionDTO buscarPorId(Integer id);
    
    HabitacionDTO buscarPorNumero(String numero);
    
    List<HabitacionDTO> buscarPorEstado(String estado);
    
    List<HabitacionDTO> buscarPorTipo(String tipo);
    
    List<HabitacionDTO> buscarPorCapacidadMinima(Integer capacidad);
    
    List<HabitacionDTO> buscarPorTarifaMaxima(BigDecimal tarifaMaxima);
    
    List<HabitacionDTO> buscarPorEstadoYTipo(String estado, String tipo);
    
    HabitacionDTO guardar(HabitacionDTO habitacionDTO);
    
    HabitacionDTO actualizar(Integer id, HabitacionDTO habitacionDTO);
    
    boolean eliminar(Integer id);
    
    boolean cambiarEstado(Integer id, String nuevoEstado);
}