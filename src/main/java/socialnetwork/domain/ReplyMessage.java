package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReplyMessage extends Message{
    private Message message;
    private List<Utilizator> to;
    private Utilizator from;
    private String mess;

    public ReplyMessage(Utilizator from, List<Utilizator> to, String mess,Message message) {
        super(from, to, mess);
        this.message=message;
        this.from=from;
        this.to=to;
        this.mess=mess;
    }


    public Message getMessageObject() {
        return message;
    }

    @Override
    public String toString() {
        if(message!=null)  return message.getFrom().getFirstName()+" at "+getDate().format(Constants.DATE_TIME_FORMATTER_CLOCK)+"\nreplied to: "+message.getMessage()+"\n"+mess;
        return from.getFirstName()+" at "+getDate().format(Constants.DATE_TIME_FORMATTER_CLOCK)+"\n"+ mess + '\n';
    }
}
