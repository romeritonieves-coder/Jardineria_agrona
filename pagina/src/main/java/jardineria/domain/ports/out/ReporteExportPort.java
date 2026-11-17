package jardineria.domain.ports.out;

import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;

import java.io.File;
import java.util.List;

/**
 * Puerto de salida para la exportación de reportes y hojas de cálculo (Arquitectura Hexagonal).
 * Desacoplado de la tecnología de generación física del archivo (Apache POI, etc.).
 */
public interface ReporteExportPort {

    void exportarClientesExcel(List<Cliente> clientes, File archivoDestino);

    void exportarProductosExcel(List<Producto> productos, File archivoDestino);

    void exportarPedidosExcel(List<Pedido> pedidos, File archivoDestino);
}

