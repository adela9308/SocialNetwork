package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class UserFriendshipDTO extends Entity<Long>{
    private Long idUser;
    private String firstName;
    private String lastName;
    private LocalDateTime date;
    private String email;

    public UserFriendshipDTO(Long idUser,String firstName, String lastName, LocalDateTime date,String email) {
        this.idUser=idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        String d=date.format(Constants.DATE_TIME_FORMATTER);
        this.date=LocalDateTime.parse(d, Constants.DATE_TIME_FORMATTER);
    }

    public String getEmail() {
        return email;
    }

    public Long getIdUser() {
        return idUser;
    }


    public String getFirstName(){
            return firstName;
        }

    public String getLastName(){
            return lastName;
        }

    public LocalDateTime getDate(){
            return date;
        }

    @Override
    public String toString(){
            return firstName+" | "+lastName+" | "+ date.format(Constants.DATE_TIME_FORMATTER);
        }

}
