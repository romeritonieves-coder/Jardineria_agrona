package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.GamaProducto;
import jardineria.domain.model.Producto;
import jardineria.domain.ports.out.ProductoRepositoryPort;
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
 * Adaptador de infraestructura para la persistencia de Producto mediante JDBC puro.
 */
@Slf4j
public class ProductoRepositoryJdbc implements ProductoRepositoryPort {

    @Override
    public Producto save(Producto producto) {
        String sql = "INSERT INTO producto (codigo_producto, nombre, gama, dimensiones, proveedor, " +
                     "descripcion, cantidad_en_stock, precio_venta, precio_proveedor) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigoProducto());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getGama() != null ? producto.getGama().getGama() : null);
            ps.setString(4, producto.getDimensiones());
            ps.setString(5, producto.getProveedor());
            ps.setString(6, producto.getDescripcion());

            if (producto.getCantidadEnStock() != null) {
                ps.setInt(7, producto.getCantidadEnStock());
            } else {
                ps.setInt(7, 0);
            }

            if (producto.getPrecioVenta() != null) {
                ps.setBigDecimal(8, producto.getPrecioVenta());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            if (producto.getPrecioProveedor() != null) {
                ps.setBigDecimal(9, producto.getPrecioProveedor());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }

            ps.executeUpdate();
            return producto;
        } catch (SQLException e) {
            log.error("Error al guardar producto '{}': {}", producto.getCodigoProducto(), e.getMessage());
            throw new PersistenceException("Error al guardar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Producto> findById(String codigoProducto) {
        String sql = "SELECT codigo_producto, nombre, gama, dimensiones, proveedor, descripcion, " +
                     "cantidad_en_stock, precio_venta, precio_proveedor " +
                     "FROM producto WHERE codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProducto(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar producto por código '{}': {}", codigoProducto, e.getMessage());
            throw new PersistenceException("Error al buscar producto por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> findAll() {
        String sql = "SELECT codigo_producto, nombre, gama, dimensiones, proveedor, descripcion, " +
                     "cantidad_en_stock, precio_venta, precio_proveedor FROM producto";
        List<Producto> productos = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
            return productos;
        } catch (SQLException e) {
            log.error("Error al listar todos los productos: {}", e.getMessage());
            throw new PersistenceException("Error al listar productos: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto update(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, gama = ?, dimensiones = ?, proveedor = ?, " +
                     "descripcion = ?, cantidad_en_stock = ?, precio_venta = ?, precio_proveedor = ? " +
                     "WHERE codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getGama() != null ? producto.getGama().getGama() : null);
            ps.setString(3, producto.getDimensiones());
            ps.setString(4, producto.getProveedor());
            ps.setString(5, producto.getDescripcion());

            if (producto.getCantidadEnStock() != null) {
                ps.setInt(6, producto.getCantidadEnStock());
            } else {
                ps.setInt(6, 0);
            }

            if (producto.getPrecioVenta() != null) {
                ps.setBigDecimal(7, producto.getPrecioVenta());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            if (producto.getPrecioProveedor() != null) {
                ps.setBigDecimal(8, producto.getPrecioProveedor());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            ps.setString(9, producto.getCodigoProducto());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró el producto con código '{}' para actualizar", producto.getCodigoProducto());
            }
            return producto;
        } catch (SQLException e) {
            log.error("Error al actualizar producto '{}': {}", producto.getCodigoProducto(), e.getMessage());
            throw new PersistenceException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String codigoProducto) {
        String sql = "DELETE FROM producto WHERE codigo_producto = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, codigoProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar el producto '{}': {}", codigoProducto, e.getMessage());
            throw new PersistenceException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }

    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        String codGama = rs.getString("gama");
        GamaProducto gama = codGama != null ? GamaProducto.builder().gama(codGama).build() : null;

        return Producto.builder()
                .codigoProducto(rs.getString("codigo_producto"))
                .nombre(rs.getString("nombre"))
                .gama(gama)
                .dimensiones(rs.getString("dimensiones"))
                .proveedor(rs.getString("proveedor"))
                .descripcion(rs.getString("descripcion"))
                .cantidadEnStock(rs.getInt("cantidad_en_stock"))
                .precioVenta(rs.getBigDecimal("precio_venta"))
                .precioProveedor(rs.getBigDecimal("precio_proveedor"))
                .build();
    }
}

