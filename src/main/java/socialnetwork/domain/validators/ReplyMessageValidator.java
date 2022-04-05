package socialnetwork.domain.validators;

import socialnetwork.domain.ReplyMessage;

public class ReplyMessageValidator implements Validator<ReplyMessage> {
    @Override
    public void validate(ReplyMessage entity) throws ValidationException {
        String error = "";
        if (entity.getFrom() == null || entity.getTo().size() == 0) error += "<From> and <To> cannot be null!\n";
        if(entity.getMessageObject()==null) error+="The message you want to respond does not exist\n";
        if(entity.getMessage()==null) error+="Message sent cannot be null!\n";
        if (!error.isEmpty()) throw new ValidationException(error);
    }
}

