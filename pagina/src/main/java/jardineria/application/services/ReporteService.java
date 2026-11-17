package jardineria.application.services;

import jardineria.application.ports.in.ReporteUseCasePort;
import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.PedidoRepositoryPort;
import jardineria.domain.ports.out.ProductoRepositoryPort;
import jardineria.domain.ports.out.ReporteExportPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * Servicio de aplicación que orquesta la generación y exportación de reportes.
 * Conecta los repositorios de dominio con el adaptador de exportación a través de ReporteExportPort.
 */
@Slf4j
@RequiredArgsConstructor
public class ReporteService implements ReporteUseCasePort {

    private final ClienteRepositoryPort clienteRepository;
    private final ProductoRepositoryPort productoRepository;
    private final PedidoRepositoryPort pedidoRepository;
    private final ReporteExportPort reporteExportPort;

    @Override
    public void exportarClientes(File archivoDestino) {
        log.info("Procesando exportación de clientes a {}", archivoDestino.getAbsolutePath());
        List<Cliente> clientes = clienteRepository.findAll();
        reporteExportPort.exportarClientesExcel(clientes, archivoDestino);
    }

    @Override
    public void exportarProductos(File archivoDestino) {
        log.info("Procesando exportación de productos a {}", archivoDestino.getAbsolutePath());
        List<Producto> productos = productoRepository.findAll();
        reporteExportPort.exportarProductosExcel(productos, archivoDestino);
    }

    @Override
    public void exportarPedidos(File archivoDestino) {
        log.info("Procesando exportación de pedidos a {}", archivoDestino.getAbsolutePath());
        List<Pedido> pedidos = pedidoRepository.findAll();
        reporteExportPort.exportarPedidosExcel(pedidos, archivoDestino);
    }
}

