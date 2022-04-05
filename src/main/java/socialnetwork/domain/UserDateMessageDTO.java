package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class UserDateMessageDTO {
    private List<Utilizator> user;
    private Long messageID;
    private LocalDateTime date;

    public UserDateMessageDTO(List<Utilizator> user, LocalDateTime date,Long messageID) {
        this.user = user;
        this.date = date;
        this.messageID=messageID;
    }

    public List<Utilizator> getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public Long getMessageID(){return messageID;}

    @Override
    public String toString() {
        String s= user.toString().substring(1, user.toString().length() - 1);
        return s + "\n" +date.format(Constants.DATE_TIME_FORMATTER_NICE);
    }

    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if (this == o) return true;
        if (!(o instanceof UserDateMessageDTO)) return false;
        UserDateMessageDTO that = (UserDateMessageDTO) o;
        return getUser().equals(that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser());
    }
}
