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
        String configuredUrl = properties.getProperty("db.url", "jdbc:mysql://localhost:3307/jardineria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String configuredUser = properties.getProperty("db.user", "root");
        String configuredPassword = properties.getProperty("db.password", "root2026");

        try {
            return DriverManager.getConnection(configuredUrl, configuredUser, configuredPassword);
        } catch (SQLException primaryEx) {
            // Fallback inteligente para entornos Docker locales donde el puerto mapeado sea 3307 o 3306
            String fallbackUrl = configuredUrl.contains(":3307") ?
                    configuredUrl.replace(":3307", ":3306") :
                    configuredUrl.replace(":3306", ":3307");
            try {
                return DriverManager.getConnection(fallbackUrl, configuredUser, configuredPassword);
            } catch (SQLException fallbackEx) {
                // Probar con contraseña alternativa "root" si falló con "root2026"
                try {
                    return DriverManager.getConnection(configuredUrl, configuredUser, "root");
                } catch (SQLException ignored) {}
                try {
                    return DriverManager.getConnection(fallbackUrl, configuredUser, "root");
                } catch (SQLException ignored) {}
                throw primaryEx;
            }
        }
    }
}

