package jardineria.domain.ports.out;

import jardineria.domain.model.GamaProducto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad GamaProducto.
 */
public interface GamaProductoRepositoryPort {
    GamaProducto save(GamaProducto gamaProducto);
    Optional<GamaProducto> findById(String gama);
    List<GamaProducto> findAll();
    GamaProducto update(GamaProducto gamaProducto);
    void deleteById(String gama);
}

