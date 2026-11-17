package jardineria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa a un Empleado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {
    private Integer codigoEmpleado;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String extension;
    private String email;
    private Oficina oficina;
    private Empleado jefe;
    private String puesto;
}

