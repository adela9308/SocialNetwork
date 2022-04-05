//package socialnetwork.repository.database;
//
//import socialnetwork.domain.Event;
//import socialnetwork.domain.FriendshipRequest;
//import socialnetwork.domain.Tuple;
//import socialnetwork.domain.Utilizator;
//import socialnetwork.domain.validators.ValidationException;
//import socialnetwork.domain.validators.Validator;
//import socialnetwork.repository.paging.Page;
//import socialnetwork.repository.paging.PageImplementation;
//import socialnetwork.repository.paging.Pageable;
//import socialnetwork.utils.Constants;
//
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.StreamSupport;
//
//public class EventDBRepository extends AbstractDBRepository<Long, Event>{
//
//    private String url;
//    private String username;
//    private String password;
//    private UserDBRepository userRepo;
//    public EventDBRepository(String tableName, String url, String username, String password,
//                             Validator<Event> validator,UserDBRepository userRepo) {
//        super(tableName, url, username, password, validator);
//        this.url=url;
//        this.username=username;
//        this.password=password;
//        this.userRepo=userRepo;
//    }
//
//    @Override
//    public Event extractEntity(ResultSet resultSet) throws SQLException {
//        Long eventID = resultSet.getLong("id");
//        String title = resultSet.getString("title");
//        String description = resultSet.getString("description");
//        Long ownerID = resultSet.getLong("owner");
//        String startDateString = resultSet.getString("start_date");
//        LocalDateTime startDate = LocalDateTime.parse(startDateString, Constants.DATE_TIME_FORMATTER);
//        String endDateString=resultSet.getString("end_date");//idk
//        LocalDateTime endDate=LocalDateTime.parse(endDateString, Constants.DATE_TIME_FORMATTER);
//
//        Utilizator owner=userRepo.findOne(ownerID);
//        Event event=new Event(title,description,startDate,endDate,owner);
//        event.setId(eventID);
//
//        return event;
//    }
//
//    @Override
//    public String queryInsert(Event event) {
//        Long id=generateID();
//        String title=event.getTitle();
//        String description= event.getDescription();
//        Long owner_id=event.getOwner().getId();
//        String start_date="'"+event.getStart_date().format(Constants.DATE_TIME_FORMATTER_WITHOUT_SECONDS)+"'";
//        String end_date="'"+event.getEnd_date().format(Constants.DATE_TIME_FORMATTER_WITHOUT_SECONDS)+"'";
//
//        return "INSERT INTO events (id,title, description,owner,start_date,end_date) VALUES ("+id+","+
//                "'"+title+"' , '"+description+
//                "',"+owner_id+", "+start_date+", "+end_date+")";
//
//
//    }
//
//    @Override
//    public String queryDelete(Long aLong) {
//        return "DELETE FROM events WHERE id="+aLong;
//    }
//
//    @Override
//    public String queryFindOne(Long aLong) {
//        return "SELECT * from events where id="+aLong;
//    }
//
//    @Override
//    public String queryFindAll() {
//        return "SELECT * from events";
//    }
//
//    @Override
//    public String queryGenerateID() {
//        return "SELECT array_agg(id) ids FROM events WHERE id = (SELECT MAX(id) FROM events)";
//    }
//
//    @Override
//    public String queryUpdate(Event entity) {
//        return null;
//    }
//
//    //events_users
//    public void participateToEvent(long userID,long eventID) {
//        //insert into events_users
//        String query="insert into events_users (id_user,id_event,notify) values ("+userID+" , "+eventID+",1)";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query))
//        {
//            int resultSet = statement.executeUpdate();
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//    public void stopParticipatingToEvent(long userID,long eventID) {
//        String query="delete from events_users where id_user="+userID+" and id_event="+eventID;
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query))
//        {
//            int resultSet = statement.executeUpdate();
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//    //NOTIFY--> 1 IF YES/ 0 IF NO
//    public void notifyEvent(long userID,long eventID){
//        String query="update events_users set notify=1 where id_user="+userID+" and id_event="+eventID;
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query))
//        {
//            int resultSet = statement.executeUpdate();
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//    public void stopNotifyingEvent(long userID,long eventID){
//        String query="update events_users set notify=0 where id_user="+userID+" and id_event="+eventID;
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query))
//        {
//            int resultSet = statement.executeUpdate();
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//    public Iterable<Long> getAllNotifyingEvents(long userID){
//        String query1="select * from events_users where notify=1 and id_user="+userID;
//        String query="select * from events\n" +
//                "where start_date>current_date and id in\n" +
//                "(select id_event\n" +
//                "from events_users eu\n" +
//                "inner join events e on eu.id_event = e.id \n" +
//                "where eu.id_user = "+userID+" and notify=1)\n";
//        List<Long> events=new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query1);
//             ResultSet resultSet = statement.executeQuery())
//        {
//            while (resultSet.next()) {
//                Long id=resultSet.getLong("id_event");
//                events.add(id);
//            }
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return events;
//    }
//
//    public Iterable<Long> getAllEventsParticipatingTo(long userID){
//        String query1="select * from events_users where id_user="+userID;
//        String query="select * from events\n" +
//                "where start_date>current_date and id in\n" +
//                "(select id_event\n" +
//                "from events_users eu\n" +
//                "inner join events e on eu.id_event  = e.id \n" +
//                "where eu.id_user = "+userID+")\n";
//        List<Long> events=new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query1);
//             ResultSet resultSet = statement.executeQuery())
//        {
//            while (resultSet.next()) {
//                Long id=resultSet.getLong("id_event");
//                events.add(id);
//            }
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return events;
//    }
//    public Iterable<Utilizator> getAllParticipantsToEvent(long eventID){
//        String query="select * from events_users where id_event="+eventID;
//        List<Utilizator> participants=new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query);
//                ResultSet resultSet = statement.executeQuery())
//        {
//            while (resultSet.next()) {
//                Long id=resultSet.getLong("id_user");
//                Utilizator u=userRepo.findOne(id);
//                participants.add(u);
//            }
//        }
//        catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return participants;
//    }
//
//
//    //pagination
//    public Iterable<Event> findAllEventsPaged(long lastID, int pageSize) {
//        Set<Event> set = new LinkedHashSet<>();
//        String query ="select * from events e where start_date>current_date and "+
//                "e.id>"+lastID+" limit "+pageSize;
//        String query1 ="select * from events e where "+
//                "e.id>"+lastID+" limit "+pageSize;
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(query1);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Event e=extractEntity(resultSet);
//                set.add(e);
//            }
//
//            return set;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return set;
//    }
//
//    public Page<Event> findAllPage(Pageable pageable,long lastID) {
////        Paginator<Utilizator> paginator = new Paginator<Utilizator>(pageable, this.findAllUsersNonFriends(userID, name));
////        return paginator.paginate();
//        return new PageImplementation<Event>(pageable, StreamSupport.stream(this.findAllEventsPaged(lastID,pageable.getPageSize()).spliterator(),false));
//    }
//
//
//}
package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class EventDBRepository extends AbstractDBRepository<Long, Event>{

    private String url;
    private String username;
    private String password;
    private UserDBRepository userRepo;
    public EventDBRepository(String tableName, String url, String username, String password,
                             Validator<Event> validator,UserDBRepository userRepo) {
        super(tableName, url, username, password, validator);
        this.url=url;
        this.username=username;
        this.password=password;
        this.userRepo=userRepo;
    }

    @Override
    public Event extractEntity(ResultSet resultSet) throws SQLException {
        Long eventID = resultSet.getLong("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        Long ownerID = resultSet.getLong("owner");
        String startDateString = resultSet.getString("start_date");
        LocalDateTime startDate = LocalDateTime.parse(startDateString, Constants.DATE_TIME_FORMATTER);
        String endDateString=resultSet.getString("end_date");//idk
        LocalDateTime endDate=LocalDateTime.parse(endDateString, Constants.DATE_TIME_FORMATTER);

        Utilizator owner=userRepo.findOne(ownerID);
        Event event=new Event(title,description,startDate,endDate,owner);
        event.setId(eventID);

        return event;
    }

    @Override
    public String queryInsert(Event event) {
        Long id=generateID();
        String title=event.getTitle();
        String description= event.getDescription();
        Long owner_id=event.getOwner().getId();
        String start_date="'"+event.getStart_date().format(Constants.DATE_TIME_FORMATTER_WITHOUT_SECONDS)+"'";
        String end_date="'"+event.getEnd_date().format(Constants.DATE_TIME_FORMATTER_WITHOUT_SECONDS)+"'";

        return "INSERT INTO events (id,title, description,owner,start_date,end_date) VALUES ("+id+","+
                "'"+title+"' , '"+description+
                "',"+owner_id+", "+start_date+", "+end_date+")";


    }

    @Override
    public String queryDelete(Long aLong) {
        return "DELETE FROM events WHERE id="+aLong;
    }

    @Override
    public String queryFindOne(Long aLong) {
        return "SELECT * from events where id="+aLong;
    }

    @Override
    public String queryFindAll() {
        return "SELECT * from events";
    }

    @Override
    public String queryGenerateID() {
        return "SELECT array_agg(id) ids FROM events WHERE id = (SELECT MAX(id) FROM events)";
    }

    @Override
    public String queryUpdate(Event entity) {
        return null;
    }

    //events_users
    public void participateToEvent(long userID,long eventID) {
        //insert into events_users
        String query="insert into events_users (id_user,id_event,notify) values ("+userID+" , "+eventID+",1)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query))
        {
            int resultSet = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void stopParticipatingToEvent(long userID,long eventID) {
        String query="delete from events_users where id_user="+userID+" and id_event="+eventID;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query))
        {
            int resultSet = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //NOTIFY--> 1 IF YES/ 0 IF NO
    public void notifyEvent(long userID,long eventID){
        String query="update events_users set notify=1 where id_user="+userID+" and id_event="+eventID;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query))
        {
            int resultSet = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void stopNotifyingEvent(long userID,long eventID){
        String query="update events_users set notify=0 where id_user="+userID+" and id_event="+eventID;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query))
        {
            int resultSet = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public Iterable<Long> getAllNotifyingEvents(long userID){
        String query="select * from events_users where notify=1 and id_user="+userID;
        List<Long> events=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long id=resultSet.getLong("id_event");
                events.add(id);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return events;
    }

    public Iterable<Long> getAllEventsParticipatingTo(long userID){
        String query="select * from events_users where id_user="+userID;
        List<Long> events=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long id=resultSet.getLong("id_event");
                events.add(id);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return events;
    }
    public Iterable<Utilizator> getAllParticipantsToEvent(long eventID){
        String query="select * from events_users where id_event="+eventID;
        List<Utilizator> participants=new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long id=resultSet.getLong("id_user");
                Utilizator u=userRepo.findOne(id);
                participants.add(u);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return participants;
    }


    //pagination
    public Iterable<Event> findAllEventsPaged(long lastID, int pageSize) {
        List<Event> set = new ArrayList<>();
        String query ="select * from events e where start_date>current_date and "+
                "e.id>"+lastID+" limit "+pageSize;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Event e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public Page<Event> findAllPage(Pageable pageable,long lastID) {
//        Paginator<Utilizator> paginator = new Paginator<Utilizator>(pageable, this.findAllUsersNonFriends(userID, name));
//        return paginator.paginate();
        return new PageImplementation<Event>(pageable, StreamSupport.stream(this.findAllEventsPaged(lastID,pageable.getPageSize()).spliterator(),false));
    }


}
