package socialnetwork.utils.events;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;

public class MessageChangeEvent implements Event{
    private ChangeEventType type;
    private ReplyMessage data, oldData;

    public MessageChangeEvent(ChangeEventType type, ReplyMessage data) {
        this.type = type;
        this.data = data;
    }

    public MessageChangeEvent(ChangeEventType type,  ReplyMessage data,  ReplyMessage oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public  ReplyMessage getData() {
        return data;
    }

    public  ReplyMessage getOldData() {
        return oldData;
    }
}
