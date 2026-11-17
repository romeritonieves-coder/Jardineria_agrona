package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.Cliente;
import jardineria.domain.model.Empleado;
import jardineria.domain.ports.out.ClienteRepositoryPort;
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
 * Adaptador de infraestructura para la persistencia de Cliente mediante JDBC puro.
 */
@Slf4j
public class ClienteRepositoryJdbc implements ClienteRepositoryPort {

    @Override
    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO cliente (codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, linea_direccion2, ciudad, region, pais, codigo_postal, " +
                     "codigo_empleado_rep_ventas, limite_credito) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, cliente.getCodigoCliente());
            ps.setString(2, cliente.getNombreCliente());
            ps.setString(3, cliente.getNombreContacto());
            ps.setString(4, cliente.getApellidoContacto());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getFax());
            ps.setString(7, cliente.getLineaDireccion1());
            ps.setString(8, cliente.getLineaDireccion2());
            ps.setString(9, cliente.getCiudad());
            ps.setString(10, cliente.getRegion());
            ps.setString(11, cliente.getPais());
            ps.setString(12, cliente.getCodigoPostal());

            if (cliente.getEmpleadoRepVentas() != null && cliente.getEmpleadoRepVentas().getCodigoEmpleado() != null) {
                ps.setInt(13, cliente.getEmpleadoRepVentas().getCodigoEmpleado());
            } else {
                ps.setNull(13, Types.INTEGER);
            }

            if (cliente.getLimiteCredito() != null) {
                ps.setBigDecimal(14, cliente.getLimiteCredito());
            } else {
                ps.setNull(14, Types.DECIMAL);
            }

            ps.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            log.error("Error al guardar cliente con código {}: {}", cliente.getCodigoCliente(), e.getMessage());
            throw new PersistenceException("Error al guardar el cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> findById(Integer codigoCliente) {
        String sql = "SELECT codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, linea_direccion2, ciudad, region, pais, codigo_postal, " +
                     "codigo_empleado_rep_ventas, limite_credito " +
                     "FROM cliente WHERE codigo_cliente = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar cliente con código {}: {}", codigoCliente, e.getMessage());
            throw new PersistenceException("Error al buscar el cliente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> findAll() {
        String sql = "SELECT codigo_cliente, nombre_cliente, nombre_contacto, apellido_contacto, " +
                     "telefono, fax, linea_direccion1, linea_direccion2, ciudad, region, pais, codigo_postal, " +
                     "codigo_empleado_rep_ventas, limite_credito FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
            return clientes;
        } catch (SQLException e) {
            log.error("Error al listar todos los clientes: {}", e.getMessage());
            throw new PersistenceException("Error al listar clientes: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente update(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre_cliente = ?, nombre_contacto = ?, apellido_contacto = ?, " +
                     "telefono = ?, fax = ?, linea_direccion1 = ?, linea_direccion2 = ?, ciudad = ?, region = ?, " +
                     "pais = ?, codigo_postal = ?, codigo_empleado_rep_ventas = ?, limite_credito = ? " +
                     "WHERE codigo_cliente = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombreCliente());
            ps.setString(2, cliente.getNombreContacto());
            ps.setString(3, cliente.getApellidoContacto());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getFax());
            ps.setString(6, cliente.getLineaDireccion1());
            ps.setString(7, cliente.getLineaDireccion2());
            ps.setString(8, cliente.getCiudad());
            ps.setString(9, cliente.getRegion());
            ps.setString(10, cliente.getPais());
            ps.setString(11, cliente.getCodigoPostal());

            if (cliente.getEmpleadoRepVentas() != null && cliente.getEmpleadoRepVentas().getCodigoEmpleado() != null) {
                ps.setInt(12, cliente.getEmpleadoRepVentas().getCodigoEmpleado());
            } else {
                ps.setNull(12, Types.INTEGER);
            }

            if (cliente.getLimiteCredito() != null) {
                ps.setBigDecimal(13, cliente.getLimiteCredito());
            } else {
                ps.setNull(13, Types.DECIMAL);
            }

            ps.setInt(14, cliente.getCodigoCliente());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el cliente con código {} para actualizar", cliente.getCodigoCliente());
            }
            return cliente;
        } catch (SQLException e) {
            log.error("Error al actualizar cliente con código {}: {}", cliente.getCodigoCliente(), e.getMessage());
            throw new PersistenceException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer codigoCliente) {
        String sql = "DELETE FROM cliente WHERE codigo_cliente = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoCliente);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar el cliente con código {}: {}", codigoCliente, e.getMessage());
            throw new PersistenceException("Error al eliminar el cliente: " + e.getMessage(), e);
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        int codRep = rs.getInt("codigo_empleado_rep_ventas");
        Empleado repVentas = rs.wasNull() ? null : Empleado.builder().codigoEmpleado(codRep).build();

        return Cliente.builder()
                .codigoCliente(rs.getInt("codigo_cliente"))
                .nombreCliente(rs.getString("nombre_cliente"))
                .nombreContacto(rs.getString("nombre_contacto"))
                .apellidoContacto(rs.getString("apellido_contacto"))
                .telefono(rs.getString("telefono"))
                .fax(rs.getString("fax"))
                .lineaDireccion1(rs.getString("linea_direccion1"))
                .lineaDireccion2(rs.getString("linea_direccion2"))
                .ciudad(rs.getString("ciudad"))
                .region(rs.getString("region"))
                .pais(rs.getString("pais"))
                .codigoPostal(rs.getString("codigo_postal"))
                .empleadoRepVentas(repVentas)
                .limiteCredito(rs.getBigDecimal("limite_credito"))
                .build();
    }
}

