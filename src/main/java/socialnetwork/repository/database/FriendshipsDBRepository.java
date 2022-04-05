/*package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipsDBRepository implements Repository<Tuple<Long,Long>,Prietenie> {


    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;


    public FriendshipsDBRepository(String url,String username,String password,Validator<Prietenie> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }
    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where requester_id="
                     +id.getLeft()+"and addressee_id="+id.getRight());
             ResultSet resultSet = statement.executeQuery()) {

            if(!resultSet.next()) {
                return null;
            }
            else {
                Long id_r = resultSet.getLong("requester_id");
                Long id_a=resultSet.getLong("addressee_id");
                String date_str = resultSet.getString("date");
                LocalDateTime date=LocalDateTime.parse(date_str, Constants.DATE_TIME_FORMATTER);

                Prietenie p=new Prietenie(id_r,id_a);
                p.setDate(date);
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id_r = resultSet.getLong("requester_id");
                Long id_a=resultSet.getLong("addressee_id");
                String date_str = resultSet.getString("date");
                LocalDateTime date=LocalDateTime.parse(date_str, Constants.DATE_TIME_FORMATTER);

                Prietenie p=new Prietenie(id_r,id_a);
                p.setDate(date);
                friendships.add(p);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Prietenie save(Prietenie entity) {
        String id_r="'"+entity.getId().getLeft() +"'";
        String id_a="'"+entity.getId().getRight()+"'";
        String date="'"+entity.getDate()+"'";
        //String querry="INSERT INTO users (firstname, lastname) VALUES ("+firstName+","+lastName+");";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement
                     ("INSERT INTO friendships(requester_id,addressee_id,date) VALUES ("+id_r+","+id_a+","+date+");");) {

            int resultSet = statement.executeUpdate();

            if (resultSet!=0) {
                return null;
            }
            else return entity;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie delete(Tuple<Long, Long> id) {
        if(id==null) throw new IllegalArgumentException("ID cannot be null");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from friendships where requester_id="
                     + id.getLeft()+" and addressee_id="+id.getRight());
        ) {
            Prietenie p=null;
            if(findOne(id)!=null)
                p=findOne(id);
            int resultSet = statement.executeUpdate();

            if (resultSet != 0) { return p; }
            else return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie update(Prietenie entity) {
        if(entity!=null) {
            delete(entity.getId());
            save(entity);
            return null;
        }
        return entity;
    }

    @Override
    public int size() {
        int dim;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) as size from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                dim=resultSet.getInt("size");
                return dim;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
*/
package socialnetwork.repository.database;


import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class FriendshipsDBRepository extends AbstractDBRepository<Tuple<Long,Long>, Prietenie> {

    private String url;
    private String username;
    private String password;
    public FriendshipsDBRepository(String dbName,String url, String username, String password, Validator<Prietenie> validator) {
        super(dbName,url,username,password,validator);
        this.url=url;
        this.username=username;
        this.password=password;
    }


    @Override
    public String queryGenerateID(){
     return null;
    }

    @Override
    public Prietenie extractEntity(ResultSet resultSet) throws SQLException {
        Long requesterID=resultSet.getLong("requester_id");
        Long addresseeID=resultSet.getLong("addressee_id");
        String createdDateString=resultSet.getString("date");
        LocalDateTime createdDate=LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
        Prietenie friendship=new Prietenie(requesterID,addresseeID);
        friendship.setDate(createdDate);
        return friendship;
    }


    @Override
    public String queryFindOne(Tuple<Long, Long> longLongTuple) {
        return "SELECT * from friendships where requester_id="
                +longLongTuple.getLeft()+"and addressee_id="+longLongTuple.getRight();
    }

    @Override
    public String queryFindAll() {
        return "SELECT * from friendships";
    }

    @Override
    public String queryInsert(Prietenie friendship) {
        Long requesterID=friendship.getId().getLeft();
        Long addresseeID=friendship.getId().getRight();
        String createdDate="'"+friendship.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        return "INSERT INTO friendships (requester_id, addressee_id,date) VALUES ("+requesterID+","+addresseeID+","+createdDate+");";
    }

    @Override
    public String queryDelete(Tuple<Long,Long> friendshipID) {
        return "DELETE FROM friendships WHERE requester_id="+friendshipID.getLeft()+" AND addressee_id="+friendshipID.getRight();
    }

    @Override
    public String queryUpdate(Prietenie f){
        return null;
    }

    public Iterable<Prietenie> findAllUserFriends(long userID,long lastID,int sizePage) {
        List<Prietenie> set = new ArrayList<>();
        //String query="select * from friendships where addressee_id="+userID+" or requester_id="+userID;
        String query="select addressee_id+requester_id-"+userID+" as requester_id,"+userID+" as addressee_id,date\n" +
                "from friendships where (requester_id="+userID+" or addressee_id="+userID+
                ") and addressee_id+requester_id-"+userID+">"+lastID+"\n" +
                "order by requester_id \n" +
                "limit "+sizePage;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }


    public Page<Prietenie> findAllPage(Pageable pageable,Long userID,long lastID) {
//        Paginator<Prietenie> paginator = new Paginator<Prietenie>(pageable, this.findAllUserFriends(userID));
//        return paginator.paginate();
        return new PageImplementation<Prietenie>(pageable,StreamSupport.stream(this.findAllUserFriends(userID,lastID, pageable.getPageSize()).spliterator(), false));
    }

}

