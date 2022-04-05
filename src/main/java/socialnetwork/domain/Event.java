package socialnetwork.domain;

import jdk.vm.ci.meta.Local;
import socialnetwork.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Event extends Entity<Long>{
    private String title;
    private String description;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private Utilizator owner;

    public Event(String title, String description, LocalDateTime start_date, LocalDateTime end_date, Utilizator owner) {
        this.title = title;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public Utilizator getOwner() {
        return owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(LocalDateTime end_date) {
        this.end_date = end_date;
    }

    public void setOwner(Utilizator owner) {
        this.owner = owner;
    }

    @Override
    public String toString(){
        return "The event "+title+" will begin on "+start_date.format(Constants.DATE_TIME_FORMATTER_NICE);
    }

    public String getInfoEvent(){
        return "The event <"+title+"> will take place between "+start_date.format(Constants.DATE_TIME_FORMATTER_NICE) +" and "+end_date.format(Constants.DATE_TIME_FORMATTER_NICE)+"\n"+
                "Description: "+description+"\n"+"Owner: "+owner.getFirstName()+" "+owner.getLastName()+"\n";
    }
}
