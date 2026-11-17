package jardineria.application.ports.in;

import java.io.File;

/**
 * Puerto de entrada para los casos de uso de exportación de reportes de negocio.
 * Permite a la interfaz gráfica solicitar reportes sin acoplarse a formatos o archivos.
 */
public interface ReporteUseCasePort {

    void exportarClientes(File archivoDestino);

    void exportarProductos(File archivoDestino);

    void exportarPedidos(File archivoDestino);
}

