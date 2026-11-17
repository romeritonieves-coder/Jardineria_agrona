package jardineria.application.exceptions;

/**
 * Excepción lanzada cuando una operación viola una regla de negocio del dominio.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

