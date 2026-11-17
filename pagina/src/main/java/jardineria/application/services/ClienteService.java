package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.ClienteUseCasePort;
import jardineria.domain.model.Cliente;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para la gestión de Clientes.
 * Aplica Inyección de Dependencias desacoplada de la infraestructura.
 */
@Slf4j
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCasePort {

    private final ClienteRepositoryPort clienteRepository;
    private final EmpleadoRepositoryPort empleadoRepository;

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        log.info("Iniciando registro de nuevo cliente con código: {}", cliente.getCodigoCliente());

        if (cliente.getCodigoCliente() == null) {
            throw new BusinessRuleException("El código del cliente es obligatorio.");
        }

        if (clienteRepository.findById(cliente.getCodigoCliente()).isPresent()) {
            throw new BusinessRuleException("Ya existe un cliente registrado con el código: " + cliente.getCodigoCliente());
        }

        validarDatosCliente(cliente);
        validarRepresentanteVentas(cliente);

        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente registrado con éxito. ID: {}", guardado.getCodigoCliente());
        return guardado;
    }

    @Override
    public Cliente actualizarCliente(Cliente cliente) {
        log.info("Iniciando actualización para el cliente con código: {}", cliente.getCodigoCliente());

        if (cliente.getCodigoCliente() == null) {
            throw new BusinessRuleException("El código del cliente es necesario para la actualización.");
        }

        clienteRepository.findById(cliente.getCodigoCliente())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con código: " + cliente.getCodigoCliente()));

        validarDatosCliente(cliente);
        validarRepresentanteVentas(cliente);

        Cliente actualizado = clienteRepository.update(cliente);
        log.info("Cliente actualizado con éxito. ID: {}", actualizado.getCodigoCliente());
        return actualizado;
    }

    @Override
    public Optional<Cliente> obtenerClientePorId(Integer codigoCliente) {
        return clienteRepository.findById(codigoCliente);
    }

    @Override
    public List<Cliente> listarTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public void eliminarCliente(Integer codigoCliente) {
        log.info("Solicitud para eliminar cliente con código: {}", codigoCliente);
        clienteRepository.findById(codigoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Cliente no encontrado: " + codigoCliente));

        clienteRepository.deleteById(codigoCliente);
        log.info("Cliente con código {} eliminado exitosamente.", codigoCliente);
    }

    private void validarDatosCliente(Cliente cliente) {
        if (cliente.getNombreCliente() == null || cliente.getNombreCliente().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre del cliente no puede estar vacío.");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new BusinessRuleException("El teléfono de contacto es obligatorio.");
        }
        if (cliente.getLimiteCredito() != null && cliente.getLimiteCredito().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("El límite de crédito no puede ser un valor negativo.");
        }
    }

    private void validarRepresentanteVentas(Cliente cliente) {
        if (cliente.getEmpleadoRepVentas() != null && cliente.getEmpleadoRepVentas().getCodigoEmpleado() != null) {
            Integer codEmpleado = cliente.getEmpleadoRepVentas().getCodigoEmpleado();
            empleadoRepository.findById(codEmpleado)
                    .orElseThrow(() -> new ResourceNotFoundException("El empleado asignado como representante de ventas (ID: " + codEmpleado + ") no existe."));
        }
    }
}

