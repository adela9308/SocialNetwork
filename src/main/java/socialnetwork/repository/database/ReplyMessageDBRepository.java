//package socialnetwork.repository.database;
//
//import socialnetwork.domain.Message;
//import socialnetwork.domain.ReplyMessage;
//import socialnetwork.domain.Utilizator;
//import socialnetwork.domain.validators.Validator;
//import socialnetwork.repository.Repository;
//import socialnetwork.utils.Constants;
//
//import java.sql.Array;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//
//public class ReplyMessageDBRepository extends AbstractDBRepository<Long, ReplyMessage> {
//
//    //private Repository<Long, Message> repoMessage;
//    //private Repository<Long,Utilizator> repoUser;
//    private MessagesDBRepository repoMessage;
//    private UserDBRepository repoUser;
//
//    public ReplyMessageDBRepository(String tableName, String url, String username, String password, Validator<ReplyMessage> validator,
//                                    MessagesDBRepository repoMessage,UserDBRepository repoUser ) {
//        super(tableName, url, username, password, validator);
//        this.repoMessage=repoMessage;
//        this.repoUser=repoUser;
//    }
//
//    @Override
//    public ReplyMessage extractEntity(ResultSet resultSet) throws SQLException {
//        Long id = resultSet.getLong("id");
//        Long id_message = resultSet.getLong("id_message");
//        Array idToList= resultSet.getArray("to_list");
//        Long idFrom = resultSet.getLong("id_from");
//        String message=resultSet.getString("message");
//        String dateString=resultSet.getString("date");
//        LocalDateTime date=LocalDateTime.parse(dateString, Constants.DATE_TIME_FORMATTER);
//
//        Utilizator fromUser=null;
//        ArrayList<Utilizator> toUsers=new ArrayList<>();
//
//        Long[] listTo=(Long[])idToList.getArray();
//        for(Long idTo:listTo) {
//            Utilizator u=repoUser.findOne(idTo);
//            if (u!=null) toUsers.add(u);
//        }
//        if(repoUser.findOne(idFrom)!=null)
//            fromUser=repoUser.findOne(idFrom);
//
//        Message mess=repoMessage.findOne(id_message);
//        ReplyMessage reply_mess = new ReplyMessage(fromUser,toUsers,message,mess);
//        reply_mess.setId(id);
//        return reply_mess;
//    }
//
//    @Override
//    public String queryInsert(ReplyMessage entity) {
//        String message="'"+entity.getMessage()+"'";
//        String createdDate="'"+entity.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
//        Long idFrom=entity.getFrom().getId();
//        Long id=generateID2();
//        String query="Insert into reply_message(\n"+
//                "id,id_message,id_to,id_from,message,date)\n"+"Values ";
//        for(Utilizator u:entity.getTo())
//            query+="("+id+","+entity.getMessageObject().getId()+","+u.getId()+","+idFrom+","+message+","+createdDate+"),";
//        query = query.substring(0, query.length()-1) + ";";
//        return  query;
//    }
//
//    @Override
//    public String queryDelete(Long aLong) {
//        return "DELETE FROM reply_message WHERE id="+aLong;
//    }
//
//    @Override
//    public String queryFindOne(Long aLong) {
//        return "SELECT id,id_message, id_from,ARRAY_AGG(id_to) to_list, message,date\n" +
//                "from reply_message\n" +
//                "where id="+aLong+"\n"+
//                "group by id,id_from,message,date,id_message\n" +
//                "order by id";
//
//    }
//
//    @Override
//    public String queryFindAll() {
//        return "SELECT id, id_message id_from,ARRAY_AGG(id_to) to_list, message,date\n" +
//                "from reply_message\n" +
//                "group by id,id_from,message,date,id_message\n" +
//                "order by id";
//    }
//}


package socialnetwork.repository.database;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.utils.Constants;
import sun.nio.ch.Util;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class ReplyMessageDBRepository extends AbstractDBRepository<Long, ReplyMessage> {

    private UserDBRepository repoUser;
    private MessagesDBRepository repoMessage;
    private Map<Long,Utilizator> usersChached;
    private Map<Long,Message> messagesChached;

    private String url;
    private String username;
    private String password;

    public ReplyMessageDBRepository(String tableName, String url, String username, String password,
               Validator<ReplyMessage> validator,UserDBRepository repoUser,MessagesDBRepository repoMessage) {
        super(tableName, url, username, password, validator);
        this.repoUser=repoUser;
        this.repoMessage=repoMessage;
        this.usersChached=new HashMap<>();
        this.messagesChached=new HashMap<>();

        this.url=url;
        this.username=username;
        this.password=password;
    }

    @Override
    public ReplyMessage extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Array idToList= resultSet.getArray("to_list");
        Long idFrom = resultSet.getLong("id_from");
        String message=resultSet.getString("message");
        String dateString=resultSet.getString("date");
        LocalDateTime date=LocalDateTime.parse(dateString, Constants.DATE_TIME_FORMATTER);
        Long repliedID=resultSet.getLong("reply");


        ArrayList<Utilizator> toUsers=new ArrayList<>();

       Long[] listTo=(Long[])idToList.getArray();

        for(Long idTo:listTo) {
            Utilizator toMapUser=usersChached.get(idTo);
            if(toMapUser==null) {
                toMapUser = repoUser.findOne(idTo);
                if (toMapUser != null) {
                    usersChached.put(toMapUser.getId(), toMapUser);
                    toUsers.add(toMapUser);
                }
            }else toUsers.add(toMapUser);
        }

        Utilizator fromUser=usersChached.get(idFrom);
        if(fromUser==null) {
            fromUser = repoUser.findOne(idFrom);
            if(fromUser!=null) usersChached.put(fromUser.getId(), fromUser);
        }

        Message repliedMessage=messagesChached.get(repliedID);
        if(repliedMessage==null) {
            repliedMessage = repoMessage.findOne(repliedID);
            if(repliedMessage!=null) messagesChached.put(repliedMessage.getId(),repliedMessage);
        }

        ReplyMessage replymess = new ReplyMessage(fromUser,toUsers,message,repliedMessage);
        replymess.setId(id);
        replymess.setDate(date);
        return replymess;
    }

    @Override
    public String queryGenerateID(){
        return "SELECT array_agg(id) ids FROM messages WHERE id = (SELECT MAX(id) FROM messages)";
    }

    @Override
    public String queryInsert(ReplyMessage entity) {
        String message="'"+entity.getMessage()+"'";
        Long id;
        id=generateID();
        String createdDate="'"+entity.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        Long idFrom=entity.getFrom().getId();
        String query="Insert into messages(\n"+
                "id,id_from,id_to,message,date,reply)\n"+"Values ";
        for(Utilizator u:entity.getTo())
            query+="("+id+","+idFrom+","+u.getId()+","+message+","+createdDate+","+entity.getMessageObject().getId()+"),";
        query = query.substring(0, query.length()-1) + ";";
        return  query;
    }

    @Override
    public String queryDelete(Long aLong) {
        return "DELETE FROM messages WHERE id="+aLong;
    }

    @Override
    public String queryFindOne(Long aLong) {
        return "SELECT id, id_from,ARRAY_AGG(id_to) to_list, message,date,reply\n" +
                "from messages\n" +
                "where id="+aLong+"\n"+
                "group by id,id_from,message,date,reply\n" +
                "order by id";

    }

    @Override
    public String queryFindAll() {
        return "SELECT id, id_from,ARRAY_AGG(id_to) to_list, message,date,reply\n" +
                "from messages\n" +
                "group by id,id_from,message,date,reply\n" +
                "order by id";
    }

    @Override
    public String queryUpdate(ReplyMessage r){
        return null;
    }

    //conversation with a user
    public Iterable<ReplyMessage> findAllMessagesConversation(long userID1,long userID2,long lastID,int pageSize) {
        List<ReplyMessage> set = new ArrayList<>();
        String query="SELECT distinct id, id_from,ARRAY_AGG(id_to) to_list, message,date,reply from messages where ((id_from="
                +userID1+" and id_to="+userID2+") or (id_to="+
                userID1+" and id_from="+userID2+"))"+
                " AND id<"+lastID+
                " group by id,id_from,message,date,reply order by date desc "+" limit "+pageSize;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ReplyMessage e=extractEntity(resultSet);
                set.add(e);
            }
            Collections.reverse(set);
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(set);
        return set;
    }
    public Page<ReplyMessage> findAllPage(Pageable pageable,long userID1,long userID2,long lastID) {
//        Paginator<ReplyMessage> paginator = new Paginator<ReplyMessage>(pageable, this.findAllMessagesConversation(userID1,userID2,lastID,pageSize));
//        return paginator.paginate();
        return new PageImplementation<ReplyMessage>(pageable, StreamSupport.stream(this.findAllMessagesConversation(userID1,userID2,lastID,pageable.getPageSize()).spliterator(),false));
    }

    //users you have convesations with
    public ReplyMessage extractEntityUsersConversation(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long idToo= resultSet.getLong("id_to");
        Long idFrom = resultSet.getLong("id_from");
        String message=resultSet.getString("message");
        String dateString=resultSet.getString("date");
        LocalDateTime date=LocalDateTime.parse(dateString, Constants.DATE_TIME_FORMATTER);
        Long repliedID=resultSet.getLong("reply");


        ArrayList<Utilizator> toUsers=new ArrayList<>();

        List<Long> listTo=new ArrayList();
        listTo.add(idToo);


        for(Long idTo:listTo) {
            Utilizator toMapUser=usersChached.get(idTo);
            if(toMapUser==null) {
                toMapUser = repoUser.findOne(idTo);
                if (toMapUser != null) {
                    usersChached.put(toMapUser.getId(), toMapUser);
                    toUsers.add(toMapUser);
                }
            }else toUsers.add(toMapUser);
        }

        Utilizator fromUser=usersChached.get(idFrom);
        if(fromUser==null) {
            fromUser = repoUser.findOne(idFrom);
            if(fromUser!=null) usersChached.put(fromUser.getId(), fromUser);
        }

        Message repliedMessage=messagesChached.get(repliedID);
        if(repliedMessage==null) {
            repliedMessage = repoMessage.findOne(repliedID);
            if(repliedMessage!=null) messagesChached.put(repliedMessage.getId(),repliedMessage);
        }

        ReplyMessage replymess = new ReplyMessage(fromUser,toUsers,message,repliedMessage);
        replymess.setId(id);
        replymess.setDate(date);
        return replymess;
    }

    public Iterable<ReplyMessage> findAllUsersConversation(long userID/*,long lastID*/) {
        List<ReplyMessage> set = new ArrayList<>();
//        String query="select id,id_from+id_to-"+userID+" as id_from,"+userID+" as id_to, message,date,reply \n" +
//                "from messages \n" +
//                "where (id_from="+userID+" or id_to="+userID+") and id<="+lastID+" \n" +
//                "order by date desc,(id_from+id_to-"+userID+") asc \n"+
//                "limit "+pageSize;

        String query="select id,id_from+id_to-"+userID+" as id_from,"+userID+" as id_to, message,date,reply \n" +
                "from messages \n" +
                "where (id_from="+userID+" or id_to="+userID+") \n" +
                "order by date desc,(id_from+id_to-"+userID+") asc \n";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            List<Long> usersConversation=new ArrayList<>();
            while (resultSet.next()) {
                ReplyMessage e=extractEntityUsersConversation(resultSet);
                if(!usersConversation.contains(e.getFrom().getId())) {
                    set.add(e);
                    usersConversation.add(e.getFrom().getId());
                }
            }
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }
    public Page<ReplyMessage> findAllPageUsersConversation(Pageable pageable,long userID) {
        Paginator<ReplyMessage> paginator = new Paginator<ReplyMessage>(pageable, this.findAllUsersConversation(userID));
        return paginator.paginate();
        //return new PageImplementation<ReplyMessage>(pageable, StreamSupport.stream(this.findAllUsersConversation(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }

    //group conversations
    public Iterable<ReplyMessageDTO> findAllGroupConversation(long userID,long lastID,int pageSize) {
        List<ReplyMessageDTO> set = new ArrayList<>();
        String query="select id,id_from,message,reply,date,ARRAY_AGG(id_to) as to_list\n" +
                "from messages\n" +
                "group by id_from,date,id,message,reply\n" +
                "having array_length(ARRAY_AGG(id), 1)>1 and (id_from="+userID+" or "+userID+"=any(ARRAY_AGG(id_to))) and id<"+lastID+"\n" +
                "order by date desc " +
                "limit "+pageSize;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ReplyMessageDTO e=extractEntityDTO(resultSet);
                set.add(e);
            }
            Collections.reverse(set);
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(set);
        return set;
    }

    public Page<ReplyMessageDTO> findAllGroupConversationPaged(Pageable pageable,long userID,long lastID) {
        return new PageImplementation<ReplyMessageDTO>(pageable, StreamSupport.stream(this.findAllGroupConversation(userID,lastID,pageable.getPageSize()).spliterator(),false));
    }

    public ReplyMessage getLastMessageWithUser(Long userID1,Long userID2){
        ReplyMessage r=null;
        String query="select * from messages \n" +
                "where (id_from="+userID1+" and id_to="+userID2+") or (id_to="+userID2+" and id_from="+userID1+")\n" +
                "order by date desc\n" +
                "limit 1";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                r=extractEntityUsersConversation(resultSet);
            }
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ReplyMessageDTO getLastMessageWithGroup(Long userID){
        ReplyMessageDTO r=null;
        String query="select id,id_from,message,reply,date,ARRAY_AGG(id_to) as to_list\n" +
                "from messages\n" +
                "group by id_from,date,id,message,reply\n" +
                "having array_length(ARRAY_AGG(id), 1)>1 and (id_from="+userID+" or "+userID+"=any(ARRAY_AGG(id_to))) \n" +
                "order by date desc \n" +
                "limit 1";//macar se salveaza in baza de date?daca da mesajul  lasa ca ma conving eu ca n am inteles

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                r=extractEntityDTO(resultSet);
            }
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ReplyMessageDTO extractEntityDTO(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Array idToList= resultSet.getArray("to_list");
        Long idFrom = resultSet.getLong("id_from");
        String message=resultSet.getString("message");
        String dateString=resultSet.getString("date");
        LocalDateTime date=LocalDateTime.parse(dateString, Constants.DATE_TIME_FORMATTER);
        Long repliedID=resultSet.getLong("reply");


        ArrayList<Utilizator> toUsers=new ArrayList<>();

        Long[] listTo=(Long[])idToList.getArray();

        for(Long idTo:listTo) {
            Utilizator toMapUser=usersChached.get(idTo);
            if(toMapUser==null) {
                toMapUser = repoUser.findOne(idTo);
                if (toMapUser != null) {
                    usersChached.put(toMapUser.getId(), toMapUser);
                    toUsers.add(toMapUser);
                }
            }else toUsers.add(toMapUser);
        }

        Utilizator fromUser=usersChached.get(idFrom);
        if(fromUser==null) {
            fromUser = repoUser.findOne(idFrom);
            if(fromUser!=null) usersChached.put(fromUser.getId(), fromUser);
        }

        Message repliedMessage=messagesChached.get(repliedID);
        if(repliedMessage==null) {
            repliedMessage = repoMessage.findOne(repliedID);
            if(repliedMessage!=null) messagesChached.put(repliedMessage.getId(),repliedMessage);
        }

        ReplyMessage replymess = new ReplyMessage(fromUser,toUsers,message,repliedMessage);
        replymess.setId(id);
        replymess.setDate(date);
        ReplyMessageDTO r=new ReplyMessageDTO(replymess);
        r.setId(replymess.getId());
        return r;
    }
    public List<ReplyMessage> getConvoWithUserBetweenDates(long id_to, long id_from, LocalDate date1, LocalDate date2) {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        String date1String="'"+date1.toString()+" 00:00:00"+"'";
        String date2String="'"+date2.toString()+" 23:59:59"+"'";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement("select *\n" +
                     "from messages\n" +
                     "where (id_from="+id_from+" and id_to="+id_to+") and (date>="+date1String+" and date<="+date2String+")\n" +
                     "order by date");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReplyMessage entity = extractEntityUsersConversation(resultSet);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
