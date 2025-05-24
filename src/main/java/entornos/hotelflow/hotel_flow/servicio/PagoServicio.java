package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Cliente;
import entornos.hotelflow.hotel_flow.modelos.Habitacion;
import entornos.hotelflow.hotel_flow.modelos.Pago;
import entornos.hotelflow.hotel_flow.modelos.Reserva;
import entornos.hotelflow.hotel_flow.repositorio.ClienteRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.HabitacionRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.PagoRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.ReservaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagoServicio implements IPagoServicio {

    @Autowired
    private PagoRepositorio pagoRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private HabitacionRepositorio habitacionRepositorio;
    
    @Autowired
    private ReservaRepositorio reservaRepositorio; // Para actualizar estado de reserva si aplica

    @Override
    @Transactional(readOnly = true)
    public List<Pago> listarTodosLosPagos() {
        return pagoRepositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> buscarPagoPorId(Integer idPago) {
        return pagoRepositorio.findById(idPago);
    }

    @Override
    @Transactional
    public Pago registrarPago(Pago pago) { // Asume que pago llega con IDs
        Cliente cliente = clienteRepositorio.findById(pago.getCliente().getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el pago."));
        Habitacion habitacion = habitacionRepositorio.findById(pago.getHabitacion().getNumeroHabitacion())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada para el pago."));

        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }
        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDate.now());
        }
        pago.setCliente(cliente);
        pago.setHabitacion(habitacion);
        
        Pago nuevoPago = pagoRepositorio.save(pago);
        
        // Opcional: Si un pago cubre una reserva, actualizar estado de la reserva.
        // Esto requiere lógica adicional para identificar la reserva asociada.
        // Por ejemplo, si el pago está asociado a una reserva específica:
        // List<Reserva> reservas = reservaRepositorio.findByClienteIdClienteAndHabitacionNumeroHabitacionAndEstado(
        //         cliente.getIdCliente(), habitacion.getNumeroHabitacion(), Reserva.EstadoReserva.NO_PAGADA);
        // if (!reservas.isEmpty()) {
        //     Reserva reservaAPagar = reservas.get(0); // Tomar la más reciente o adecuada
        //     // Comparar monto, etc.
        //     reservaAPagar.setEstado(Reserva.EstadoReserva.PAGADA);
        //     reservaAPagar.setTipoPago(Pago.MetodoPagoOpciones.valueOf(pago.getMetodoPago().name())); // Adaptar enum
        //     reservaRepositorio.save(reservaAPagar);
        // }

        return nuevoPago;
    }

    @Override
    @Transactional
    public void anularPago(Integer idPago) {
        Pago pago = pagoRepositorio.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con id: " + idPago));
        // Aquí iría la lógica de anulación:
        // - Marcar el pago como anulado (si tienes un campo estado en Pago).
        // - O eliminarlo si las reglas de negocio lo permiten.
        // - Revertir el estado de la reserva si el pago la había marcado como PAGADA.
        System.out.println("Lógica de anulación de pago " + idPago + " pendiente de implementación detallada.");
        pagoRepositorio.delete(pago); // Ejemplo simple de eliminación
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> buscarPagosPorCliente(Integer idCliente) {
        return pagoRepositorio.findByClienteIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> buscarPagosPorHabitacion(Integer numeroHabitacion) {
        return pagoRepositorio.findByHabitacionNumeroHabitacion(numeroHabitacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> buscarPagosPorFecha(LocalDate fecha) {
        return pagoRepositorio.findByFechaPagoBetween(fecha, fecha); // Para un solo día
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagadoPorCliente(Integer idCliente) {
        return pagoRepositorio.findByClienteIdCliente(idCliente).stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}