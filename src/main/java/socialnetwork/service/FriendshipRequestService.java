package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipRequestDBRepository;
import socialnetwork.repository.database.MessagesDBRepository;
import socialnetwork.repository.database.ReplyMessageDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EventChangeEvent;
import socialnetwork.utils.events.MessageChangeEvent;
import socialnetwork.utils.events.RequestsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;
import sun.nio.ch.Util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendshipRequestService implements Observable<RequestsChangeEvent> {
    private Repository<Tuple<Long, Long>, Prietenie> repoFriendship;
    private Repository<Long, Utilizator> repoUser;
    private FriendshipRequestDBRepository repo;

    private List<Observer<RequestsChangeEvent>> observers=new ArrayList<>();

    public FriendshipRequestService(FriendshipRequestDBRepository repo,
                                    Repository<Long, Utilizator> repoUser,
                                    Repository<Tuple<Long, Long>, Prietenie> repoFriendship) {
        this.repo = repo;
        this.repoUser=repoUser;
        this.repoFriendship=repoFriendship;

    }
    @Override
    public void addObserver(Observer<RequestsChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<RequestsChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(RequestsChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    /**
     * Adaug o noua cerere de prietenie
     * @return obiectul de tip prietenie adaugat
     */
    public FriendshipRequest sendRequest(FriendshipRequest p) {
        Long idd1=p.getId().getLeft();
        Long idd2=p.getId().getRight();
        String state=p.getRequest();
        String error = "";
        if(repoUser.findOne(idd2)==null) error += "The user you want to send a friend request to doesn't exist \n";
        if(error.isEmpty()&&state.equals("pending")) {
            int ok = 0;
            UserFriendshipRequestDTO x = null;
            for (UserFriendshipRequestDTO fr : userFriendshipRequest(idd1)) {
                if (fr.getId().equals(idd2)) {
                    ok = 1;
                    x = fr;
                }
            }
            if (ok == 1) error += x.getFirstName() + " " + x.getLastName() + " already sent you a friend request! \n";
        }
        if(error.isEmpty()&&state.equals("pending")){
            int ok=0;
            UserFriendshipRequestDTO x = null;
            for(UserFriendshipRequestDTO fr:userFriendshipRequest(idd2)){
                if(fr.getId().equals(idd1)) {ok=1;x=fr;}
            }
            if(ok==1) error+="You already sent a friend request to "+repoUser.findOne(idd2).getFirstName()+" "+repoUser.findOne(idd2).getLastName()+" \n";
        }
        if(error.isEmpty()&&(state.equals("approved")||state.equals("rejected"))){
            int ok=0;
            for(UserFriendshipRequestDTO fr:userFriendshipRequest(idd1)){
                if(fr.getId().equals(idd2)) ok=1;
            }
            if(ok==0) error+="Friendship request does not exist!\n";
        }
        if(error.isEmpty()) {
            Prietenie a=new Prietenie(idd1,idd2); Prietenie a1=new Prietenie(idd2,idd1);
            if(repoFriendship.findOne(a.getId())!=null||repoFriendship.findOne(a1.getId())!=null)
                error+="You are already friends!\n";
        }


        if (error.isEmpty()) {
            try {
                if(state.equals("pending"))
                {
                    p=new FriendshipRequest(idd1,idd2,state);
                    repo.save(p);
                    notifyObservers(new RequestsChangeEvent(ChangeEventType.ADD, p));

                }
                else {
                    p = new FriendshipRequest(idd2, idd1, state);
                    repo.update(p);
                    notifyObservers(new RequestsChangeEvent(ChangeEventType.UPDATE, p));

                }

                if(state.equals("approved")){
                    Prietenie pr=new Prietenie(idd1,idd2);
                    repoFriendship.save(pr);
                    repo.delete(p.getId());
                }
                if(state.equals("rejected")) repo.delete(p.getId());
                return p;
            } catch(ValidationException e){error += e.getMessage(); }
        }

        if(!error.isEmpty())throw new ServiceException(error);
        return null;
    }

    public  List<UserFriendshipRequestDTO> userFriendshipRequest(Long id){
        if(repoUser.findOne(id)==null) throw new ServiceException("There is no user with the specified ID \n");
        System.out.println(repoUser.findOne(id).getFirstName()+repoUser.findOne(id).getLastName()
                        +"'s friendship requests:  ");

        Map<Long,String> y = new TreeMap<>();
        for(FriendshipRequest fr: repo.findAll())
            if(fr.getId().getRight().equals(id)) {
                y.put(fr.getId().getLeft(), fr.getRequest());
            }

        List<UserFriendshipRequestDTO> list=y.entrySet().stream()
                .filter(x->(x.getValue().equals("pending")))
                .map(x-> {
                    Utilizator userSt=repoUser.findOne(x.getKey());
                    UserFriendshipRequestDTO dto = new UserFriendshipRequestDTO(userSt.getId(),userSt.getFirstName(),
                            userSt.getLastName(), x.getValue(),getFriendshipRequest(userSt.getId(),id).getDate());
                    return dto;
                })
                .collect(Collectors.toList());


        return list;
    }
    public FriendshipRequest getFriendshipRequest(Long id1,Long id2){
        FriendshipRequest p=null;
        for(FriendshipRequest fr: repo.findAll())
            if(fr.getId().getLeft().equals(id1)&&fr.getId().getRight().equals(id2)) {
                p=fr;
            }
        return p;

    }

    public FriendshipRequest removeRequest(Long id1, Long id2){
        String error = "";
        if(repoUser.findOne(id2)==null || repoUser.findOne(id1)==null) error += "The user doesn't exist \n";
        if(error.isEmpty())
        try {
             for (FriendshipRequest fr : repo.findAll())
                if (fr.getId().getLeft().equals(id1) && fr.getId().getRight().equals(id2)) {
                    FriendshipRequest req = new FriendshipRequest(id1, id2, fr.getRequest());
                    repo.delete(new Tuple<>(id1, id2));
                    notifyObservers(new RequestsChangeEvent(ChangeEventType.DELETE,req));
                }
           } catch(ValidationException ex) {error+=ex.getMessage(); }
            if(!error.isEmpty()) throw new ServiceException(error);
            return null;
        }


    public  List<UserFriendshipRequestDTO> requestReceivedByUser(Long id){
        Map<Long,String> y = new TreeMap<>();
        for(FriendshipRequest fr: repo.findAll())
            if(fr.getId().getRight().equals(id)) {
                y.put(fr.getId().getLeft(), fr.getRequest());
            }

        List<UserFriendshipRequestDTO> list=y.entrySet().stream()
                .map(x-> {
                    Utilizator user=repoUser.findOne(x.getKey());
                    UserFriendshipRequestDTO dto = new UserFriendshipRequestDTO(user.getId(),user.getFirstName(),
                            user.getLastName(), x.getValue(),getFriendshipRequest(user.getId(),id).getDate());
                    return dto;
                })
                .collect(Collectors.toList());

        list.sort(Comparator.comparing(UserFriendshipRequestDTO::getDate));
        return list;
    }
    public  List<UserFriendshipRequestDTO> requestsSentByUser(Long id){
        Map<Long,String> y = new TreeMap<>();
        for(FriendshipRequest fr: repo.findAll())
            if(fr.getId().getLeft().equals(id)) {
                y.put(fr.getId().getRight(), fr.getRequest());
            }
        List<Long> list=y.entrySet().stream()
                .filter(x->x.getValue().equals("pending"))
                .map(x-> {
                    Long idd=x.getKey();
                    return idd;
                })
                .collect(Collectors.toList());

        List<UserFriendshipRequestDTO> lista= new ArrayList<>();
        for(Long idR: list){
            Utilizator userR= repoUser.findOne(idR);
            UserFriendshipRequestDTO dto=new UserFriendshipRequestDTO(userR.getId(),userR.getFirstName(),userR.getLastName(),"pending",getFriendshipRequest(id,userR.getId()).getDate());
            lista.add(dto);
        }
        return lista;
    }


    //paging

    private int pageFRSent = -1;
    private int sizeFRSent = 1;

    private int pageFRReceived = -1;
    private int sizeFRReceived = 1;

    private Pageable pageable;

    public int getPageSizeFRSent(){return sizeFRSent;}
    public int getPageSizeFRReceived(){return sizeFRReceived;}

    public void setPageSizeSent(int size) {
        this.sizeFRSent = size;
    }
    public void setPageSizeReceived(int size) {
        this.sizeFRReceived = size;
    }

    public List<UserFriendshipRequestDTO> getNextFRSent(long userID,long lastID) {
        this.pageFRSent++;
        return getFRSentOnPage(this.pageFRSent,userID,lastID);
    }

    public List<UserFriendshipRequestDTO> getFRSentOnPage(int page,long userID,long lastID) {
        this.pageFRSent=page;
        Pageable pageable = new PageableImplementation(page, this.sizeFRSent);
        Page<FriendshipRequest> studentPage = repo.findAllPageFRSent(pageable,userID,lastID);
        return studentPage.getContent()
                .map(x->{
                    Utilizator userR=repoUser.findOne(x.getId().getRight());
                    UserFriendshipRequestDTO dto=new UserFriendshipRequestDTO(userR.getId(),userR.getFirstName(),
                            userR.getLastName(),"pending",x.getDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<UserFriendshipRequestDTO> getNextFRReceived(long userID,long lastID) {
        this.pageFRReceived++;
        return getFRReceivedOnPage(this.pageFRReceived,userID,lastID);
    }

    public List<UserFriendshipRequestDTO> getFRReceivedOnPage(int page,long userID,long lastID) {
        this.pageFRReceived=page;
        Pageable pageable = new PageableImplementation(page, this.sizeFRReceived);
        Page<FriendshipRequest> studentPage = repo.findAllPageFRReceived(pageable,userID,lastID);
        return studentPage.getContent()
                .map(x->{
                    Utilizator userR=repoUser.findOne(x.getId().getLeft());
                    UserFriendshipRequestDTO dto=new UserFriendshipRequestDTO(userR.getId(),userR.getFirstName(),
                            userR.getLastName(),"pending",x.getDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
