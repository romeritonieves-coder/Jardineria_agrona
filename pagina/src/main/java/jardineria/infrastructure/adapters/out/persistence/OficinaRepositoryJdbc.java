package jardineria.infrastructure.adapters.out.persistence;

import jardineria.domain.model.Oficina;
import jardineria.domain.ports.out.OficinaRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura para la persistencia de Oficina mediante JDBC puro.
 */
@Slf4j
public class OficinaRepositoryJdbc implements OficinaRepositoryPort {

    @Override
    public Oficina save(Oficina oficina) {
        String sql = "INSERT INTO oficina (codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, oficina.getCodigoOficina());
            ps.setString(2, oficina.getCiudad());
            ps.setString(3, oficina.getPais());
            ps.setString(4, oficina.getRegion());
            ps.setString(5, oficina.getCodigoPostal());
            ps.setString(6, oficina.getTelefono());
            ps.setString(7, oficina.getLineaDireccion1());
            ps.setString(8, oficina.getLineaDireccion2());

            ps.executeUpdate();
            return oficina;
        } catch (SQLException e) {
            log.error("Error al guardar oficina con código {}: {}", oficina.getCodigoOficina(), e.getMessage());
            throw new PersistenceException("Error al guardar la oficina: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Oficina> findById(String codigoOficina) {
        String sql = "SELECT codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2 " +
                     "FROM oficina WHERE codigo_oficina = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, codigoOficina);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOficina(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error al buscar oficina con código {}: {}", codigoOficina, e.getMessage());
            throw new PersistenceException("Error al buscar la oficina por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Oficina> findAll() {
        String sql = "SELECT codigo_oficina, ciudad, pais, region, codigo_postal, telefono, linea_direccion1, linea_direccion2 FROM oficina";
        List<Oficina> oficinas = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                oficinas.add(mapResultSetToOficina(rs));
            }
            return oficinas;
        } catch (SQLException e) {
            log.error("Error al listar todas las oficinas: {}", e.getMessage());
            throw new PersistenceException("Error al listar oficinas: " + e.getMessage(), e);
        }
    }

    @Override
    public Oficina update(Oficina oficina) {
        String sql = "UPDATE oficina SET ciudad = ?, pais = ?, region = ?, codigo_postal = ?, telefono = ?, " +
                     "linea_direccion1 = ?, linea_direccion2 = ? WHERE codigo_oficina = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, oficina.getCiudad());
            ps.setString(2, oficina.getPais());
            ps.setString(3, oficina.getRegion());
            ps.setString(4, oficina.getCodigoPostal());
            ps.setString(5, oficina.getTelefono());
            ps.setString(6, oficina.getLineaDireccion1());
            ps.setString(7, oficina.getLineaDireccion2());
            ps.setString(8, oficina.getCodigoOficina());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No se encontró la oficina con código {} para actualizar", oficina.getCodigoOficina());
            }
            return oficina;
        } catch (SQLException e) {
            log.error("Error al actualizar la oficina con código {}: {}", oficina.getCodigoOficina(), e.getMessage());
            throw new PersistenceException("Error al actualizar la oficina: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(String codigoOficina) {
        String sql = "DELETE FROM oficina WHERE codigo_oficina = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, codigoOficina);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error al eliminar la oficina con código {}: {}", codigoOficina, e.getMessage());
            throw new PersistenceException("Error al eliminar la oficina: " + e.getMessage(), e);
        }
    }

    private Oficina mapResultSetToOficina(ResultSet rs) throws SQLException {
        return Oficina.builder()
                .codigoOficina(rs.getString("codigo_oficina"))
                .ciudad(rs.getString("ciudad"))
                .pais(rs.getString("pais"))
                .region(rs.getString("region"))
                .codigoPostal(rs.getString("codigo_postal"))
                .telefono(rs.getString("telefono"))
                .lineaDireccion1(rs.getString("linea_direccion1"))
                .lineaDireccion2(rs.getString("linea_direccion2"))
                .build();
    }
}

