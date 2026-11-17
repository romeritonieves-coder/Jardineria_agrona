package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.Cliente;
import jardineria.domain.model.Pedido;
import jardineria.domain.ports.out.PedidoRepositoryPort;
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
 * Adaptador de infraestructura para la persistencia de Pedido mediante JDBC puro.
 */
@Slf4j
public class PedidoRepositoryJdbc implements PedidoRepositoryPort {

    @Override
    public Pedido save(Pedido pedido) {
        String sql = "INSERT INTO pedido (codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, pedido.getCodigoPedido());

            if (pedido.getFechaPedido() != null) {
                ps.setDate(2, Date.valueOf(pedido.getFechaPedido()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (pedido.getFechaEsperada() != null) {
                ps.setDate(3, Date.valueOf(pedido.getFechaEsperada()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (pedido.getFechaEntrega() != null) {
                ps.setDate(4, Date.valueOf(pedido.getFechaEntrega()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, pedido.getEstado());
            ps.setString(6, pedido.getComentarios());

            if (pedido.getCliente() != null && pedido.getCliente().getCodigoCliente() != null) {
                ps.setInt(7, pedido.getCliente().getCodigoCliente());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.executeUpdate();
            return pedido;
        } catch (SQLException e) {
            log.error("Error al guardar pedido con código {}: {}", pedido.getCodigoPedido(), e.getMessage());
            throw new PersistenceException("Error al guardar pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pedido> findById(Integer codigoPedido) {
        String sql = "SELECT codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente " +
                     "FROM pedido WHERE codigo_pedido = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPedido(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar pedido con código {}: {}", codigoPedido, e.getMessage());
            throw new PersistenceException("Error al buscar pedido por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> findAll() {
        String sql = "SELECT codigo_pedido, fecha_pedido, fecha_esperada, fecha_entrega, estado, comentarios, codigo_cliente FROM pedido";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
            return pedidos;
        } catch (SQLException e) {
            log.error("Error al listar todos los pedidos: {}", e.getMessage());
            throw new PersistenceException("Error al listar pedidos: " + e.getMessage(), e);
        }
    }

    @Override
    public Pedido update(Pedido pedido) {
        String sql = "UPDATE pedido SET fecha_pedido = ?, fecha_esperada = ?, fecha_entrega = ?, estado = ?, " +
                     "comentarios = ?, codigo_cliente = ? WHERE codigo_pedido = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (pedido.getFechaPedido() != null) {
                ps.setDate(1, Date.valueOf(pedido.getFechaPedido()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            if (pedido.getFechaEsperada() != null) {
                ps.setDate(2, Date.valueOf(pedido.getFechaEsperada()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (pedido.getFechaEntrega() != null) {
                ps.setDate(3, Date.valueOf(pedido.getFechaEntrega()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, pedido.getEstado());
            ps.setString(5, pedido.getComentarios());

            if (pedido.getCliente() != null && pedido.getCliente().getCodigoCliente() != null) {
                ps.setInt(6, pedido.getCliente().getCodigoCliente());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, pedido.getCodigoPedido());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el pedido con código {} para actualizar", pedido.getCodigoPedido());
            }
            return pedido;
        } catch (SQLException e) {
            log.error("Error al actualizar pedido con código {}: {}", pedido.getCodigoPedido(), e.getMessage());
            throw new PersistenceException("Error al actualizar pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer codigoPedido) {
        String sql = "DELETE FROM pedido WHERE codigo_pedido = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, codigoPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar pedido con código {}: {}", codigoPedido, e.getMessage());
            throw new PersistenceException("Error al eliminar pedido: " + e.getMessage(), e);
        }
    }

    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        Date fechaPedido = rs.getDate("fecha_pedido");
        Date fechaEsperada = rs.getDate("fecha_esperada");
        Date fechaEntrega = rs.getDate("fecha_entrega");

        int codCliente = rs.getInt("codigo_cliente");
        Cliente cliente = rs.wasNull() ? null : Cliente.builder().codigoCliente(codCliente).build();

        return Pedido.builder()
                .codigoPedido(rs.getInt("codigo_pedido"))
                .fechaPedido(fechaPedido != null ? fechaPedido.toLocalDate() : null)
                .fechaEsperada(fechaEsperada != null ? fechaEsperada.toLocalDate() : null)
                .fechaEntrega(fechaEntrega != null ? fechaEntrega.toLocalDate() : null)
                .estado(rs.getString("estado"))
                .comentarios(rs.getString("comentarios"))
                .cliente(cliente)
                .build();
    }
}

