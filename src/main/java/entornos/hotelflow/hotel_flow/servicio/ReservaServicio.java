package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.*;
import entornos.hotelflow.hotel_flow.repositorio.ClienteRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.HabitacionRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.ReservaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaServicio implements IReservaServicio {

    @Autowired
    private ReservaRepositorio reservaRepositorio;

    @Autowired
    private HabitacionRepositorio habitacionRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    
    @Autowired
    private IHabitacionServicio habitacionServicio; // Inyectado para actualizar estado de habitación

    // Helper para convertir Entidad a DTO
    private ReservaDTO convertirADTO(Reserva reserva) {
        Cliente cliente = clienteRepositorio.findById(reserva.getCliente().getIdCliente()).orElse(null);
        Habitacion habitacion = habitacionRepositorio.findById(reserva.getHabitacion().getNumeroHabitacion()).orElse(null);
        String nombreCliente = (cliente != null) ? cliente.getNombres() + " " + cliente.getApellidos() : "Cliente Desconocido";
        String tipoHabitacion = (habitacion != null) ? habitacion.getTipo().name() : "Tipo Desconocido";
        return new ReservaDTO(reserva, nombreCliente, tipoHabitacion);
    }

    // --- Implementación de métodos solicitados por el compilador ---

    @Override
    @Transactional
    public ReservaDTO cambiarEstadoReserva(Integer idReserva, Reserva.EstadoReserva nuevoEstado, Reserva.TipoPagoReserva tipoPagoSiAplica) {
        Reserva reserva = obtenerReservaEntidadPorId(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));
        
        Reserva.EstadoReserva estadoAnterior = reserva.getEstado();
        reserva.setEstado(nuevoEstado);

        if (nuevoEstado == Reserva.EstadoReserva.PAGADA && tipoPagoSiAplica != null) {
            reserva.setTipoPago(tipoPagoSiAplica);
        } else if (nuevoEstado == Reserva.EstadoReserva.PAGADA && reserva.getTipoPago() == null) { // Si se marca pagada y no se provee tipo, y no tenía uno
             throw new IllegalArgumentException("Se requiere un tipo de pago para marcar la reserva como PAGADA si no se especifica uno nuevo.");
        }
        
        Reserva actualizada = reservaRepositorio.save(reserva);

        // Lógica de negocio al cambiar estado:
        Habitacion habitacion = actualizada.getHabitacion();
        if (nuevoEstado == Reserva.EstadoReserva.ACTIVA && actualizada.getFechaLlegadaEstadia().isEqual(LocalDate.now())) {
            if (habitacion.getEstado() == Habitacion.EstadoHabitacion.LIBRE || habitacion.getEstado() == Habitacion.EstadoHabitacion.LIMPIEZA) {
                 habitacionServicio.actualizarEstadoHabitacion(habitacion.getNumeroHabitacion(), Habitacion.EstadoHabitacion.OCUPADO);
            }
        } else if (nuevoEstado == Reserva.EstadoReserva.CANCELADA || 
                  (estadoAnterior == Reserva.EstadoReserva.ACTIVA && (nuevoEstado == Reserva.EstadoReserva.PAGADA /*asumiendo checkout y pagada*/))) {
            if (habitacion.getEstado() == Habitacion.EstadoHabitacion.OCUPADO && 
                (actualizada.getFechaSalidaEstadia().isEqual(LocalDate.now()) || actualizada.getFechaSalidaEstadia().isBefore(LocalDate.now()) || nuevoEstado == Reserva.EstadoReserva.CANCELADA )) {
                habitacionServicio.actualizarEstadoHabitacion(habitacion.getNumeroHabitacion(), Habitacion.EstadoHabitacion.LIMPIEZA);
            }
        }
        
        return convertirADTO(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaDTO obtenerReservaPorId(Integer idReserva) {
        // Este es el que el error indica, devuelve DTO directamente (no Optional)
        return reservaRepositorio.findById(idReserva)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva + " (método directo DTO)"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        // Asumimos que se refiere a la fecha de creación de la reserva como en el método renombrado
        return obtenerReservasPorRangoDeFechaCreacion(fechaInicio, fechaFin);
    }


    // --- Implementación de métodos que ya teníamos/refinamos ---

    @Override
    @Transactional
    public ReservaDTO crearReserva(ReservaRequestDTO requestDTO) {
        Cliente cliente = clienteRepositorio.findById(requestDTO.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + requestDTO.getIdCliente()));
        Habitacion habitacion = habitacionRepositorio.findById(requestDTO.getNumeroHabitacion())
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con número: " + requestDTO.getNumeroHabitacion()));

        if (requestDTO.getFechaLlegadaEstadia() == null || requestDTO.getFechaSalidaEstadia() == null ||
            requestDTO.getFechaLlegadaEstadia().isAfter(requestDTO.getFechaSalidaEstadia()) ||
            requestDTO.getFechaLlegadaEstadia().isEqual(requestDTO.getFechaSalidaEstadia())) {
            throw new IllegalArgumentException("Fechas de estadía inválidas. La fecha de salida debe ser posterior a la fecha de llegada.");
        }

        List<Reserva> reservasSolapadas = reservaRepositorio.findReservasActivasSolapadas(
            habitacion.getNumeroHabitacion(), requestDTO.getFechaLlegadaEstadia(), requestDTO.getFechaSalidaEstadia()
        );

        if (!reservasSolapadas.isEmpty()) {
            throw new IllegalStateException("La habitación " + habitacion.getNumeroHabitacion() +
                    " no está disponible para las fechas seleccionadas.");
        }

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCliente(cliente);
        nuevaReserva.setHabitacion(habitacion);
        nuevaReserva.setFechaLlegadaEstadia(requestDTO.getFechaLlegadaEstadia());
        nuevaReserva.setFechaSalidaEstadia(requestDTO.getFechaSalidaEstadia());
        nuevaReserva.setFechaReserva(requestDTO.getFechaReserva() != null ? requestDTO.getFechaReserva() : LocalDate.now());
        
        try {
            nuevaReserva.setTipoPago(Reserva.TipoPagoReserva.valueOf(requestDTO.getTipoPago().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de pago inválido: " + requestDTO.getTipoPago());
        }
        
        if (requestDTO.getEstado() != null && !requestDTO.getEstado().isBlank()) {
            try {
                nuevaReserva.setEstado(Reserva.EstadoReserva.valueOf(requestDTO.getEstado().toUpperCase()));
            } catch (IllegalArgumentException e) {
                 nuevaReserva.setEstado(Reserva.EstadoReserva.NO_PAGADA);
            }
        } else {
            nuevaReserva.setEstado(Reserva.EstadoReserva.NO_PAGADA);
        }

        Reserva guardada = reservaRepositorio.save(nuevaReserva);
        
        if (guardada.getFechaLlegadaEstadia().isEqual(LocalDate.now()) && 
            (habitacion.getEstado() == Habitacion.EstadoHabitacion.LIBRE || habitacion.getEstado() == Habitacion.EstadoHabitacion.LIMPIEZA) &&
            (guardada.getEstado() == Reserva.EstadoReserva.ACTIVA || guardada.getEstado() == Reserva.EstadoReserva.PAGADA /*O NO_PAGADA si check-in se permite así*/ )) {
            habitacionServicio.actualizarEstadoHabitacion(habitacion.getNumeroHabitacion(), Habitacion.EstadoHabitacion.OCUPADO);
        }
        return convertirADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerTodasLasReservas() {
        return reservaRepositorio.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaDTO> obtenerReservaDTOPorId(Integer idReserva) {
        return reservaRepositorio.findById(idReserva).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> obtenerReservaEntidadPorId(Integer idReserva) {
        return reservaRepositorio.findById(idReserva);
    }

    @Override
    @Transactional
    public ReservaDTO actualizarReserva(Integer idReserva, ReservaRequestDTO requestDTO) {
        Reserva reservaExistente = obtenerReservaEntidadPorId(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));

        if (requestDTO.getIdCliente() != null && !requestDTO.getIdCliente().equals(reservaExistente.getCliente().getIdCliente())) {
            throw new IllegalArgumentException("No se puede cambiar el cliente de una reserva existente.");
        }
        
        Habitacion habitacionOriginal = reservaExistente.getHabitacion();
        LocalDate llegadaOriginal = reservaExistente.getFechaLlegadaEstadia();
        LocalDate salidaOriginal = reservaExistente.getFechaSalidaEstadia();

        Habitacion habitacionNueva = (requestDTO.getNumeroHabitacion() != null) ?
            habitacionRepositorio.findById(requestDTO.getNumeroHabitacion())
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con número: " + requestDTO.getNumeroHabitacion()))
            : habitacionOriginal;
        
        LocalDate llegadaNueva = requestDTO.getFechaLlegadaEstadia() != null ? requestDTO.getFechaLlegadaEstadia() : llegadaOriginal;
        LocalDate salidaNueva = requestDTO.getFechaSalidaEstadia() != null ? requestDTO.getFechaSalidaEstadia() : salidaOriginal;

        if (llegadaNueva.isAfter(salidaNueva) || llegadaNueva.isEqual(salidaNueva)) {
            throw new IllegalArgumentException("Fechas de estadía inválidas al actualizar.");
        }

        if (!habitacionNueva.getNumeroHabitacion().equals(habitacionOriginal.getNumeroHabitacion()) ||
            !llegadaNueva.isEqual(llegadaOriginal) || !salidaNueva.isEqual(salidaOriginal)) {
            
            List<Reserva> reservasSolapadas = reservaRepositorio.findReservasActivasSolapadas(
                habitacionNueva.getNumeroHabitacion(), llegadaNueva, salidaNueva)
                .stream().filter(r -> !r.getIdReserva().equals(idReserva)).collect(Collectors.toList());

            if (!reservasSolapadas.isEmpty()) {
                throw new IllegalStateException("La habitación " + habitacionNueva.getNumeroHabitacion() + " no está disponible para las nuevas fechas/habitación.");
            }
            reservaExistente.setHabitacion(habitacionNueva);
            reservaExistente.setFechaLlegadaEstadia(llegadaNueva);
            reservaExistente.setFechaSalidaEstadia(salidaNueva);
        }

        if (requestDTO.getTipoPago() != null) {
            try {
                reservaExistente.setTipoPago(Reserva.TipoPagoReserva.valueOf(requestDTO.getTipoPago().toUpperCase()));
            } catch (IllegalArgumentException e) { /* Mantener el tipo de pago existente si el nuevo es inválido o no se proporciona */ }
        }
        if (requestDTO.getEstado() != null && !requestDTO.getEstado().isBlank()) {
             try {
                Reserva.EstadoReserva nuevoEstado = Reserva.EstadoReserva.valueOf(requestDTO.getEstado().toUpperCase());
                // Usar el método específico para cambio de estado si la lógica es más compleja
                // return cambiarEstadoReserva(idReserva, nuevoEstado, requestDTO.getTipoPago());
                reservaExistente.setEstado(nuevoEstado);
             } catch (IllegalArgumentException e) { /* Mantener estado existente si el nuevo es inválido */ }
        }
        // No se suele cambiar la fecha de creación de la reserva (fechaReserva)

        Reserva actualizada = reservaRepositorio.save(reservaExistente);
        return convertirADTO(actualizada);
    }

    @Override
    @Transactional
    public void cancelarReserva(Integer idReserva) {
        // Llama al método más detallado para cambiar estado
        cambiarEstadoReserva(idReserva, Reserva.EstadoReserva.CANCELADA, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorCliente(Integer idCliente) {
        return reservaRepositorio.findByClienteIdCliente(idCliente).stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorHabitacion(Integer numeroHabitacion) {
        return reservaRepositorio.findByHabitacionNumeroHabitacion(numeroHabitacion).stream().map(this::convertirADTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasActivasParaHabitacionEnFechas(Integer numeroHabitacion, LocalDate fechaLlegada, LocalDate fechaSalida) {
        if (fechaLlegada == null || fechaSalida == null || fechaLlegada.isAfter(fechaSalida) || fechaLlegada.isEqual(fechaSalida)) {
            throw new IllegalArgumentException("Fechas de llegada y salida inválidas.");
        }
        return reservaRepositorio.findReservasActivasSolapadas(numeroHabitacion, fechaLlegada, fechaSalida)
            .stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorRangoDeFechaCreacion(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Rango de fechas de creación inválido.");
        }
        return reservaRepositorio.findByFechaReservaBetween(fechaInicio, fechaFin).stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}