package socialnetwork.domain;

import jdk.vm.ci.meta.Local;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    /**
     * date: data la care a fost creata prietenia
     */
    LocalDateTime date;

    public Prietenie(Long id1,Long id2) {
        Tuple<Long,Long> id=new Tuple<Long,Long>(id1,id2);
        setId(id);
        String d=LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        date=LocalDateTime.parse(d, Constants.DATE_TIME_FORMATTER);
    }
    public void setDate(LocalDateTime d) {
        date=d;
    }
    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friends <" +getId().getLeft()+";"+ getId().getRight()+"> " + getDate().format(Constants.DATE_TIME_FORMATTER);
    }
}
