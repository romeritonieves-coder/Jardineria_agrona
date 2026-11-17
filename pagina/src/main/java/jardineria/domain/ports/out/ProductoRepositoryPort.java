package jardineria.domain.ports.out;

import jardineria.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Producto.
 */
public interface ProductoRepositoryPort {
    Producto save(Producto producto);
    Optional<Producto> findById(String codigoProducto);
    List<Producto> findAll();
    Producto update(Producto producto);
    void deleteById(String codigoProducto);
}

