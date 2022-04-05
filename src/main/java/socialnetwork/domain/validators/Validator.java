package socialnetwork.domain.validators;

import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.exceptions.UserValidationException;

public interface Validator<T> {
    /**
     * functie de validare pt un obiect de tip T
     * @param entity obiectul de validat
     * @throws ValidationException daca obiectul nu are datele corecte
     */
    void validate(T entity) throws ValidationException, UserValidationException, PrietenieValidationException;
}