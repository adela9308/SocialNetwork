package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.exceptions.UserValidationException;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException, UserValidationException, PrietenieValidationException {
        String error="";
        if(entity.getFrom()==null||entity.getTo().size()==0) error+="<From> and <To> cannot be null!\n";
        if(entity.getMessage()==null) error+="Message sent cannot be null!\n";
        if(!error.isEmpty()) throw new ValidationException(error);
    }
}
