package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.UserValidationException;

public class UtilizatorValidator implements Validator<Utilizator> {
    /**
     * valideaza un obiect de tip Utiliaztpr
     * @param entity obiectul de validat
     * @throws ValidationException daca Utilizatorul nu are datele corecte
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException{
        String error="";
        //TODO: implement method validate
        String s=entity.getFirstName();
        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    error+="First Name of the user cannot contain numbers!\n";
                    break;
                }
            }
        }
        if(s.isEmpty()) error+="First Name cannot be null\n";

        s=entity.getLastName();
        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    error+="Last Name of the user cannot contain numbers!\n";
                    break;
                }
            }
        }
        if(s.isEmpty()) error+="Last Name cannot be null\n";

        if(entity.getEmail().isEmpty()) error+="Field <Email> can not be empty\n";
        if(entity.getPassword().isEmpty()) error+="Field <Password> can not be empty\n";

        if(!entity.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")){
            error+= "The email is not valid!\n";
        }

        if(!error.isEmpty()) throw new UserValidationException(error);

    }
}
