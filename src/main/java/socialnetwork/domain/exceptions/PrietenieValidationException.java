package socialnetwork.domain.exceptions;

/**
 * Clasa de exceptii pt validarea obiectelor de tip Prietenie
 */                                             //ValidationException
public class PrietenieValidationException extends RuntimeException{
    public PrietenieValidationException() {
    }

    public PrietenieValidationException(String message) {
        super(message);
    }
}
