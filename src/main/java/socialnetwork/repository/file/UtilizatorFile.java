package socialnetwork.repository.file;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator>{

    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }


    /**
     * creeaza un el de tip utilizator dintr-un string
     * @param attributes lista de stringuri
     * @return el de tip utlilizaztor
     */
    @Override
    public Utilizator extractEntity(List<String> attributes) {
        //TODO: implement method
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /**
     * concateneaza inf despre un obiect de tip Utilizator intr-un string
     * @param entity obiectul de tip Utilizator
     * @return stringul resspectiv
     */
    @Override

    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }


}
