package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.PrietenieValidationException;

public class PrietenieValidator implements Validator<Prietenie> {
    /**
     * valideaza un obiect de tip prietenie
     * @param entity obiectul de validat
     * @throws ValidationException daca Prieteniea nu are datele corecte
     */
    @Override

    public void validate(Prietenie entity) throws ValidationException {
        String error="";
        if(entity.getId().getLeft()==null||entity.getId().getRight()==null)
            error+="None of the IDs can be null\n";
        else if(entity.getId().getLeft().equals(entity.getId().getRight())){
                error+="IDs must be different\n";}
        if(!error.isEmpty()) throw new PrietenieValidationException(error);


}
}