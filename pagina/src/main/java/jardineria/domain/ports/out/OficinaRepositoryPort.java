package jardineria.domain.ports.out;

import jardineria.domain.model.Oficina;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Oficina.
 */
public interface OficinaRepositoryPort {
    Oficina save(Oficina oficina);
    Optional<Oficina> findById(String codigoOficina);
    List<Oficina> findAll();
    Oficina update(Oficina oficina);
    void deleteById(String codigoOficina);
}

