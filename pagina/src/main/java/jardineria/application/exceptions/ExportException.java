package jardineria.application.exceptions;

/**
 * Excepción lanzada cuando ocurre un error durante la generación o exportación de reportes.
 */
public class ExportException extends RuntimeException {
    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}

