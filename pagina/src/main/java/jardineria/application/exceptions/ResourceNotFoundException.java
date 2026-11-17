package jardineria.application.exceptions;

/**
 * Excepción lanzada cuando una entidad requerida no se encuentra en el sistema.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

