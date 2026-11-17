package jardineria.application.ports.in;

import jardineria.domain.model.GamaProducto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Gamas de Producto.
 */
public interface GamaProductoUseCasePort {
    GamaProducto registrarGama(GamaProducto gama);
    GamaProducto actualizarGama(GamaProducto gama);
    Optional<GamaProducto> obtenerGamaPorId(String gama);
    List<GamaProducto> listarGamas();
    void eliminarGama(String gama);
}

