package jardineria.domain.ports.out;

import jardineria.domain.model.DetallePedido;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad DetallePedido.
 */
public interface DetallePedidoRepositoryPort {
    DetallePedido save(DetallePedido detallePedido);
    Optional<DetallePedido> findById(Integer codigoPedido, String codigoProducto);
    List<DetallePedido> findAll();
    List<DetallePedido> findByCodigoPedido(Integer codigoPedido);
    DetallePedido update(DetallePedido detallePedido);
    void deleteById(Integer codigoPedido, String codigoProducto);
}

