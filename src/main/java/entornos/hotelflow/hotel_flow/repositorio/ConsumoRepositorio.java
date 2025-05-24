package entornos.hotelflow.hotel_flow.repositorio;

import entornos.hotelflow.hotel_flow.modelos.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsumoRepositorio extends JpaRepository<Consumo, Integer> {
    List<Consumo> findByClienteIdCliente(Integer idCliente);
    List<Consumo> findByProductoIdProducto(Integer idProducto);
    List<Consumo> findByFechaConsumo(LocalDate fechaConsumo);
    List<Consumo> findByClienteIdClienteAndFechaConsumoBetween(Integer idCliente, LocalDate inicio, LocalDate fin);
    List<Consumo> findByClienteIdClienteAndCargadoAHabitacionFalse(Integer idCliente);
}