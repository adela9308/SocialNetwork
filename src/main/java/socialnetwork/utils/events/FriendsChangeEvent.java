package socialnetwork.utils.events;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Prietenie;

public class FriendsChangeEvent implements Event{
    private ChangeEventType type;
    private Prietenie data, oldData;

    public FriendsChangeEvent(ChangeEventType type, Prietenie data) {
        this.type = type;
        this.data = data;
    }

    public FriendsChangeEvent(ChangeEventType type,  Prietenie data,  Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public  Prietenie getData() {
        return data;
    }

    public  Prietenie getOldData() {
        return oldData;
    }
}
