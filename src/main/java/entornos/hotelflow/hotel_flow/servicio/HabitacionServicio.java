package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;
import entornos.hotelflow.hotel_flow.repositorio.HabitacionRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.IHabitacionServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitacionServicio implements IHabitacionServicio {
    
    @Autowired
    private HabitacionRepositorio habitacionRepositorio;
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listarTodas() {
        return habitacionRepositorio.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public HabitacionDTO buscarPorId(Integer id) {
        return habitacionRepositorio.findById(id)
                .map(this::convertirADTO)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public HabitacionDTO buscarPorNumero(String numero) {
        return habitacionRepositorio.findByNumero(numero)
                .map(this::convertirADTO)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorEstado(String estado) {
        try {
            Habitacion.EstadoHabitacion estadoEnum = Habitacion.EstadoHabitacion.valueOf(estado.toUpperCase());
            return habitacionRepositorio.findByEstado(estadoEnum).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorTipo(String tipo) {
        try {
            Habitacion.TipoHabitacion tipoEnum = Habitacion.TipoHabitacion.valueOf(tipo.toUpperCase());
            return habitacionRepositorio.findByTipo(tipoEnum).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorCapacidadMinima(Integer capacidad) {
        return habitacionRepositorio.findByCapacidadGreaterThanEqual(capacidad).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorTarifaMaxima(BigDecimal tarifaMaxima) {
        return habitacionRepositorio.findByTarifaBaseLessThanEqual(tarifaMaxima).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorEstadoYTipo(String estado, String tipo) {
        try {
            Habitacion.EstadoHabitacion estadoEnum = Habitacion.EstadoHabitacion.valueOf(estado.toUpperCase());
            Habitacion.TipoHabitacion tipoEnum = Habitacion.TipoHabitacion.valueOf(tipo.toUpperCase());
            
            return habitacionRepositorio.buscarPorEstadoYTipo(estadoEnum, tipoEnum).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    @Override
    @Transactional
    public HabitacionDTO guardar(HabitacionDTO habitacionDTO) {
        if (habitacionDTO.getNumero() != null && 
                habitacionRepositorio.existsByNumero(habitacionDTO.getNumero())) {
            throw new RuntimeException("Ya existe una habitación con el número: " + habitacionDTO.getNumero());
        }
        
        Habitacion habitacion = convertirAEntidad(habitacionDTO);
        Habitacion guardada = habitacionRepositorio.save(habitacion);
        return convertirADTO(guardada);
    }
    
    @Override
    @Transactional
    public HabitacionDTO actualizar(Integer id, HabitacionDTO habitacionDTO) {
        Optional<Habitacion> habitacionExistente = habitacionRepositorio.findById(id);
        
        if (habitacionExistente.isEmpty()) {
            return null;
        }
        
        // Verificar si el número de habitación está siendo modificado y ya existe
        if (habitacionDTO.getNumero() != null && 
                !habitacionDTO.getNumero().equals(habitacionExistente.get().getNumero()) &&
                habitacionRepositorio.existsByNumero(habitacionDTO.getNumero())) {
            throw new RuntimeException("Ya existe una habitación con el número: " + habitacionDTO.getNumero());
        }
        
        Habitacion habitacion = habitacionExistente.get();
        actualizarDatosEntidad(habitacion, habitacionDTO);
        
        Habitacion actualizada = habitacionRepositorio.save(habitacion);
        return convertirADTO(actualizada);
    }
    
    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (habitacionRepositorio.existsById(id)) {
            habitacionRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean cambiarEstado(Integer id, String nuevoEstado) {
        Optional<Habitacion> habitacionOpt = habitacionRepositorio.findById(id);
        
        if (habitacionOpt.isEmpty()) {
            return false;
        }
        
        try {
            Habitacion.EstadoHabitacion estadoEnum = Habitacion.EstadoHabitacion.valueOf(nuevoEstado.toUpperCase());
            Habitacion habitacion = habitacionOpt.get();
            habitacion.setEstado(estadoEnum);
            habitacionRepositorio.save(habitacion);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Métodos auxiliares para conversión entre entidad y DTO
    private HabitacionDTO convertirADTO(Habitacion habitacion) {
        return new HabitacionDTO(
                habitacion.getIdHabitacion(),
                habitacion.getNumero(),
                habitacion.getTipo().name(),
                habitacion.getCapacidad(),
                habitacion.getTarifaBase(),
                habitacion.getEstado().name(),
                habitacion.getDescripcion()
        );
    }
    
    private Habitacion convertirAEntidad(HabitacionDTO dto) {
        Habitacion habitacion = new Habitacion();
        
        if (dto.getIdHabitacion() != null) {
            habitacion.setIdHabitacion(dto.getIdHabitacion());
        }
        
        habitacion.setNumero(dto.getNumero());
        
        if (dto.getTipo() != null) {
            habitacion.setTipo(Habitacion.TipoHabitacion.valueOf(dto.getTipo().toUpperCase()));
        }
        
        habitacion.setCapacidad(dto.getCapacidad());
        habitacion.setTarifaBase(dto.getTarifaBase());
        
        if (dto.getEstado() != null) {
            habitacion.setEstado(Habitacion.EstadoHabitacion.valueOf(dto.getEstado().toUpperCase()));
        }
        
        habitacion.setDescripcion(dto.getDescripcion());
        
        return habitacion;
    }
    
    private void actualizarDatosEntidad(Habitacion habitacion, HabitacionDTO dto) {
        if (dto.getNumero() != null) {
            habitacion.setNumero(dto.getNumero());
        }
        
        if (dto.getTipo() != null) {
            habitacion.setTipo(Habitacion.TipoHabitacion.valueOf(dto.getTipo().toUpperCase()));
        }
        
        if (dto.getCapacidad() != null) {
            habitacion.setCapacidad(dto.getCapacidad());
        }
        
        if (dto.getTarifaBase() != null) {
            habitacion.setTarifaBase(dto.getTarifaBase());
        }
        
        if (dto.getEstado() != null) {
            habitacion.setEstado(Habitacion.EstadoHabitacion.valueOf(dto.getEstado().toUpperCase()));
        }
        
        if (dto.getDescripcion() != null) {
            habitacion.setDescripcion(dto.getDescripcion());
        }
    }
}