package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.Empleado;
import jardineria.domain.model.Oficina;
import jardineria.domain.ports.out.EmpleadoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura para la persistencia de Empleado mediante JDBC puro.
 */
@Slf4j
public class EmpleadoRepositoryJdbc implements EmpleadoRepositoryPort {

    @Override
    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleado (codigo_empleado, nombre, apellido1, apellido2, extension, email, codigo_oficina, codigo_jefe, puesto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, empleado.getCodigoEmpleado());
            ps.setString(2, empleado.getNombre());
            ps.setString(3, empleado.getApellido1());
            ps.setString(4, empleado.getApellido2());
            ps.setString(5, empleado.getExtension());
            ps.setString(6, empleado.getEmail());
            ps.setString(7, empleado.getOficina() != null ? empleado.getOficina().getCodigoOficina() : null);

            if (empleado.getJefe() != null && empleado.getJefe().getCodigoEmpleado() != null) {
                ps.setInt(8, empleado.getJefe().getCodigoEmpleado());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setString(9, empleado.getPuesto());

            ps.executeUpdate();
            return empleado;
        } catch (SQLException e) {
            log.error("Error al guardar empleado con código {}: {}", empleado.getCodigoEmpleado(), e.getMessage());
            throw new PersistenceException("Error al guardar el empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Empleado> findById(Integer codigoEmpleado) {
        String sql = "SELECT codigo_empleado, nombre, apellido1, apellido2, extension, email, codigo_oficina, codigo_jefe, puesto " +
                     "FROM empleado WHERE codigo_empleado = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmpleado(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar empleado con código {}: {}", codigoEmpleado, e.getMessage());
            throw new PersistenceException("Error al buscar el empleado por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Empleado> findAll() {
        String sql = "SELECT codigo_empleado, nombre, apellido1, apellido2, extension, email, codigo_oficina, codigo_jefe, puesto FROM empleado";
        List<Empleado> empleados = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapResultSetToEmpleado(rs));
            }
            return empleados;
        } catch (SQLException e) {
            log.error("Error al listar todos los empleados: {}", e.getMessage());
            throw new PersistenceException("Error al listar empleados: " + e.getMessage(), e);
        }
    }

    @Override
    public Empleado update(Empleado empleado) {
        String sql = "UPDATE empleado SET nombre = ?, apellido1 = ?, apellido2 = ?, extension = ?, email = ?, " +
                     "codigo_oficina = ?, codigo_jefe = ?, puesto = ? WHERE codigo_empleado = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido1());
            ps.setString(3, empleado.getApellido2());
            ps.setString(4, empleado.getExtension());
            ps.setString(5, empleado.getEmail());
            ps.setString(6, empleado.getOficina() != null ? empleado.getOficina().getCodigoOficina() : null);

            if (empleado.getJefe() != null && empleado.getJefe().getCodigoEmpleado() != null) {
                ps.setInt(7, empleado.getJefe().getCodigoEmpleado());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setString(8, empleado.getPuesto());
            ps.setInt(9, empleado.getCodigoEmpleado());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el empleado con código {} para actualizar", empleado.getCodigoEmpleado());
            }
            return empleado;
        } catch (SQLException e) {
            log.error("Error al actualizar empleado con código {}: {}", empleado.getCodigoEmpleado(), e.getMessage());
            throw new PersistenceException("Error al actualizar el empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer codigoEmpleado) {
        String sql = "DELETE FROM empleado WHERE codigo_empleado = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoEmpleado);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar el empleado con código {}: {}", codigoEmpleado, e.getMessage());
            throw new PersistenceException("Error al eliminar el empleado: " + e.getMessage(), e);
        }
    }

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        String codOficina = rs.getString("codigo_oficina");
        Oficina oficina = codOficina != null ? Oficina.builder().codigoOficina(codOficina).build() : null;

        int codJefe = rs.getInt("codigo_jefe");
        Empleado jefe = rs.wasNull() ? null : Empleado.builder().codigoEmpleado(codJefe).build();

        return Empleado.builder()
                .codigoEmpleado(rs.getInt("codigo_empleado"))
                .nombre(rs.getString("nombre"))
                .apellido1(rs.getString("apellido1"))
                .apellido2(rs.getString("apellido2"))
                .extension(rs.getString("extension"))
                .email(rs.getString("email"))
                .oficina(oficina)
                .jefe(jefe)
                .puesto(rs.getString("puesto"))
                .build();
    }
}

