package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pago;
import jardineria.domain.ports.out.PagoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura para la persistencia de Pago mediante JDBC puro.
 */
@Slf4j
public class PagoRepositoryJdbc implements PagoRepositoryPort {

    @Override
    public Pago save(Pago pago) {
        String sql = "INSERT INTO pago (codigo_cliente, forma_pago, id_transaccion, fecha_pago, total) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (pago.getCliente() != null && pago.getCliente().getCodigoCliente() != null) {
                ps.setInt(1, pago.getCliente().getCodigoCliente());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, pago.getFormaPago());
            ps.setString(3, pago.getIdTransaccion());

            if (pago.getFechaPago() != null) {
                ps.setDate(4, Date.valueOf(pago.getFechaPago()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (pago.getTotal() != null) {
                ps.setBigDecimal(5, pago.getTotal());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            ps.executeUpdate();
            return pago;
        } catch (SQLException e) {
            log.error("Error al guardar pago con idTransaccion '{}': {}", pago.getIdTransaccion(), e.getMessage());
            throw new PersistenceException("Error al guardar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pago> findById(String idTransaccion) {
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total " +
                     "FROM pago WHERE id_transaccion = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idTransaccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPago(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar pago por idTransaccion '{}': {}", idTransaccion, e.getMessage());
            throw new PersistenceException("Error al buscar pago por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> findAll() {
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total FROM pago";
        List<Pago> pagos = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pagos.add(mapResultSetToPago(rs));
            }
            return pagos;
        } catch (SQLException e) {
            log.error("Error al listar todos los pagos: {}", e.getMessage());
            throw new PersistenceException("Error al listar pagos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> findByCodigoCliente(Integer codigoCliente) {
        String sql = "SELECT codigo_cliente, forma_pago, id_transaccion, fecha_pago, total " +
                     "FROM pago WHERE codigo_cliente = ? ORDER BY fecha_pago DESC";
        List<Pago> pagos = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(mapResultSetToPago(rs));
                }
            }
            return pagos;
        } catch (SQLException e) {
            log.error("Error al listar pagos para el cliente con código {}: {}", codigoCliente, e.getMessage());
            throw new PersistenceException("Error al listar pagos por código de cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Pago update(Pago pago) {
        String sql = "UPDATE pago SET codigo_cliente = ?, forma_pago = ?, fecha_pago = ?, total = ? " +
                     "WHERE id_transaccion = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (pago.getCliente() != null && pago.getCliente().getCodigoCliente() != null) {
                ps.setInt(1, pago.getCliente().getCodigoCliente());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, pago.getFormaPago());

            if (pago.getFechaPago() != null) {
                ps.setDate(3, Date.valueOf(pago.getFechaPago()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (pago.getTotal() != null) {
                ps.setBigDecimal(4, pago.getTotal());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.setString(5, pago.getIdTransaccion());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el pago con idTransaccion '{}' para actualizar", pago.getIdTransaccion());
            }
            return pago;
        } catch (SQLException e) {
            log.error("Error al actualizar pago con idTransaccion '{}': {}", pago.getIdTransaccion(), e.getMessage());
            throw new PersistenceException("Error al actualizar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String idTransaccion) {
        String sql = "DELETE FROM pago WHERE id_transaccion = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, idTransaccion);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar pago con idTransaccion '{}': {}", idTransaccion, e.getMessage());
            throw new PersistenceException("Error al eliminar pago: " + e.getMessage(), e);
        }
    }

    private Pago mapResultSetToPago(ResultSet rs) throws SQLException {
        int codCliente = rs.getInt("codigo_cliente");
        Cliente cliente = rs.wasNull() ? null : Cliente.builder().codigoCliente(codCliente).build();
        Date fechaPago = rs.getDate("fecha_pago");

        return Pago.builder()
                .cliente(cliente)
                .formaPago(rs.getString("forma_pago"))
                .idTransaccion(rs.getString("id_transaccion"))
                .fechaPago(fechaPago != null ? fechaPago.toLocalDate() : null)
                .total(rs.getBigDecimal("total"))
                .build();
    }
}

