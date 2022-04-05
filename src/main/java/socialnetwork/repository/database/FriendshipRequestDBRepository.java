package socialnetwork.repository.database;

import socialnetwork.domain.*;
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


public class FriendshipRequestDBRepository extends AbstractDBRepository<Tuple<Long,Long>,FriendshipRequest> {


    private  String url;
    private String username;
    private String password;

    public FriendshipRequestDBRepository(String dbName,String url, String username, String password, Validator<FriendshipRequest> validator) {
        super(dbName,url,username,password,validator);
        this.url=url;
        this.username=username;
        this.password=password;
    }
    @Override
    public FriendshipRequest extractEntity(ResultSet resultSet) throws SQLException {
        Long requesterID=resultSet.getLong("requester_id");
        Long addresseeID=resultSet.getLong("addressee_id");
        String request=resultSet.getString("request_state");
        String createdDateString=resultSet.getString("date");
        LocalDateTime createdDate=LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);

        FriendshipRequest fr=new FriendshipRequest(requesterID,addresseeID,request);
        fr.setDate(createdDate);
        return fr;
    }


    @Override
    public String queryGenerateID(){
        return "SELECT array_agg(id) ids FROM friendship_requests WHERE id = (SELECT MAX(id) FROM friendship_requests)";
    }

    @Override
    public String queryInsert(FriendshipRequest entity) {
        Long requesterID=entity.getId().getLeft();
        Long addresseeID=entity.getId().getRight();
        String request="'"+entity.getRequest()+"'";
        String date="'"+entity.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        //Long id=generateID();
        return "INSERT INTO friendship_requests (requester_id, addressee_id,request_state,date) " +
                "VALUES ("+requesterID+","+addresseeID+","+request+","+date+");";

 }

    @Override
    public String queryDelete(Tuple<Long, Long> request) {
        return "DELETE FROM friendship_requests WHERE requester_id="+request.getLeft()+" AND addressee_id="+request.getRight();

    }

    @Override
    public String queryFindOne(Tuple<Long, Long> longLongTuple) {
        return "SELECT * from friendship_requests where requester_id="
                +longLongTuple.getLeft()+"and addressee_id="+longLongTuple.getRight();
    }

    @Override
    public String queryFindAll() {
        return "SELECT * from friendship_requests";
    }

    @Override
    public String queryUpdate(FriendshipRequest f){
        return "Update friendship_requests"
                +" set request_state='"+f.getRequest()+"', date='"+f.getDate()+"'"+
        " where requester_id="+f.getId().getLeft()+" and addressee_id="+f.getId().getRight();
    }

    public Iterable<FriendshipRequest> findAllFRReceived(long userID,long lastID,int pageSize) {
        List<FriendshipRequest> set = new ArrayList<>();
//        String query="select * from friendship_requests where addressee_id="+userID+
//                "AND id>"+lastID+" limit "+pageSize;
        String query="select addressee_id+requester_id-"+userID+" as requester_id,addressee_id,request_state,date\n" +
                "from friendship_requests where addressee_id="+userID+"\n" +
                " and requester_id>"+lastID+
                " order by requester_id limit "+pageSize;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                FriendshipRequest e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }
    public Iterable<FriendshipRequest> findAllFRSent(long userID,long lastID,int pageSize) {
        List<FriendshipRequest> set = new ArrayList<>();
//        String query="select * from friendship_requests where requester_id="+userID+
//                "AND id>"+lastID+" limit "+pageSize;
        String query="select addressee_id+requester_id-"+userID+" as addressee_id,requester_id,request_state,date\n" +
                "from friendship_requests where (requester_id="+userID+
                " and addressee_id>"+lastID+")\n" +
                "order by addressee_id limit "+pageSize;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                FriendshipRequest e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public Page<FriendshipRequest> findAllPageFRSent(Pageable pageable,long userID,long lastID) {
//        Paginator<FriendshipRequest> paginator = new Paginator<FriendshipRequest>(pageable, this.findAllFRSent(userID));
//        return paginator.paginate();
        return new PageImplementation<FriendshipRequest>(pageable, StreamSupport.stream(this.findAllFRSent(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }
    public Page<FriendshipRequest> findAllPageFRReceived(Pageable pageable,long userID,long lastID) {
//        Paginator<FriendshipRequest> paginator = new Paginator<FriendshipRequest>(pageable, this.findAllFRReceived(userID));
//        return paginator.paginate();
        return new PageImplementation<FriendshipRequest>(pageable, StreamSupport.stream(this.findAllFRReceived(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }
}


