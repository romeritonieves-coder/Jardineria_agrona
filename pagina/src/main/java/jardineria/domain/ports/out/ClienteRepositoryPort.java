package jardineria.domain.ports.out;

import jardineria.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Cliente.
 */
public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Integer codigoCliente);
    List<Cliente> findAll();
    Cliente update(Cliente cliente);
    void deleteById(Integer codigoCliente);
}

