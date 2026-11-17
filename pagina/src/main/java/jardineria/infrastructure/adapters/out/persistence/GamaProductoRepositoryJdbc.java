package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.GamaProducto;
import jardineria.domain.ports.out.GamaProductoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura para la persistencia de GamaProducto mediante JDBC puro.
 */
@Slf4j
public class GamaProductoRepositoryJdbc implements GamaProductoRepositoryPort {

    @Override
    public GamaProducto save(GamaProducto gamaProducto) {
        String sql = "INSERT INTO gama_producto (gama, descripcion_texto, descripcion_html, imagen) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, gamaProducto.getGama());
            ps.setString(2, gamaProducto.getDescripcionTexto());
            ps.setString(3, gamaProducto.getDescripcionHtml());
            ps.setString(4, gamaProducto.getImagen());

            ps.executeUpdate();
            return gamaProducto;
        } catch (SQLException e) {
            log.error("Error al guardar gama de producto '{}': {}", gamaProducto.getGama(), e.getMessage());
            throw new PersistenceException("Error al guardar gama de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<GamaProducto> findById(String gama) {
        String sql = "SELECT gama, descripcion_texto, descripcion_html, imagen FROM gama_producto WHERE gama = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, gama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGamaProducto(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar gama de producto '{}': {}", gama, e.getMessage());
            throw new PersistenceException("Error al buscar gama de producto por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GamaProducto> findAll() {
        String sql = "SELECT gama, descripcion_texto, descripcion_html, imagen FROM gama_producto";
        List<GamaProducto> gamas = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                gamas.add(mapResultSetToGamaProducto(rs));
            }
            return gamas;
        } catch (SQLException e) {
            log.error("Error al listar todas las gamas de producto: {}", e.getMessage());
            throw new PersistenceException("Error al listar gamas de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public GamaProducto update(GamaProducto gamaProducto) {
        String sql = "UPDATE gama_producto SET descripcion_texto = ?, descripcion_html = ?, imagen = ? WHERE gama = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, gamaProducto.getDescripcionTexto());
            ps.setString(2, gamaProducto.getDescripcionHtml());
            ps.setString(3, gamaProducto.getImagen());
            ps.setString(4, gamaProducto.getGama());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró la gama '{}' para actualizar", gamaProducto.getGama());
            }
            return gamaProducto;
        } catch (SQLException e) {
            log.error("Error al actualizar la gama de producto '{}': {}", gamaProducto.getGama(), e.getMessage());
            throw new PersistenceException("Error al actualizar gama de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String gama) {
        String sql = "DELETE FROM gama_producto WHERE gama = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, gama);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar la gama de producto '{}': {}", gama, e.getMessage());
            throw new PersistenceException("Error al eliminar gama de producto: " + e.getMessage(), e);
        }
    }

    private GamaProducto mapResultSetToGamaProducto(ResultSet rs) throws SQLException {
        return GamaProducto.builder()
                .gama(rs.getString("gama"))
                .descripcionTexto(rs.getString("descripcion_texto"))
                .descripcionHtml(rs.getString("descripcion_html"))
                .imagen(rs.getString("imagen"))
                .build();
    }
}

