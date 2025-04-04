package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.*;
import entornos.hotelflow.hotel_flow.repositorio.*;
import jakarta.persistence.EntityNotFoundException; // Excepción estándar de JPA
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaServicio implements IReservaServicio {

    @Autowired
    private ReservaRepositorio reservaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private HabitacionRepositorio habitacionRepositorio;

    @Override
    @Transactional 
    public ReservaDTO crearReserva(ReservaRequestDTO reservaRequest) {
        // 1. Validar fechas
        if (reservaRequest.getFechaSalida().isBefore(reservaRequest.getFechaEntrada()) ||
            reservaRequest.getFechaSalida().isEqual(reservaRequest.getFechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        // 2. Buscar Usuario y Habitación (lanzar excepción si no existen)
        Usuario usuario = usuarioRepositorio.findById(reservaRequest.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + reservaRequest.getIdUsuario()));

        Habitacion habitacion = habitacionRepositorio.findById(reservaRequest.getIdHabitacion())
                .orElseThrow(() -> new EntityNotFoundException("Habitación no encontrada con ID: " + reservaRequest.getIdHabitacion()));

        // 3. Verificar disponibilidad de la habitación
        List<Reserva> solapadas = reservaRepositorio.findReservasActivasSolapadas(
                habitacion.getIdHabitacion(),
                reservaRequest.getFechaEntrada(),
                reservaRequest.getFechaSalida()
        );

        if (!solapadas.isEmpty()) {
            throw new IllegalStateException("La habitación no está disponible para las fechas seleccionadas.");
        }


        // 4. Calcular total (tarifa base * noches)
        long noches = ChronoUnit.DAYS.between(reservaRequest.getFechaEntrada(), reservaRequest.getFechaSalida()) + 1;
        if (noches <= 0) noches = 1; // Mínimo 1 noche
        BigDecimal total = habitacion.getTarifaBase().multiply(BigDecimal.valueOf(noches));

        // 5. Crear la entidad Reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuario(usuario);
        nuevaReserva.setHabitacion(habitacion);
        nuevaReserva.setFechaEntrada(reservaRequest.getFechaEntrada());
        nuevaReserva.setFechaSalida(reservaRequest.getFechaSalida());
        nuevaReserva.setTotalReserva(total);
        // El estado y fecha_creacion se manejan con @PrePersist

        // 6. Guardar la reserva
        Reserva reservaGuardada = reservaRepositorio.save(nuevaReserva);

        // 7. Convertir a DTO y devolver
        return convertirAReservaDTO(reservaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaDTO buscarReservaPorId(Integer idReserva) {
        Reserva reserva = reservaRepositorio.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + idReserva));
        return convertirAReservaDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarReservasPorUsuario(Long idUsuario) {
        // Validar si el usuario existe primero si es necesario
        if (!usuarioRepositorio.existsById(idUsuario)) {
             throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        return reservaRepositorio.findByUsuarioIdUsuario(idUsuario).stream()
                .map(this::convertirAReservaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarTodasLasReservas() {
         return reservaRepositorio.findAll().stream()
                .map(this::convertirAReservaDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ReservaDTO cancelarReserva(Integer idReserva) {
        Reserva reserva = reservaRepositorio.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + idReserva));

        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya está cancelada.");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        Reserva reservaCancelada = reservaRepositorio.save(reserva);

        return convertirAReservaDTO(reservaCancelada);
    }

    @Override
    @Transactional
    public ReservaDTO confirmarReserva(Integer idReserva) {
        Reserva reserva = reservaRepositorio.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + idReserva));

        if (reserva.getEstado() == Reserva.EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("La reserva ya está CONFIRMADA.");
        }

        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        Reserva reservaConfirmada = reservaRepositorio.save(reserva);

        return convertirAReservaDTO(reservaConfirmada);
    } 
    

    // --- Métodos privados de conversión ---

    private ReservaDTO convertirAReservaDTO(Reserva reserva) {

        return new ReservaDTO(
                reserva.getIdReserva(),
                reserva.getUsuario().getIdUsuario(),
                reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido(),
                reserva.getHabitacion().getIdHabitacion(),
                reserva.getHabitacion().getNumero(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getEstado().name(), 
                reserva.getTotalReserva(),
                reserva.getFechaCreacion()
        );
    }

}