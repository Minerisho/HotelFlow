package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Consumo;
// Considera crear ConsumoDTO y ConsumoRequestDTO
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface IConsumoServicio {
    List<Consumo> listarTodosLosConsumos();
    Optional<Consumo> buscarConsumoPorId(Integer idConsumo);
    Consumo registrarConsumo(Consumo consumo); // Podría tomar un ConsumoRequestDTO
    // No suele haber un "actualizar" consumo, más bien cancelar y volver a crear.
    void eliminarConsumo(Integer idConsumo); // Podría requerir reajustar inventario
    List<Consumo> buscarConsumosPorCliente(Integer idCliente);
    List<Consumo> buscarConsumosPorProducto(Integer idProducto);
    List<Consumo> buscarConsumosPorFecha(LocalDate fecha);
    List<Consumo> buscarConsumosPendientesPorCliente(Integer idCliente);
    Consumo marcarConsumoComoCargado(Integer idConsumo);
}