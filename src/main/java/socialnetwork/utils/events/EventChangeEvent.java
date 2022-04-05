package socialnetwork.utils.events;
import socialnetwork.domain.Event;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.utils.events.ChangeEventType;

public class EventChangeEvent implements socialnetwork.utils.events.Event {
    private ChangeEventType type;
    private Event data, oldData;

    public EventChangeEvent(ChangeEventType type, Event data) {
        this.type = type;
        this.data = data;
    }
    public EventChangeEvent(ChangeEventType type, Event data, Event oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public  Event getData() {
        return data;
    }

    public  Event getOldData() {
        return oldData;
    }
}