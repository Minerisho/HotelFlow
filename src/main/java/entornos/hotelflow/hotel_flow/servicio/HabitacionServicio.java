package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.HabitacionDTO;
import entornos.hotelflow.hotel_flow.modelos.Reserva; // Importado para lógica de eliminación
import entornos.hotelflow.hotel_flow.repositorio.ClienteRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.HabitacionRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.ReservaRepositorio; // Importado para lógica de eliminación y disponibilidad
import jakarta.persistence.criteria.Predicate; // Para Specification
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification; // Para Specification
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList; // Para Specification
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitacionServicio implements IHabitacionServicio {

    @Autowired
    private HabitacionRepositorio habitacionRepositorio;

    @Autowired
    private ReservaRepositorio reservaRepositorio; // Para verificar disponibilidad y dependencias

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    // --- Implementación de métodos solicitados por el compilador ---


    

    @Override
    @Transactional(readOnly = true)
    public HabitacionDTO obtenerHabitacionPorNumero(int numero) {
        return habitacionRepositorio.findById(numero)
                .map(HabitacionDTO::new)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con número: " + numero + " (método directo DTO)"));
    }

    @Override
    @Transactional
    public Habitacion guardarHabitacion(Habitacion habitacion) {
        // Este método devuelve la entidad. Podría ser usado internamente o si se decide no usar DTO para la creación.
        // Si 'crearHabitacion' ya existe y hace lo mismo pero devuelve DTO, se puede unificar.
        if (habitacionRepositorio.findById(habitacion.getNumeroHabitacion()).isPresent()) {
            throw new IllegalArgumentException("Habitación con número " + habitacion.getNumeroHabitacion() + " ya existe.");
        }
        habitacion.setEstado(Habitacion.EstadoHabitacion.LIBRE); // Estado por defecto al guardar
        habitacion.setDisponible(true); // Disponible por defecto
        return habitacionRepositorio.save(habitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorEstado(Habitacion.EstadoHabitacion estado) {
        return habitacionRepositorio.findByEstado(estado).stream()
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarPorTipo(Habitacion.TipoHabitacion tipo) {
        return habitacionRepositorio.findByTipo(tipo).stream()
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarHabitacionesDisponibles() {
        // Devuelve habitaciones donde el campo 'disponible' es TRUE
        return habitacionRepositorio.findByDisponibleTrue().stream()
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    // --- Implementación de métodos que ya teníamos/refinamos ---

    @Override
    @Transactional
    public HabitacionDTO crearHabitacion(Habitacion habitacion) {
        // Este es el método que devuelve DTO, preferido para los controladores.
        Habitacion guardada = guardarHabitacion(habitacion); // Reutiliza la lógica de guardarEntidad
        return new HabitacionDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> obtenerTodasLasHabitaciones() {
        return habitacionRepositorio.findAll().stream()
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HabitacionDTO> obtenerHabitacionDTOPorNumero(int numero) {
        return habitacionRepositorio.findById(numero).map(HabitacionDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Habitacion> obtenerHabitacionEntidadPorNumero(int numero) {
        return habitacionRepositorio.findById(numero);
    }

    @Override
    @Transactional
    public HabitacionDTO actualizarHabitacion(int numero, Habitacion habitacionDetalles) {
        Habitacion habitacionExistente = obtenerHabitacionEntidadPorNumero(numero)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con número: " + numero));

        if (habitacionDetalles.getTipo() != null) {
            habitacionExistente.setTipo(habitacionDetalles.getTipo());
        }
        if (habitacionDetalles.getClimatizacion() != null) {
            habitacionExistente.setClimatizacion(habitacionDetalles.getClimatizacion());
        }
        if (habitacionDetalles.getEstado() != null) {
            habitacionExistente.setEstado(habitacionDetalles.getEstado());
            if(habitacionDetalles.getEstado() == Habitacion.EstadoHabitacion.OCUPADO) {
                habitacionExistente.setDisponible(false);
            } else if (habitacionDetalles.getEstado() == Habitacion.EstadoHabitacion.LIBRE || habitacionDetalles.getEstado() == Habitacion.EstadoHabitacion.LIMPIEZA) {
                // Verificar si hay reservas para hoy antes de marcar como disponible
                 List<Reserva> reservasHoy = reservaRepositorio.findReservasActivasSolapadas(numero, LocalDate.now(), LocalDate.now().plusDays(1));
                 habitacionExistente.setDisponible(reservasHoy.isEmpty());
            }
        }
        if (habitacionDetalles.getDisponible() != null) { // Permite override explícito
            habitacionExistente.setDisponible(habitacionDetalles.getDisponible());
        }
        if (habitacionDetalles.getPrecio() != null && habitacionDetalles.getPrecio().compareTo(BigDecimal.ZERO) >= 0) {
            habitacionExistente.setPrecio(habitacionDetalles.getPrecio());
        }
        Habitacion actualizada = habitacionRepositorio.save(habitacionExistente);
        return new HabitacionDTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminarHabitacion(int numero) {
        Habitacion habitacion = obtenerHabitacionEntidadPorNumero(numero)
            .orElseThrow(() -> new RuntimeException("Habitación no encontrada con número: " + numero));
        
        List<Reserva> reservasAsociadas = reservaRepositorio.findReservasActivasSolapadas(numero, LocalDate.MIN, LocalDate.MAX)
            .stream()
            .filter(r -> r.getFechaSalidaEstadia().isAfter(LocalDate.now().minusDays(1)) && r.getEstado() != Reserva.EstadoReserva.CANCELADA) // Reservas activas o futuras
            .toList();

        if (!reservasAsociadas.isEmpty()){
            throw new IllegalStateException("No se puede eliminar la habitación " + numero + " porque tiene reservas activas o futuras.");
        }
        habitacionRepositorio.delete(habitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarHabitaciones(
            Habitacion.TipoHabitacion tipo,
            Habitacion.EstadoHabitacion estado,
            Boolean disponible,
            BigDecimal precioMin,
            BigDecimal precioMax) {
        Specification<Habitacion> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tipo != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), tipo));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (disponible != null) {
                predicates.add(criteriaBuilder.equal(root.get("disponible"), disponible));
            }
            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }
            if (precioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMax));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return habitacionRepositorio.findAll(spec).stream()
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> buscarHabitacionesDisponiblesParaFechas(
            LocalDate fechaLlegada,
            LocalDate fechaSalida,
            Habitacion.TipoHabitacion tipo) {
        if (fechaLlegada == null || fechaSalida == null || fechaLlegada.isAfter(fechaSalida) || fechaLlegada.isEqual(fechaSalida)) {
            throw new IllegalArgumentException("Fechas de llegada y salida inválidas para la búsqueda.");
        }

        List<Habitacion> habitacionesCandidatas;
        if (tipo != null) {
            habitacionesCandidatas = habitacionRepositorio.findByTipo(tipo);
        } else {
            habitacionesCandidatas = habitacionRepositorio.findAll();
        }
        
        List<Integer> numerosHabitacionesOcupadas = reservaRepositorio
                .findReservasActivasSolapadas(null, fechaLlegada, fechaSalida)
                .stream()
                .map(reserva -> reserva.getHabitacion().getNumeroHabitacion())
                .distinct()
                .toList();

        return habitacionesCandidatas.stream()
                .filter(habitacion -> !numerosHabitacionesOcupadas.contains(habitacion.getNumeroHabitacion()))
                .map(HabitacionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HabitacionDTO actualizarEstadoHabitacion(Integer numeroHabitacion, Habitacion.EstadoHabitacion nuevoEstado) {
        Habitacion habitacion = obtenerHabitacionEntidadPorNumero(numeroHabitacion)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con número: " + numeroHabitacion));
        
        habitacion.setEstado(nuevoEstado);
        if (nuevoEstado == Habitacion.EstadoHabitacion.OCUPADO) {
            habitacion.setDisponible(false);
        } else if (nuevoEstado == Habitacion.EstadoHabitacion.LIBRE || nuevoEstado == Habitacion.EstadoHabitacion.LIMPIEZA) {
             List<Reserva> reservasHoy = reservaRepositorio.findReservasActivasSolapadas(numeroHabitacion, LocalDate.now(), LocalDate.now().plusDays(1));
             habitacion.setDisponible(reservasHoy.isEmpty());
        }
        Habitacion actualizada = habitacionRepositorio.save(habitacion);
        return new HabitacionDTO(actualizada);
    }

    @Override
    @Transactional
    public HabitacionDTO actualizarPrecioHabitacion(Integer numeroHabitacion, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser nulo o negativo.");
        }
        Habitacion habitacion = obtenerHabitacionEntidadPorNumero(numeroHabitacion)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con número: " + numeroHabitacion));
        habitacion.setPrecio(nuevoPrecio);
        Habitacion actualizada = habitacionRepositorio.save(habitacion);
        return new HabitacionDTO(actualizada);
    }
}