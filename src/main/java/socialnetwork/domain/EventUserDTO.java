package socialnetwork.domain;

import javafx.scene.control.CheckBox;
import socialnetwork.utils.Constants;

import java.text.DateFormat;
import java.time.LocalDateTime;

public class EventUserDTO {
    private String title;
    private LocalDateTime start_date;
    private Long eventID;
    private Long p,n;
    private CheckBox participating;
    private CheckBox notify;

    public EventUserDTO(String title, LocalDateTime start_date,Long eventID, Long p,Long n) {
        this.title = title;
        this.start_date = start_date;
        this.eventID=eventID;
        this.participating = new CheckBox();
        if(p==1) this.participating.setSelected(true);
        this.notify=new CheckBox();
        if(n==1) this.notify.setSelected(true);
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStart_date() {
        String date= start_date.format(Constants.DATE_TIME_FORMATTER_NICE);
        return start_date=LocalDateTime.parse(date,Constants.DATE_TIME_FORMATTER_NICE);

    }

    public Long getEventID(){
        return eventID;
    }
    public CheckBox getParticipating() {
        return participating;
    }
    public CheckBox getNotify(){
        return notify;
    }

    @Override
    public String toString(){
        return "The event <"+title+"> is taking place on "+start_date.format(Constants.DATE_TIME_FORMATTER_NICE)+"\n";
    }
}
