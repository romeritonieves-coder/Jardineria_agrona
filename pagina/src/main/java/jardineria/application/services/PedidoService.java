package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.PedidoUseCasePort;
import jardineria.domain.model.DetallePedido;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
import jardineria.domain.ports.out.PedidoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que orquesta la creación, consulta y ciclo de vida de los Pedidos.
 * Aplica reglas de negocio sobre stock, fechas y estados.
 */
@Slf4j
@RequiredArgsConstructor
public class PedidoService implements PedidoUseCasePort {

    private final PedidoRepositoryPort pedidoRepository;
    private final DetallePedidoRepositoryPort detallePedidoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final ProductoRepositoryPort productoRepository;

    @Override
    public Pedido crearPedido(Pedido pedido) {
        log.info("Iniciando creación de pedido con código: {}", pedido.getCodigoPedido());

        if (pedido.getCodigoPedido() == null) {
            throw new BusinessRuleException("El código del pedido es obligatorio.");
        }

        if (pedidoRepository.findById(pedido.getCodigoPedido()).isPresent()) {
            throw new BusinessRuleException("Ya existe un pedido registrado con el código: " + pedido.getCodigoPedido());
        }

        if (pedido.getCliente() == null || pedido.getCliente().getCodigoCliente() == null) {
            throw new BusinessRuleException("El pedido debe tener un cliente asignado.");
        }

        Integer codCliente = pedido.getCliente().getCodigoCliente();
        clienteRepository.findById(codCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codCliente + " no existe."));

        if (pedido.getFechaPedido() == null) {
            pedido.setFechaPedido(LocalDate.now());
        }

        if (pedido.getFechaEsperada() == null) {
            pedido.setFechaEsperada(pedido.getFechaPedido().plusDays(3));
        }

        if (pedido.getFechaEsperada().isBefore(pedido.getFechaPedido())) {
            throw new BusinessRuleException("La fecha esperada no puede ser anterior a la fecha del pedido.");
        }

        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("Pendiente");
        }

        // Guardar cabecera del pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Procesar líneas de detalle si están presentes
        List<DetallePedido> detallesGuardados = new ArrayList<>();
        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            int numeroLinea = 1;
            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalle.getProducto() == null || detalle.getProducto().getCodigoProducto() == null) {
                    throw new BusinessRuleException("Cada detalle de pedido debe incluir un producto válido.");
                }

                String codProd = detalle.getProducto().getCodigoProducto();
                Producto producto = productoRepository.findById(codProd)
                        .orElseThrow(() -> new ResourceNotFoundException("El producto con código '" + codProd + "' no existe."));

                if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                    throw new BusinessRuleException("La cantidad solicitada para el producto '" + codProd + "' debe ser mayor a cero.");
                }

                // Control y reserva de stock
                if (producto.getCantidadEnStock() < detalle.getCantidad()) {
                    throw new BusinessRuleException("Stock insuficiente para el producto '" + producto.getNombre() +
                            "'. Disponible: " + producto.getCantidadEnStock() + ", Solicitado: " + detalle.getCantidad());
                }

                // Descontar inventario
                producto.setCantidadEnStock(producto.getCantidadEnStock() - detalle.getCantidad());
                productoRepository.update(producto);

                // Configurar detalle
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(producto);
                if (detalle.getPrecioUnidad() == null) {
                    detalle.setPrecioUnidad(producto.getPrecioVenta());
                }
                if (detalle.getNumeroLinea() == null) {
                    detalle.setNumeroLinea(numeroLinea++);
                }

                detallesGuardados.add(detallePedidoRepository.save(detalle));
            }
        }

        pedidoGuardado.setDetalles(detallesGuardados);
        log.info("Pedido {} creado con éxito con {} líneas de detalle.", pedidoGuardado.getCodigoPedido(), detallesGuardados.size());
        return pedidoGuardado;
    }

    @Override
    public Optional<Pedido> obtenerPedidoPorId(Integer codigoPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(codigoPedido);
        pedidoOpt.ifPresent(p -> p.setDetalles(detallePedidoRepository.findByCodigoPedido(codigoPedido)));
        return pedidoOpt;
    }

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> listarPedidosPorCliente(Integer codigoCliente) {
        clienteRepository.findById(codigoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codigoCliente + " no existe."));

        return pedidoRepository.findAll().stream()
                .filter(p -> p.getCliente() != null && codigoCliente.equals(p.getCliente().getCodigoCliente()))
                .toList();
    }

    @Override
    public Pedido entregarPedido(Integer codigoPedido, LocalDate fechaEntrega) {
        log.info("Registrando entrega para el pedido con código: {}", codigoPedido);
        Pedido pedido = pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con código: " + codigoPedido));

        if ("Entregado".equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessRuleException("El pedido ya se encuentra entregado.");
        }

        if ("Rechazado".equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede entregar un pedido que ha sido rechazado.");
        }

        pedido.setEstado("Entregado");
        pedido.setFechaEntrega(fechaEntrega != null ? fechaEntrega : LocalDate.now());

        Pedido actualizado = pedidoRepository.update(pedido);
        log.info("Pedido {} marcado como Entregado en fecha {}.", codigoPedido, pedido.getFechaEntrega());
        return actualizado;
    }

    @Override
    public void cancelarPedido(Integer codigoPedido) {
        log.info("Iniciando cancelación del pedido con código: {}", codigoPedido);
        Pedido pedido = pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con código: " + codigoPedido));

        if ("Entregado".equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar un pedido que ya fue entregado al cliente.");
        }

        if ("Rechazado".equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessRuleException("El pedido ya se encuentra cancelado/rechazado.");
        }

        // Reintegrar stock de los productos del pedido
        List<DetallePedido> detalles = detallePedidoRepository.findByCodigoPedido(codigoPedido);
        for (DetallePedido detalle : detalles) {
            if (detalle.getProducto() != null && detalle.getProducto().getCodigoProducto() != null) {
                productoRepository.findById(detalle.getProducto().getCodigoProducto()).ifPresent(producto -> {
                    producto.setCantidadEnStock(producto.getCantidadEnStock() + detalle.getCantidad());
                    productoRepository.update(producto);
                    log.info("Stock reincorporado (+{}) para producto '{}'.", detalle.getCantidad(), producto.getCodigoProducto());
                });
            }
        }

        pedido.setEstado("Rechazado");
        pedidoRepository.update(pedido);
        log.info("Pedido {} cancelado exitosamente.", codigoPedido);
    }

    @Override
    public Pedido actualizarPedido(Pedido pedido) {
        log.info("Iniciando actualización para el pedido con código: {}", pedido.getCodigoPedido());

        if (pedido.getCodigoPedido() == null) {
            throw new BusinessRuleException("El código del pedido es requerido para la actualización.");
        }

        pedidoRepository.findById(pedido.getCodigoPedido())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con código: " + pedido.getCodigoPedido()));

        if (pedido.getCliente() == null || pedido.getCliente().getCodigoCliente() == null) {
            throw new BusinessRuleException("El pedido debe tener un cliente asignado.");
        }

        Integer codCliente = pedido.getCliente().getCodigoCliente();
        clienteRepository.findById(codCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codCliente + " no existe."));

        if (pedido.getFechaEsperada() != null && pedido.getFechaPedido() != null
                && pedido.getFechaEsperada().isBefore(pedido.getFechaPedido())) {
            throw new BusinessRuleException("La fecha esperada no puede ser anterior a la fecha del pedido.");
        }

        Pedido actualizado = pedidoRepository.update(pedido);
        log.info("Pedido {} actualizado con éxito.", actualizado.getCodigoPedido());
        return actualizado;
    }

    @Override
    public void eliminarPedido(Integer codigoPedido) {
        log.info("Solicitud para eliminar el pedido con código: {}", codigoPedido);

        pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Pedido no encontrado: " + codigoPedido));

        pedidoRepository.deleteById(codigoPedido);
        log.info("Pedido con código {} eliminado exitosamente.", codigoPedido);
    }
}

