package socialnetwork.utils.events;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.ReplyMessage;

public class RequestsChangeEvent implements Event{
    private ChangeEventType type;
    private FriendshipRequest data, oldData;

    public RequestsChangeEvent(ChangeEventType type, FriendshipRequest data) {
        this.type = type;
        this.data = data;
    }

    public RequestsChangeEvent(ChangeEventType type,  FriendshipRequest data,  FriendshipRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public  FriendshipRequest getData() {
        return data;
    }

    public  FriendshipRequest getOldData() {
        return oldData;
    }
}
