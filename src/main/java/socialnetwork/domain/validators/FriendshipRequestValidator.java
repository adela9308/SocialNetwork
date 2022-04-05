package socialnetwork.domain.validators;
import socialnetwork.domain.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {
    /**
     * valideaza un obiect de tip prietenie
     * @param entity obiectul de validat
     * @throws ValidationException daca Prieteniea nu are datele corecte
     */
    @Override

    public void validate(FriendshipRequest entity) throws ValidationException {
        String error="";
        if(entity.getId().getRight()==null)
            error+="The ID of the user you want to send a friend request cannot be null!\n";
        else {
            if(entity.getId().getLeft().equals(entity.getId().getRight())){
                error+="You cannot send yourself a friend request!\n";}

            if(!entity.getRequest().equals("pending")&&!entity.getRequest().equals("rejected")&&!entity.getRequest().equals("approved"))
                error+="The friendsip request must be in one of this 3 states: pending,rejected or approved!\n";
        }
        if(!error.isEmpty()) throw new ValidationException(error);


    }
}