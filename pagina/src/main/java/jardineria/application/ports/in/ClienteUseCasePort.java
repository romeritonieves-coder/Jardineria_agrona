package jardineria.application.ports.in;

import jardineria.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Clientes.
 */
public interface ClienteUseCasePort {
    Cliente registrarCliente(Cliente cliente);
    default Cliente crearCliente(Cliente cliente) {
        return registrarCliente(cliente);
    }
    Cliente actualizarCliente(Cliente cliente);
    Optional<Cliente> obtenerClientePorId(Integer codigoCliente);
    List<Cliente> listarTodosLosClientes();
    default List<Cliente> obtenerTodosLosClientes() {
        return listarTodosLosClientes();
    }
    void eliminarCliente(Integer codigoCliente);
}

