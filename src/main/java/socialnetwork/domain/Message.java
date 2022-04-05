package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    Utilizator from;
    List<Utilizator> to;
    String message;
    LocalDateTime date;

    public Message(){}
    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        String d=LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        this.date=LocalDateTime.parse(d, Constants.DATE_TIME_FORMATTER);
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime newDate){
        this.date=newDate;
    }


//    @Override
//    public String toString() {
//        return  "From: "+from +" | To: "+to+" | "+date.format(Constants.DATE_TIME_FORMATTER_NICE)+
//                "---> " + message + '\n';
//    }
@Override
public String toString() {
    return from.getFirstName()+": "+ message + '\n';
}

    public String display(){
        return "ID: "+getId()+"|From: "+from+"|To: "+to+"|Message: "+message+" \n";
    }

}
