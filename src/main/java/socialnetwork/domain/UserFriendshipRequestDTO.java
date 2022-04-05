package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class UserFriendshipRequestDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String state;
    private LocalDateTime date;


    public UserFriendshipRequestDTO(Long id, String firstName, String lastName, String state,LocalDateTime date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
        this.date=date;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getState(){
        return state;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString(){
        return firstName+" "+lastName+" | "+ state+" | "+date.format(Constants.DATE_TIME_FORMATTER);
    }


}
