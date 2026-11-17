package jardineria.domain.ports.out;

import jardineria.domain.model.Pago;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Pago.
 */
public interface PagoRepositoryPort {
    Pago save(Pago pago);
    Optional<Pago> findById(String idTransaccion);
    List<Pago> findAll();
    List<Pago> findByCodigoCliente(Integer codigoCliente);
    Pago update(Pago pago);
    void deleteById(String idTransaccion);
}

