package jardineria.application.ports.in;

import jardineria.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Clientes.
 */
public interface ClienteUseCasePort {
    Cliente registrarCliente(Cliente cliente);
    Cliente actualizarCliente(Cliente cliente);
    Optional<Cliente> obtenerClientePorId(Integer codigoCliente);
    List<Cliente> listarTodosLosClientes();
    void eliminarCliente(Integer codigoCliente);
}

