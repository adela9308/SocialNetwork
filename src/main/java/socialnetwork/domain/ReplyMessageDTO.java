package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class ReplyMessageDTO extends Entity<Long> {
    ReplyMessage m;

    public ReplyMessageDTO(ReplyMessage m) {
        this.m = m;
    }

    public ReplyMessage getM() {
        return m;
    }
    public LocalDateTime getDate(){
        return m.getDate();
    }

    @Override
    public String toString() {
        String s= m.getTo().toString().substring(1, m.getTo().toString().length() - 1);
        String ret=  "From: "+m.getFrom() +"\nTo: "+s+"\n"+m.getDate().format(Constants.DATE_TIME_FORMATTER_NICE)+
                "\n" + m.getMessage() + '\n';
        if(m.getMessageObject()!=null) ret+="Reply to: "+m.getMessageObject().getMessage()+"\n";
        return ret;
    }

}
