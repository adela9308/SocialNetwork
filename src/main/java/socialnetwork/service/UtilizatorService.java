package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.exceptions.UserValidationException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.UserDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilizatorService  {
    //private Repository<Long, Utilizator> repo;
    private UserDBRepository repo;
    private Repository<Tuple<Long,Long>,Prietenie> repo_pr;


    public UtilizatorService(UserDBRepository repo,Repository<Tuple<Long,Long>,Prietenie> repo_pr) {
        this.repo = repo;
        this.repo_pr=repo_pr;
    }

    /**
     * Adauga un utilizator
     * @param messageTask utilizatorul de adaugat
     * @return utilizatorul adaugat
     */
    public Utilizator addUtilizator(Utilizator messageTask) {

        for(Utilizator u:repo.findAll()) {
            if (u.getEmail().equals(messageTask.getEmail()))
                throw new ServiceException("There is already an account using this email \n");
        }
        try{
        Utilizator task = repo.save(messageTask);
        return task;
       } catch(UserValidationException e){throw new ServiceException(e.getMessage());}
    }

    /**
     * sterge un utilizator
     * @param idd id-ul utilizatorului de sters
     * @return utilizatorul sters
     */
    public Utilizator removeUtilizator(Long idd){
        String error = "";
        if(repo.findOne(idd)==null) error += "ID doesn't exist \n";

        if(error.isEmpty()){
            List<Prietenie> deSters=new ArrayList<>();
            for(Prietenie p: repo_pr.findAll())
                    if(p.getId().getLeft() == idd||p.getId().getRight()==idd) {
                        deSters.add(p);}
            for(Prietenie p:deSters){
                        Long idd1 = p.getId().getLeft(), idd2 = p.getId().getRight();
                        repo.findOne(idd1).removeFriend(repo.findOne(idd2));
                        repo.findOne(idd2).removeFriend(repo.findOne(idd1));
                        repo_pr.delete(p.getId());
                    }
            Utilizator del=repo.delete(idd);
            return del;
        }
        else throw new ServiceException(error);
    }

    /**
     * @return toate obiectele de tip Utilizator
     */
    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    /**
     * @param id id-ul utilizatorului cautat
     * @return obiectul de tip utiliator cu id ul dat
     */
    public Utilizator getUtilizator(Long id){
        for(Utilizator u:repo.findAll())
            if(u.getId()==id) return u;
        return null;
    }
    public Utilizator getUtilizatorMailPassword(String mail,String pass){
        return repo.findOneMailPassword(mail,pass);
    }
    public Utilizator getUtilizatorName(String firstName,String LastName){
        for(Utilizator u:repo.findAll())
            if(u.getFirstName().equals(firstName)&&u.getLastName().equals(LastName)) return u;
        return null;
    }
    /**
     * @return id ul ulitmului utilizator din fisier
     */
    public Long getLastID(){
        long id = 0;
        for(Utilizator u: repo.findAll())
            id=u.getId();
        return id;
    }

    //paging
    //search add new friend
    private int pageFiltered = -1;
    private int sizeFiltered = 1;

    public void setPageSizeFiltered(int size) {
        this.sizeFiltered = size;
    }
    public int getPageSizeFiltered(){return sizeFiltered;}

    public List<Utilizator> getNextUsersNonFriendsFiltered(long userID, String name,long lastID) {
        this.pageFiltered++;
        return getUsersOnPageFiltered(this.pageFiltered,userID,name,lastID);
    }
    public List<Utilizator> getUsersOnPageFiltered(int page,long userID,String name,long lastID) {
        this.pageFiltered=page;
        Pageable pageable = new PageableImplementation(page, this.sizeFiltered);
        Page<Utilizator> studentPage = repo.findAllPageFiltered(pageable,userID,name,lastID);
        return studentPage.getContent().collect(Collectors.toList());
    }

    //search send new message
    private int page = -1;
    private int size = 1;

    public void setPageSize(int size) {
        this.size = size;
    }
    public int getPageSize(){return size;}

    public List<Utilizator> getNextUsersNonFriends(long userID,String name,long lastID) {
        this.page++;
        return getUsersOnPage(this.page,userID,name,lastID);
    }

    public List<Utilizator> getUsersOnPage(int page,long userID,String name,long lastID) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Utilizator> studentPage = repo.findAllPage(pageable,userID,name,lastID);
        return studentPage.getContent().collect(Collectors.toList());
    }
    //search report
    private int pageReport = -1;
    private int sizeReport = 1;

    public void setPageSizeReport(int sizeReport) {
        this.sizeReport = sizeReport;
    }
    public int getPageSizeReport(){return sizeReport;}

    public List<Utilizator> getNextUsersNonFriendsReport(long userID,String name,long lastID) {
        this.pageReport++;
        return getUsersOnPage(this.pageReport,userID,name,lastID);
    }

    public List<Utilizator> getUsersOnPageReport(int page,long userID,String name,long lastID) {
        this.pageReport=page;
        Pageable pageable = new PageableImplementation(page, this.sizeReport);
        Page<Utilizator> studentPage = repo.findAllPage(pageable,userID,name,lastID);
        return studentPage.getContent().collect(Collectors.toList());
    }

}
