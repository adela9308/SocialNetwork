package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class FriendshipRequest extends Entity<Tuple<Long,Long>> {
    String request;
    LocalDateTime date;

    public FriendshipRequest(Long requester_id,Long addressee_id, String request) {
        Tuple<Long,Long> id=new Tuple<Long,Long>(requester_id,addressee_id);
        setId(id);
        this.request = request;
        String d=LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        date=LocalDateTime.parse(d, Constants.DATE_TIME_FORMATTER);
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}

enum status{
    PENDING,ACCEPTED,REJECTED
}