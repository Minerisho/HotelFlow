package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Integer> {
    List<Pago> findByClienteIdCliente(Integer idCliente);
    List<Pago> findByHabitacionNumeroHabitacion(Integer numeroHabitacion);
    List<Pago> findByFechaPagoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Pago> findByMetodoPago(Pago.MetodoPagoOpciones metodoPago);
}