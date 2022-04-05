package socialnetwork.domain.exceptions;

/**
 * Clasa de exceptii pt Service
 */
public class ServiceException extends RuntimeException {
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }
}
