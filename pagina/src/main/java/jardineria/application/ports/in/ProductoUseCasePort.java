package jardineria.application.ports.in;

import jardineria.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Productos.
 */
public interface ProductoUseCasePort {
    Producto registrarProducto(Producto producto);
    Producto actualizarProducto(Producto producto);
    Optional<Producto> obtenerProductoPorId(String codigoProducto);
    List<Producto> listarProductos();
    List<Producto> listarProductosPorGama(String gama);
    Producto actualizarStock(String codigoProducto, int nuevoStock);
    void eliminarProducto(String codigoProducto);
}

