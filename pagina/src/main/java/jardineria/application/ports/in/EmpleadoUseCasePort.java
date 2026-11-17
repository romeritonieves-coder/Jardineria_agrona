package jardineria.application.ports.in;

import jardineria.domain.model.Empleado;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Casos de Uso) para la gestión de Empleados.
 */
public interface EmpleadoUseCasePort {
    Empleado registrarEmpleado(Empleado empleado);
    Empleado actualizarEmpleado(Empleado empleado);
    Optional<Empleado> obtenerEmpleadoPorId(Integer codigoEmpleado);
    List<Empleado> listarEmpleados();
    List<Empleado> listarSubordinados(Integer codigoJefe);
    void eliminarEmpleado(Integer codigoEmpleado);
}

