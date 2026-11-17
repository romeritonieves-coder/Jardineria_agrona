package jardineria.domain.ports.out;

import jardineria.domain.model.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Pedido.
 */
public interface PedidoRepositoryPort {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Integer codigoPedido);
    List<Pedido> findAll();
    Pedido update(Pedido pedido);
    void deleteById(Integer codigoPedido);
}

