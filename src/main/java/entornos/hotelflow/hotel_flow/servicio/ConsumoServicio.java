package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Cliente;
import entornos.hotelflow.hotel_flow.modelos.Consumo;
import entornos.hotelflow.hotel_flow.modelos.Inventario;
import entornos.hotelflow.hotel_flow.repositorio.ClienteRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.ConsumoRepositorio;
import entornos.hotelflow.hotel_flow.repositorio.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConsumoServicio implements IConsumoServicio {

    @Autowired
    private ConsumoRepositorio consumoRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio; // Para validar el cliente

    @Autowired
    private InventarioRepositorio inventarioRepositorio; // Para validar producto y stock

    @Autowired
    private IInventarioServicio inventarioServicio; // Para ajustar stock

    @Override
    @Transactional(readOnly = true)
    public List<Consumo> listarTodosLosConsumos() {
        return consumoRepositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Consumo> buscarConsumoPorId(Integer idConsumo) {
        return consumoRepositorio.findById(idConsumo);
    }

    @Override
    @Transactional
    public Consumo registrarConsumo(Consumo consumo) { // Asumimos que consumo llega con ID de cliente y producto
        Cliente cliente = clienteRepositorio.findById(consumo.getCliente().getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el consumo."));
        Inventario producto = inventarioRepositorio.findById(consumo.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para el consumo."));

        if (consumo.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del consumo debe ser mayor a cero.");
        }

        // Ajustar stock
        inventarioServicio.ajustarCantidadProducto(producto.getIdProducto(), -consumo.getCantidad());

        consumo.setCliente(cliente);
        consumo.setProducto(producto);
        if (consumo.getFechaConsumo() == null) {
            consumo.setFechaConsumo(LocalDate.now());
        }
        return consumoRepositorio.save(consumo);
    }

    @Override
    @Transactional
    public void eliminarConsumo(Integer idConsumo) {
        Consumo consumo = consumoRepositorio.findById(idConsumo)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + idConsumo));
        // Reajustar stock al eliminar/cancelar consumo
        inventarioServicio.ajustarCantidadProducto(consumo.getProducto().getIdProducto(), consumo.getCantidad());
        consumoRepositorio.delete(consumo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Consumo> buscarConsumosPorCliente(Integer idCliente) {
        return consumoRepositorio.findByClienteIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Consumo> buscarConsumosPorProducto(Integer idProducto) {
        return consumoRepositorio.findByProductoIdProducto(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Consumo> buscarConsumosPorFecha(LocalDate fecha) {
        return consumoRepositorio.findByFechaConsumo(fecha);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Consumo> buscarConsumosPendientesPorCliente(Integer idCliente) {
        return consumoRepositorio.findByClienteIdClienteAndCargadoAHabitacionFalse(idCliente);
    }

    @Override
    @Transactional
    public Consumo marcarConsumoComoCargado(Integer idConsumo) {
        Consumo consumo = consumoRepositorio.findById(idConsumo)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + idConsumo));
        consumo.setCargadoAHabitacion(true);
        return consumoRepositorio.save(consumo);
    }
}