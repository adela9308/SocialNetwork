package socialnetwork.domain.exceptions;

/**
 * Clasa de exceptii pt validarea obiectelor de tip Utilizator
 */
public class UserValidationException extends RuntimeException{
    public UserValidationException() {
    }

    public UserValidationException(String message) {
        super(message);
    }
}
