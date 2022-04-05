/*package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, Utilizator>{


        private String url;
        private String username;
        private String password;
        private Validator<Utilizator> validator;

        public UserDBRepository(String url, String username, String password, Validator<Utilizator> validator) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.validator = validator;
        }
        @Override
        public Utilizator findOne(Long aLong) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * from users where id="+aLong);
                 ResultSet resultSet = statement.executeQuery()) {

                if(!resultSet.next()) {
                    return null;
                }
                else {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
                    return utilizator;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Iterable<Utilizator> findAll() {
            Set<Utilizator> users = new HashSet<>();
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * from users");
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
                    users.add(utilizator);
                }
                return users;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        public Utilizator save(Utilizator entity) {
            String firstName="'"+entity.getFirstName() +"'";
            String lastName="'"+entity.getLastName()+"'";
            //String querry="INSERT INTO users (firstname, lastname) VALUES ("+firstName+","+lastName+");";
            validator.validate(entity);
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement
                         ("INSERT INTO users(first_name, last_name) VALUES ("+firstName+","+lastName+");");) {

                int resultSet = statement.executeUpdate();

                if (resultSet==0) {
                    return entity;
                }
                else return null;

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Utilizator delete(Long aLong) {
            if(aLong==null) throw new IllegalArgumentException("ID cannot be null");
            try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement("delete from users where id=" + aLong);
                ) {
                Utilizator u=null;
                if(findOne(aLong)!=null)
                    u=findOne(aLong);
                int resultSet = statement.executeUpdate();

                if (resultSet != 0) { return u; }
                    else return null;

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        public Utilizator update(Utilizator entity) {
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
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) as size from users");
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
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class UserDBRepository extends AbstractDBRepository<Long, Utilizator> {


    private String url;
    private String username;
    private String password;
    public UserDBRepository(String dbName,String url, String username, String password, Validator<Utilizator> validator) {
        super(dbName,url,username,password,validator);
        this.url=url;
        this.username=username;
        this.password=password;
    }



    @Override
    public String queryGenerateID(){
        return "SELECT array_agg(id) ids FROM users WHERE id = (SELECT MAX(id) FROM users)";
    }

    @Override
    public Utilizator extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String mail= resultSet.getString("email");
        String pass= resultSet.getString("password");
        Utilizator user = new Utilizator(firstName, lastName,mail,pass);
        user.setId(id);
        return user;
    }


    @Override
    public String queryDelete(Long userID) {
        return "DELETE FROM users WHERE id="+userID;
    }

    @Override
    public String queryFindOne(Long aLong) {
        return "SELECT * from users where id="+aLong;
    }


    public Utilizator findOneMailPassword(String mail, String pass) {
        if(mail.isEmpty()||pass.isEmpty()) return null;
        String query="SELECT * from users where email='"+mail+"' and password = crypt('"+pass+"', password)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if(resultSet.next()) {
                return extractEntity(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String queryFindAll() {
        return "SELECT * from users";
    }

    @Override
    public String queryUpdate(Utilizator u){
        return null;
    }
    @Override
    public String queryInsert(Utilizator user) {
        Long id=generateID();
        String firstName="'"+user.getFirstName()+"'";
        String lastName="'"+user.getLastName()+"'";
        return "INSERT INTO users (id,first_name, last_name,email,password) VALUES ("+id+","+firstName+","+lastName+", '"+user.getEmail()+"' ,"+
        "crypt('"+user.getPassword()+"',gen_salt('bf',8)));";

    }
    public Iterable<Utilizator> findAllUsersNonFriendsFiltered(long userID,String name,long lastID,int pageSize) {
        List<Utilizator> set = new ArrayList<>();
        String query="";
        if(name!=null)
        query= "SELECT u2.id,u2.first_name,u2.last_name,u2.email,u2.password \n"+
                "FROM users u1,users u2 \n"+
                "WHERE NOT EXISTS(SELECT 1 FROM friendships f \n"+
                "WHERE (\n" +
                "(f.requester_id = u1.id AND f.addressee_id = u2.id) or\n"+
                "(f.requester_id = u2.id AND f.addressee_id = u1.id)))\n"+
                "and not exists(select 1 from friendship_requests fr\n" +
                "where((fr.requester_id = u1.id AND fr.addressee_id = u2.id) or\n" +
                "(fr.requester_id = u2.id AND fr.addressee_id = u1.id)))"+
                "AND u1.id != u2.id AND u1.id = "+userID +"\n" +
                "AND (LOWER(CONCAT(u2.first_name, ' ', u2.last_name)) like '"+name+"%'"+
                "OR LOWER(CONCAT(u2.last_name, ' ', u2.first_name)) like '"+name+"%')\n"+
                "AND u2.id>"+lastID+" limit "+pageSize;
        else query="SELECT u2.id,u2.first_name,u2.last_name,u2.email,u2.password \n"+
                "FROM users u1,users u2 \n"+
                "WHERE NOT EXISTS(SELECT 1 FROM friendships f \n"+
                "WHERE (\n" +
                "(f.requester_id = u1.id AND f.addressee_id = u2.id) or\n"+
                "(f.requester_id = u2.id AND f.addressee_id = u1.id)))\n"+
                "and not exists(select 1 from friendship_requests fr\n" +
                "where((fr.requester_id = u1.id AND fr.addressee_id = u2.id) or\n" +
                "(fr.requester_id = u2.id AND fr.addressee_id = u1.id)))"+
                "AND u1.id != u2.id AND u1.id = "+userID +"\n" +
                "AND u2.id>"+lastID+" limit "+pageSize;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Utilizator e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }
    public Page<Utilizator> findAllPageFiltered(Pageable pageable,long userID,String name,long lastID) {
        //Paginator<Utilizator> paginator = new Paginator<Utilizator>(pageable, this.findAllUsersNonFriendsFiltered(userID,name));
        //return paginator.paginate();
        return new PageImplementation<Utilizator>(pageable, StreamSupport.stream(this.findAllUsersNonFriendsFiltered(userID,name,lastID,pageable.getPageSize()).spliterator(),false));
    }

    public Iterable<Utilizator> findAllUsersWithoutU(long userID, String name, long lastID, int pageSize) {
        List<Utilizator> set = new ArrayList<>();
        String query;
        if(name!=null)
            query="select * from users u where id!="+userID+" AND (LOWER(CONCAT(first_name, ' ', last_name)) like '"+name+"%'"+
                    " OR LOWER(CONCAT(last_name, ' ', first_name)) like '"+name+"%')"+
                    " AND u.id>"+lastID+" limit "+pageSize;

        else query="select * from users u where id!="+userID+
                "AND u.id>"+lastID+" limit "+pageSize;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Utilizator e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public Page<Utilizator> findAllPage(Pageable pageable,long userID,String name,long lastID) {
//        Paginator<Utilizator> paginator = new Paginator<Utilizator>(pageable, this.findAllUsersNonFriends(userID, name));
//        return paginator.paginate();
        return new PageImplementation<Utilizator>(pageable, StreamSupport.stream(this.findAllUsersWithoutU(userID,name,lastID,pageable.getPageSize()).spliterator(),false));
    }



}

