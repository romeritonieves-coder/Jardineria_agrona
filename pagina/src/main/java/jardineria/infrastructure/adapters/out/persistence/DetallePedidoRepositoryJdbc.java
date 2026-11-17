package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.DetallePedido;
import jardineria.domain.model.Pedido;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.DetallePedidoRepositoryPort;
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
 * Adaptador de infraestructura para la persistencia de DetallePedido mediante JDBC puro.
 */
@Slf4j
public class DetallePedidoRepositoryJdbc implements DetallePedidoRepositoryPort {

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        String sql = "INSERT INTO detalle_pedido (codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (detallePedido.getPedido() != null && detallePedido.getPedido().getCodigoPedido() != null) {
                ps.setInt(1, detallePedido.getPedido().getCodigoPedido());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            if (detallePedido.getProducto() != null && detallePedido.getProducto().getCodigoProducto() != null) {
                ps.setString(2, detallePedido.getProducto().getCodigoProducto());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            if (detallePedido.getCantidad() != null) {
                ps.setInt(3, detallePedido.getCantidad());
            } else {
                ps.setInt(3, 0);
            }

            if (detallePedido.getPrecioUnidad() != null) {
                ps.setBigDecimal(4, detallePedido.getPrecioUnidad());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            if (detallePedido.getNumeroLinea() != null) {
                ps.setInt(5, detallePedido.getNumeroLinea());
            } else {
                ps.setInt(5, 1);
            }

            ps.executeUpdate();
            return detallePedido;
        } catch (SQLException e) {
            log.error("Error al guardar detalle de pedido: {}", e.getMessage());
            throw new PersistenceException("Error al guardar detalle de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<DetallePedido> findById(Integer codigoPedido, String codigoProducto) {
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea " +
                     "FROM detalle_pedido WHERE codigo_pedido = ? AND codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoPedido);
            ps.setString(2, codigoProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDetallePedido(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar detalle de pedido (pedido: {}, producto: {}): {}", codigoPedido, codigoProducto, e.getMessage());
            throw new PersistenceException("Error al buscar detalle de pedido por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetallePedido> findAll() {
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea FROM detalle_pedido";
        List<DetallePedido> detalles = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                detalles.add(mapResultSetToDetallePedido(rs));
            }
            return detalles;
        } catch (SQLException e) {
            log.error("Error al listar todos los detalles de pedido: {}", e.getMessage());
            throw new PersistenceException("Error al listar detalles de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetallePedido> findByCodigoPedido(Integer codigoPedido) {
        String sql = "SELECT codigo_pedido, codigo_producto, cantidad, precio_unidad, numero_linea " +
                     "FROM detalle_pedido WHERE codigo_pedido = ? ORDER BY numero_linea ASC";
        List<DetallePedido> detalles = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetallePedido(rs));
                }
            }
            return detalles;
        } catch (SQLException e) {
            log.error("Error al buscar detalles para el pedido con código {}: {}", codigoPedido, e.getMessage());
            throw new PersistenceException("Error al buscar detalles por código de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public DetallePedido update(DetallePedido detallePedido) {
        String sql = "UPDATE detalle_pedido SET cantidad = ?, precio_unidad = ?, numero_linea = ? " +
                     "WHERE codigo_pedido = ? AND codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (detallePedido.getCantidad() != null) {
                ps.setInt(1, detallePedido.getCantidad());
            } else {
                ps.setInt(1, 0);
            }

            if (detallePedido.getPrecioUnidad() != null) {
                ps.setBigDecimal(2, detallePedido.getPrecioUnidad());
            } else {
                ps.setNull(2, Types.DECIMAL);
            }

            if (detallePedido.getNumeroLinea() != null) {
                ps.setInt(3, detallePedido.getNumeroLinea());
            } else {
                ps.setInt(3, 1);
            }

            ps.setInt(4, detallePedido.getPedido() != null ? detallePedido.getPedido().getCodigoPedido() : 0);
            ps.setString(5, detallePedido.getProducto() != null ? detallePedido.getProducto().getCodigoProducto() : "");

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el detalle de pedido para actualizar");
            }
            return detallePedido;
        } catch (SQLException e) {
            log.error("Error al actualizar detalle de pedido: {}", e.getMessage());
            throw new PersistenceException("Error al actualizar detalle de pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer codigoPedido, String codigoProducto) {
        String sql = "DELETE FROM detalle_pedido WHERE codigo_pedido = ? AND codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoPedido);
            ps.setString(2, codigoProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar detalle de pedido (pedido: {}, producto: {}): {}", codigoPedido, codigoProducto, e.getMessage());
            throw new PersistenceException("Error al eliminar detalle de pedido: " + e.getMessage(), e);
        }
    }

    private DetallePedido mapResultSetToDetallePedido(ResultSet rs) throws SQLException {
        int codPedido = rs.getInt("codigo_pedido");
        Pedido pedido = rs.wasNull() ? null : Pedido.builder().codigoPedido(codPedido).build();

        String codProducto = rs.getString("codigo_producto");
        Producto producto = codProducto != null ? Producto.builder().codigoProducto(codProducto).build() : null;

        return DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(rs.getInt("cantidad"))
                .precioUnidad(rs.getBigDecimal("precio_unidad"))
                .numeroLinea(rs.getInt("numero_linea"))
                .build();
    }
}

