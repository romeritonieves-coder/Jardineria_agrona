package jardineria.domain.ports.out;

import jardineria.domain.model.Empleado;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las operaciones de persistencia de la entidad Empleado.
 */
public interface EmpleadoRepositoryPort {
    Empleado save(Empleado empleado);
    Optional<Empleado> findById(Integer codigoEmpleado);
    List<Empleado> findAll();
    Empleado update(Empleado empleado);
    void deleteById(Integer codigoEmpleado);
}

