package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.PagoUseCasePort;
import jardineria.domain.model.Pago;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.PagoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para la gestión de Pagos.
 */
@Slf4j
@RequiredArgsConstructor
public class PagoService implements PagoUseCasePort {

    private final PagoRepositoryPort pagoRepository;
    private final ClienteRepositoryPort clienteRepository;

    @Override
    public Pago registrarPago(Pago pago) {
        log.info("Iniciando registro de pago con ID de transacción: {}", pago.getIdTransaccion());

        if (pago.getIdTransaccion() == null || pago.getIdTransaccion().trim().isEmpty()) {
            throw new BusinessRuleException("El ID de transacción del pago es obligatorio.");
        }

        if (pagoRepository.findById(pago.getIdTransaccion()).isPresent()) {
            throw new BusinessRuleException("Ya existe un pago registrado con el ID de transacción: " + pago.getIdTransaccion());
        }

        if (pago.getCliente() == null || pago.getCliente().getCodigoCliente() == null) {
            throw new BusinessRuleException("El pago debe tener un cliente asignado.");
        }

        Integer codCliente = pago.getCliente().getCodigoCliente();
        clienteRepository.findById(codCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codCliente + " no existe."));

        if (pago.getFormaPago() == null || pago.getFormaPago().trim().isEmpty()) {
            throw new BusinessRuleException("La forma de pago es obligatoria (ej. PayPal, Tarjeta, Transferencia).");
        }

        if (pago.getTotal() == null || pago.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El monto total del pago debe ser mayor a cero.");
        }

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDate.now());
        }

        Pago guardado = pagoRepository.save(pago);
        log.info("Pago registrado exitosamente. Transacción: {}, Monto: {}", guardado.getIdTransaccion(), guardado.getTotal());
        return guardado;
    }

    @Override
    public Optional<Pago> obtenerPagoPorId(String idTransaccion) {
        return pagoRepository.findById(idTransaccion);
    }

    @Override
    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    @Override
    public List<Pago> listarPagosPorCliente(Integer codigoCliente) {
        clienteRepository.findById(codigoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codigoCliente + " no existe."));

        return pagoRepository.findByCodigoCliente(codigoCliente);
    }

    @Override
    public BigDecimal calcularTotalPagadoPorCliente(Integer codigoCliente) {
        clienteRepository.findById(codigoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con código " + codigoCliente + " no existe."));

        List<Pago> pagos = pagoRepository.findByCodigoCliente(codigoCliente);
        return pagos.stream()
                .map(Pago::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void eliminarPago(String idTransaccion) {
        log.info("Solicitud para eliminar el pago con ID de transacción: {}", idTransaccion);

        pagoRepository.findById(idTransaccion)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Pago no encontrado: " + idTransaccion));

        pagoRepository.deleteById(idTransaccion);
        log.info("Pago con ID de transacción '{}' eliminado exitosamente.", idTransaccion);
    }
}

