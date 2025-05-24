package entornos.hotelflow.hotel_flow.servicio;

import entornos.hotelflow.hotel_flow.modelos.Inventario;
import entornos.hotelflow.hotel_flow.repositorio.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioServicio implements IInventarioServicio {

    @Autowired
    private InventarioRepositorio inventarioRepositorio;

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> listarTodoElInventario() {
        return inventarioRepositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventario> buscarProductoPorId(Integer idProducto) {
        return inventarioRepositorio.findById(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventario> buscarProductoPorNombre(String nombre) {
        return inventarioRepositorio.findByNombre(nombre);
    }

    @Override
    @Transactional
    public Inventario guardarProducto(Inventario producto) {
        if (producto.getNombre() != null && inventarioRepositorio.findByNombre(producto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }
        if (producto.getCantidad() == null || producto.getCantidad() < 0) {
            producto.setCantidad(0);
        }
        if (producto.getPrecio() == null || producto.getPrecio().doubleValue() < 0) {
            throw new IllegalArgumentException("El precio del producto no puede ser negativo.");
        }
        return inventarioRepositorio.save(producto);
    }

    @Override
    @Transactional
    public Inventario actualizarProducto(Integer idProducto, Inventario productoDetalles) {
        Inventario productoExistente = inventarioRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));

        if (productoDetalles.getNombre() != null && !productoDetalles.getNombre().equals(productoExistente.getNombre())) {
            if (inventarioRepositorio.findByNombre(productoDetalles.getNombre()).isPresent()) {
                throw new IllegalArgumentException("El nuevo nombre del producto ya existe: " + productoDetalles.getNombre());
            }
            productoExistente.setNombre(productoDetalles.getNombre());
        }
        if (productoDetalles.getPrecio() != null && productoDetalles.getPrecio().doubleValue() >= 0) {
            productoExistente.setPrecio(productoDetalles.getPrecio());
        }
        if (productoDetalles.getCantidad() != null && productoDetalles.getCantidad() >= 0) {
             productoExistente.setCantidad(productoDetalles.getCantidad());
        }
        return inventarioRepositorio.save(productoExistente);
    }

    @Override
    @Transactional
    public Inventario ajustarCantidadProducto(Integer idProducto, int cantidadAjuste) {
        Inventario producto = inventarioRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + idProducto));
        int nuevaCantidad = producto.getCantidad() + cantidadAjuste;
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("No hay suficiente stock para realizar el ajuste. Stock actual: " + producto.getCantidad());
        }
        producto.setCantidad(nuevaCantidad);
        return inventarioRepositorio.save(producto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer idProducto) {
        if (!inventarioRepositorio.existsById(idProducto)) {
            throw new RuntimeException("Producto no encontrado con id: " + idProducto);
        }
        // Considerar si hay consumos asociados o si se debe hacer borrado lógico.
        inventarioRepositorio.deleteById(idProducto);
    }
}