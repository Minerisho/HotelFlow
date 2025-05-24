package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Pago;
// Considera crear PagoDTO y PagoRequestDTO
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface IPagoServicio {
    List<Pago> listarTodosLosPagos();
    Optional<Pago> buscarPagoPorId(Integer idPago);
    Pago registrarPago(Pago pago); // Podría tomar un PagoRequestDTO
    // Los pagos raramente se actualizan, más bien se anulan y se crea uno nuevo.
    void anularPago(Integer idPago); // Lógica para anular un pago
    List<Pago> buscarPagosPorCliente(Integer idCliente);
    List<Pago> buscarPagosPorHabitacion(Integer numeroHabitacion);
    List<Pago> buscarPagosPorFecha(LocalDate fecha);
    BigDecimal calcularTotalPagadoPorCliente(Integer idCliente);
}