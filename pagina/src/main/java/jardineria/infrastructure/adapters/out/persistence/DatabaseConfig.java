package jardineria.infrastructure.adapters.out.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton de configuración y gestión de la conexión JDBC leyendo db.properties.
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private final Properties properties = new Properties();

    private DatabaseConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Advertencia: No se encontró db.properties en classpath.");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar db.properties: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/jardineria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = properties.getProperty("db.user", "root");
        String password = properties.getProperty("db.password", "root");
        return DriverManager.getConnection(url, user, password);
    }
}

