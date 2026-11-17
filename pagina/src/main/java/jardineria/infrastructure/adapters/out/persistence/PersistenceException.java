package jardineria.infrastructure.adapters.out.persistence;

/**
 * Excepción en tiempo de ejecución para errores ocurridos en la capa de persistencia JDBC.
 */
public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

