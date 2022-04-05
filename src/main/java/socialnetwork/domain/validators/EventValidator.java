package socialnetwork.domain.validators;

import socialnetwork.domain.Event;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.exceptions.UserValidationException;

import java.time.LocalDateTime;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException, UserValidationException, PrietenieValidationException {
        String errors="";
        if(entity.getStart_date().isAfter(entity.getEnd_date()))
            errors+="Start date cannot be after the end date\n";
        if(entity.getStart_date().isBefore(LocalDateTime.now())||entity.getEnd_date().isBefore(LocalDateTime.now()))
            errors+="The event cannot take place on an earlier date! \n";
        if(entity.getTitle().isEmpty()) errors+="The title of the event cannot be null";
        if(!errors.isEmpty()) throw new ValidationException(errors);
    }
}
