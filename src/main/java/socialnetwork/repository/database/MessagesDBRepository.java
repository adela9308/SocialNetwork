package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.utils.Constants;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MessagesDBRepository extends AbstractDBRepository<Long, Message> {


    private String url;
    private String username;
    private String password;
    private UserDBRepository repoUser;
    public MessagesDBRepository(String tableName, String url, String username, String password, Validator<Message> validator,UserDBRepository repoUser) {
        super(tableName, url, username, password, validator);
        this.repoUser=repoUser;
        this.url=url;
        this.username=username;
        this.password=password;
    }


    @Override
    public String queryGenerateID(){
        return "SELECT array_agg(id) ids FROM messages WHERE id = (SELECT MAX(id) FROM messages)";
    }

    @Override
    public Message extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Array idToList= resultSet.getArray("to_list");
        Long idFrom = resultSet.getLong("id_from");
        String message=resultSet.getString("message");
        String dateString=resultSet.getString("date");
        LocalDateTime date=LocalDateTime.parse(dateString, Constants.DATE_TIME_FORMATTER);

        Utilizator fromUser=null;
        ArrayList<Utilizator> toUsers=new ArrayList<>();

        Long[] listTo=(Long[])idToList.getArray();
        for(Long idTo:listTo) {
            Utilizator u=repoUser.findOne(idTo);
            if (u!=null) toUsers.add(u);
        }
        if(repoUser.findOne(idFrom)!=null)
            fromUser=repoUser.findOne(idFrom);

        Message mess = new Message(fromUser,toUsers,message);
        mess.setId(id);
        mess.setDate(date);
        return mess;
    }

    @Override
    public String queryInsert(Message entity) {
        String message="'"+entity.getMessage()+"'";
        Long id;
        id=generateID();
        String createdDate="'"+entity.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        Long idFrom=entity.getFrom().getId();
        String query="Insert into messages(\n"+
                "id,id_from,id_to,message,date)\n"+"Values ";
        for(Utilizator u:entity.getTo())
            query+="("+id+","+idFrom+","+u.getId()+","+message+","+createdDate+"),";
        query = query.substring(0, query.length()-1) + ";";
        return  query;
    }

    @Override
    public String queryDelete(Long aLong) {
        return "DELETE FROM messages WHERE id="+aLong;
    }

    @Override
    public String queryFindOne(Long aLong) {
        return "SELECT id, id_from,ARRAY_AGG(id_to) to_list, message,date\n" +
                "from messages\n" +
                "where id="+aLong+"\n"+
                "group by id,id_from,message,date\n" +
                "order by id";

    }

    @Override
    public String queryFindAll() {
        return "SELECT id, id_from,ARRAY_AGG(id_to) to_list, message,date\n" +
                "from messages\n" +
                "group by id,id_from,message,date\n" +
                "order by id";
    }

    @Override
    public String queryUpdate(Message f){
        return null;
    }

    public Page<Message> findAllPage(Pageable pageable) {
        Paginator<Message> paginator = new Paginator<Message>(pageable, this.findAll());
        return paginator.paginate();
    }
}
