package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Inventario;
// Considera crear InventarioDTO
import java.util.List;
import java.util.Optional;

public interface IInventarioServicio {
    List<Inventario> listarTodoElInventario();
    Optional<Inventario> buscarProductoPorId(Integer idProducto);
    Optional<Inventario> buscarProductoPorNombre(String nombre);
    Inventario guardarProducto(Inventario producto);
    Inventario actualizarProducto(Integer idProducto, Inventario productoDetalles);
    Inventario ajustarCantidadProducto(Integer idProducto, int cantidadAjuste);
    void eliminarProducto(Integer idProducto);
}