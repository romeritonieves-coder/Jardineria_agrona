package jardineria.application.ports.in;

import jardineria.domain.model.Pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Pedidos.
 */
public interface PedidoUseCasePort {
    Pedido crearPedido(Pedido pedido);
    Optional<Pedido> obtenerPedidoPorId(Integer codigoPedido);
    List<Pedido> listarPedidos();
    List<Pedido> listarPedidosPorCliente(Integer codigoCliente);
    Pedido entregarPedido(Integer codigoPedido, LocalDate fechaEntrega);
    void cancelarPedido(Integer codigoPedido);
}

