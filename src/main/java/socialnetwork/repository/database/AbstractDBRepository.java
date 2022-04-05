package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.*;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private String tableName;
    private String url;
    private String username;
    private String password;
    private Validator<E> validator;

    public AbstractDBRepository(String tableName, String url, String username, String password, Validator<E> validator) {
        this.tableName = tableName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public abstract E extractEntity(ResultSet resultSet) throws SQLException;

    public abstract String queryInsert(E entity);
    public abstract String queryDelete(ID id);
    public abstract String queryFindOne(ID id);
    public abstract String queryFindAll();
    public abstract String queryGenerateID();
    public abstract String queryUpdate(E entity);

    @Override
    public E save(E entity) {
        if(entity!=null){
            insertToDatabase(entity);
            return null;
        }
        return entity;
    }

    @Override
    public E delete(ID id) {
        if(id!=null) {
           return deleteFromDatabase(id);
        }
        return null;
    }

    @Override
    public E update(E entity) {
        if(entity!=null) {
//            deleteFromDatabase(entity.getId());
//            insertToDatabase(entity);
            updateToDatabase(entity);
            return null;
        }
        return entity;
    }

    @Override
    public E findOne(ID id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(queryFindOne(id));
             ResultSet resultSet = statement.executeQuery()) {

            if(!resultSet.next()) {
                return null;
            }
            else {
               E e=extractEntity(resultSet);
                return e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        List<E> set = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(queryFindAll());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                E e=extractEntity(resultSet);
                set.add(e);
            }

            return set;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }


    protected E updateToDatabase(E entity){
        try{
            validator.validate(entity);
        }catch(ValidationException e){throw new ValidationException (e.getMessage());}
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(queryUpdate(entity)))
        {
            int resultSet = statement.executeUpdate();

            if (resultSet==0) {
                return entity;
            }
            else return null;

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    protected E insertToDatabase(E entity) {
        try{
        validator.validate(entity);
        }catch(ValidationException e){throw new ValidationException (e.getMessage());}
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(queryInsert(entity)))
        {
            int resultSet = statement.executeUpdate();

            if (resultSet==0) {
                return entity;
            }
            else return null;

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    protected E deleteFromDatabase(ID id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementDrop = connection.prepareStatement(queryDelete(id)))
        {
            statementDrop.executeUpdate();
            E u=null;
            if(findOne(id)!=null)
                u=findOne(id);
            int resultSet = statementDrop.executeUpdate();

            if (resultSet != 0) { return u; }
            else return null;

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Long generateID(){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(queryGenerateID());
             ResultSet resultSet = statement.executeQuery()) {

            if(!resultSet.next()) {
                return null;
            }else {
                Array id = resultSet.getArray("ids");
                if(id!=null){
                    Long[] listTo = (Long[]) id.getArray();
                    return listTo[0]+1;}
                else return 1L;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }





    @Override
    public int size() {
        int dim;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) as size from table_name ");
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

