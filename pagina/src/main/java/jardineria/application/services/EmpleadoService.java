package jardineria.application.services;

import jardineria.application.exceptions.BusinessRuleException;
import jardineria.application.exceptions.ResourceNotFoundException;
import jardineria.application.ports.in.EmpleadoUseCasePort;
import jardineria.domain.model.Empleado;
import jardineria.domain.ports.out.ClienteRepositoryPort;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import jardineria.domain.ports.out.OficinaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para la gestión de Empleados.
 */
@Slf4j
@RequiredArgsConstructor
public class EmpleadoService implements EmpleadoUseCasePort {

    private final EmpleadoRepositoryPort empleadoRepository;
    private final OficinaRepositoryPort oficinaRepository;
    private final ClienteRepositoryPort clienteRepository;

    @Override
    public Empleado registrarEmpleado(Empleado empleado) {
        log.info("Iniciando registro de empleado con código: {}", empleado.getCodigoEmpleado());

        if (empleado.getCodigoEmpleado() == null) {
            throw new BusinessRuleException("El código del empleado es obligatorio.");
        }

        if (empleadoRepository.findById(empleado.getCodigoEmpleado()).isPresent()) {
            throw new BusinessRuleException("Ya existe un empleado con el código: " + empleado.getCodigoEmpleado());
        }

        validarDatosEmpleado(empleado);
        validarOficinaYJefe(empleado);

        Empleado guardado = empleadoRepository.save(empleado);
        log.info("Empleado registrado con éxito: {}", guardado.getCodigoEmpleado());
        return guardado;
    }

    @Override
    public Empleado actualizarEmpleado(Empleado empleado) {
        log.info("Iniciando actualización para empleado: {}", empleado.getCodigoEmpleado());

        if (empleado.getCodigoEmpleado() == null) {
            throw new BusinessRuleException("El código del empleado es requerido para actualizar.");
        }

        empleadoRepository.findById(empleado.getCodigoEmpleado())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el empleado con código: " + empleado.getCodigoEmpleado()));

        validarDatosEmpleado(empleado);
        validarOficinaYJefe(empleado);

        Empleado actualizado = empleadoRepository.update(empleado);
        log.info("Empleado actualizado exitosamente: {}", actualizado.getCodigoEmpleado());
        return actualizado;
    }

    @Override
    public Optional<Empleado> obtenerEmpleadoPorId(Integer codigoEmpleado) {
        return empleadoRepository.findById(codigoEmpleado);
    }

    @Override
    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    @Override
    public List<Empleado> listarSubordinados(Integer codigoJefe) {
        return empleadoRepository.findAll().stream()
                .filter(e -> e.getJefe() != null && codigoJefe.equals(e.getJefe().getCodigoEmpleado()))
                .toList();
    }

    @Override
    public void eliminarEmpleado(Integer codigoEmpleado) {
        log.info("Solicitud para eliminar el empleado con código: {}", codigoEmpleado);

        empleadoRepository.findById(codigoEmpleado)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Empleado no encontrado: " + codigoEmpleado));

        // Validar que no tenga clientes asignados como representante de ventas
        boolean tieneClientes = clienteRepository.findAll().stream()
                .anyMatch(c -> c.getEmpleadoRepVentas() != null && codigoEmpleado.equals(c.getEmpleadoRepVentas().getCodigoEmpleado()));

        if (tieneClientes) {
            throw new BusinessRuleException("No se puede eliminar el empleado porque es representante de ventas de clientes activos.");
        }

        // Validar que no sea jefe de otros empleados
        boolean tieneSubordinados = !listarSubordinados(codigoEmpleado).isEmpty();
        if (tieneSubordinados) {
            throw new BusinessRuleException("No se puede eliminar el empleado porque tiene subordinados a su cargo.");
        }

        empleadoRepository.deleteById(codigoEmpleado);
        log.info("Empleado con código {} eliminado exitosamente.", codigoEmpleado);
    }

    private void validarDatosEmpleado(Empleado empleado) {
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre del empleado es obligatorio.");
        }
        if (empleado.getApellido1() == null || empleado.getApellido1().trim().isEmpty()) {
            throw new BusinessRuleException("El primer apellido del empleado es obligatorio.");
        }
        if (empleado.getEmail() == null || !empleado.getEmail().contains("@")) {
            throw new BusinessRuleException("El email del empleado debe ser válido y contener '@'.");
        }
        if (empleado.getExtension() == null || empleado.getExtension().trim().isEmpty()) {
            throw new BusinessRuleException("La extensión telefónica es obligatoria.");
        }
    }

    private void validarOficinaYJefe(Empleado empleado) {
        if (empleado.getOficina() == null || empleado.getOficina().getCodigoOficina() == null) {
            throw new BusinessRuleException("El empleado debe tener una oficina asignada.");
        }

        String codOficina = empleado.getOficina().getCodigoOficina();
        oficinaRepository.findById(codOficina)
                .orElseThrow(() -> new ResourceNotFoundException("La oficina con código '" + codOficina + "' no existe."));

        if (empleado.getJefe() != null && empleado.getJefe().getCodigoEmpleado() != null) {
            Integer codJefe = empleado.getJefe().getCodigoEmpleado();
            if (codJefe.equals(empleado.getCodigoEmpleado())) {
                throw new BusinessRuleException("Un empleado no puede ser su propio jefe.");
            }
            empleadoRepository.findById(codJefe)
                    .orElseThrow(() -> new ResourceNotFoundException("El jefe asignado con código " + codJefe + " no existe."));
        }
    }
}

