package socialnetwork.service;


import socialnetwork.comunity.Graph;
import socialnetwork.domain.*;
import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipsDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.Constants;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendsChangeEvent;
import socialnetwork.utils.events.RequestsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrietenieService implements Observable<FriendsChangeEvent> {
    private FriendshipsDBRepository repo;
    private Repository<Long, Utilizator> repoU;
    private List<Observer<FriendsChangeEvent>> observers=new ArrayList<>();

    public PrietenieService(FriendshipsDBRepository repo,Repository<Long, Utilizator> repou) {
        this.repo = repo;
        this.repoU=repou;
        //load friends(adaug in listele de prieteni )
        for(Prietenie p:this.repo.findAll()){
            Long id1=p.getId().getLeft();Long id2=p.getId().getRight();
            this.repoU.findOne(id1).addFriend(this.repoU.findOne(id2));
            this.repoU.findOne(id2).addFriend(this.repoU.findOne(id1));
        }
    }

    @Override
    public void addObserver(Observer<FriendsChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendsChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendsChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }


    /**
     * Adaug o noua prietenie
     * @param idd1 id-ul utilizzatorului1
     * @param idd2 id-ul utilizzatorului2
     * @return obiectul de tip prietenie adaugat
     */
    public Prietenie addPrieten(Long idd1,Long idd2) {
        String error = "",error1="",error2="";
        if(repoU.findOne(idd1)==null) error1 += "ID1 doesn't exist \n";
        if(repoU.findOne(idd2)==null) error2 += "ID2 doesn't exist \n";
        error=error1+error2;
        if (error.isEmpty()) {
            try {
                Prietenie pr = new Prietenie(idd1, idd2);
                Prietenie p = repo.save(pr);
                notifyObservers(new FriendsChangeEvent(ChangeEventType.ADD,p));
//                repoU.findOne(idd1).addFriend(repoU.findOne(idd2));
//                repoU.findOne(idd2).addFriend(repoU.findOne(idd1));
                return p;
            }catch(PrietenieValidationException e){error += e.getMessage(); }
        }

        if(!error.isEmpty())throw new ServiceException(error);
        return null;
    }
    /**
     * Sterg o prietenie
     * @param idd1 id-ul utilizzatorului1
     * @param idd2 id-ul utilizzatorului2
     * @return obiectul de tip prietenie sters
     */
    public Prietenie removePrieten(Long idd1,Long idd2){
        String error = "",error1="",error2="";
        if(repoU.findOne(idd1)==null) error1 += "ID1 doesn't exist \n";
        if(repoU.findOne(idd2)==null) error2 += "ID2 doesn't exist \n";

        error=error1+error2;
        if(error.isEmpty())
        try {
            Prietenie pr = new Prietenie(idd1, idd2);
            Prietenie pr1 = new Prietenie(idd2, idd1);
            repoU.findOne(idd1).removeFriend(repoU.findOne(idd2));
            repoU.findOne(idd2).removeFriend(repoU.findOne(idd1));
            if(repo.delete(pr.getId())==null) {
                repo.delete(pr1.getId());
                notifyObservers(new FriendsChangeEvent(ChangeEventType.DELETE,pr1));
            }
        }catch(PrietenieValidationException e) {error+=e.getMessage();}

        if(!error.isEmpty())throw new ServiceException(error);
        return null;

    }
    /**
     * @return toate el de tip Prietenie
     */
    public Iterable<Prietenie> getAll(){
        return repo.findAll();
    }

    /**
     * @return nr de comunitati=nr de comp conexe
     */
    public int NumarComunitati(){
        Graph<Long> g=new Graph<Long>();
        for(Prietenie p:repo.findAll()){
            g.addEdge(p.getId().getLeft(),p.getId().getRight(),true);
        }
        return g.numarComunitati();
    }

    /**
     * @return cea mai sociabila comunnitate=comp conexa cu cel mai lung drum
     */
    public List<Long> ComunitateSociabila(){
        Graph<Long> g=new Graph<Long>();
        for(Prietenie p:repo.findAll()){
            g.addEdge(p.getId().getLeft(),p.getId().getRight(),true);
        }
        return g.longestPathGraph();
    }


    public  List<UserFriendshipDTO> userFriendships(Long id) {
        if(repoU.findOne(id)==null) throw new ServiceException("There is no user with the specified ID \n");
        System.out.println("Prietenii lui "+repoU.findOne(id).getFirstName()+" "+
                repoU.findOne(id).getLastName()+"(ID:"+id+ ") sunt: ");
        List<UserFriendshipDTO> list= StreamSupport.stream(repo.findAll().spliterator(),false)
                .filter(x->(x.getId().getLeft()==id||x.getId().getRight()==id))
                .map(x-> {
                    Utilizator userDr=repoU.findOne(x.getId().getRight());
                    Utilizator userSt=repoU.findOne(x.getId().getLeft());
                    UserFriendshipDTO dto;
                    if(userSt.getId().compareTo(id)==0)
                        dto = new UserFriendshipDTO(userDr.getId(),userDr.getFirstName(),userDr.getLastName(),x.getDate(),userDr.getEmail());
                    else
                        dto = new UserFriendshipDTO(userSt.getId(),userSt.getFirstName(),userSt.getLastName(),x.getDate(),userSt.getEmail());
                    return dto;
                        })
                .collect(Collectors.toList());
        return list;
    }
    public  List<UserFriendshipDTO> userFriendshipsGivenMonth(Long id, Month month) {
        if(repoU.findOne(id)==null) throw new ServiceException("There is no user with the specified ID \n");
        System.out.println("Prietenii lui "+repoU.findOne(id).getFirstName()+" "+
                repoU.findOne(id).getLastName()+"(ID:"+id+ ") din luna ceruta sunt: ");

        List<UserFriendshipDTO> list= StreamSupport.stream(repo.findAll().spliterator(),false)
                .filter(x->(x.getId().getLeft()==id||x.getId().getRight()==id)&&x.getDate().getMonth()==month)
                .map(x-> {
                    Utilizator userDr=repoU.findOne(x.getId().getRight());
                    Utilizator userSt=repoU.findOne(x.getId().getLeft());
                    UserFriendshipDTO dto;
                    if(userSt.getId().compareTo(id)==0)
                        dto = new UserFriendshipDTO(userDr.getId(),userDr.getFirstName(),userDr.getLastName(),x.getDate(),userDr.getEmail());
                    else
                        dto = new UserFriendshipDTO(userSt.getId(),userSt.getFirstName(),userSt.getLastName(),x.getDate(),userSt.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
        return list;
    }


    public List<Prietenie> friendshipsPeriodOfTime(Long id1,LocalDate d1, LocalDate d2){
        List<Prietenie> prietenii = new ArrayList<Prietenie>();
        for (Prietenie p : repo.findAll()) {
            LocalDate ld= p.getDate().toLocalDate();
            if(ld.isAfter(d1)&&ld.isBefore(d2)&&(p.getId().getLeft().equals(id1)||p.getId().getRight().equals(id1))) {
                System.out.println(p.getId());
                prietenii.add(p);
            }
        }
        prietenii.sort(Comparator.comparing(Prietenie::getDate));
        return prietenii;
    }

    //paging

    private int page = -1;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }
    public int getPageSize(){return size;}

    public List<UserFriendshipDTO> getNextFriends(long userID,long lastID) {
        this.page++;
        return getFriendsOnPage(this.page,userID,lastID);
    }

    public List<UserFriendshipDTO> getFriendsOnPage(int page,long userID,long lastID) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Prietenie> studentPage = repo.findAllPage(pageable,userID,lastID);
        return studentPage.getContent()
                .map(x-> {
                    Utilizator userDr=repoU.findOne(x.getId().getRight());
                    Utilizator userSt=repoU.findOne(x.getId().getLeft());
                    UserFriendshipDTO dto;
                    if(userSt.getId().compareTo(userID)==0)
                        dto = new UserFriendshipDTO(userDr.getId(),userDr.getFirstName(),userDr.getLastName(),x.getDate(),userDr.getEmail());
                    else
                        dto = new UserFriendshipDTO(userSt.getId(),userSt.getFirstName(),userSt.getLastName(),x.getDate(),userSt.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());

    }
}
