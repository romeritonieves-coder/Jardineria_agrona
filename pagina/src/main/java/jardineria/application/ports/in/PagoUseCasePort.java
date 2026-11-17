package jardineria.application.ports.in;

import jardineria.domain.model.Pago;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Pagos.
 */
public interface PagoUseCasePort {
    Pago registrarPago(Pago pago);
    Optional<Pago> obtenerPagoPorId(String idTransaccion);
    List<Pago> listarPagos();
    List<Pago> listarPagosPorCliente(Integer codigoCliente);
    BigDecimal calcularTotalPagadoPorCliente(Integer codigoCliente);
    void eliminarPago(String idTransaccion);
}

