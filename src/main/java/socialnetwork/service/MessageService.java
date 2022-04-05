package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessagesDBRepository;
import socialnetwork.repository.database.ReplyMessageDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.MessageChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<MessageChangeEvent> {
    private Repository<Long, Utilizator> repoUsers;
    private MessagesDBRepository repoMessage;
    private ReplyMessageDBRepository repoReplyMessage;
    private List<Long> usersConversation;

    public MessageService(MessagesDBRepository repoMessage,ReplyMessageDBRepository repoReplyMessage,Repository<Long, Utilizator> repoUsers) {
        this.repoMessage = repoMessage;
        this.repoReplyMessage=repoReplyMessage;
        this.repoUsers=repoUsers;
    }
    private List<Observer<MessageChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }



    public void sendMessage(Message message){
        String error = "";
       /* for(Utilizator u:message.getTo()) {
            if (repoUser.findOne(u.getId()) == null) {error += "ID doesn't exist \n";break;}
        }
        if(error.isEmpty())*/
        try{
            repoMessage.save(message);
            ReplyMessage r=new ReplyMessage(message.getFrom(),message.getTo(), message.getMessage(), null);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD,r));
        }catch(ValidationException e) {error+=e.getMessage();}
        if(!error.isEmpty()) throw new ServiceException(error);
    }


    public void replyMessage(ReplyMessage message){
        String error = "";
        if(repoMessage.findOne(message.getMessageObject().getId())==null) error+="The message you want to respond does not exist\n";
        if(error.isEmpty())
        try{
            repoReplyMessage.save(message);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD,message));

        }catch(ValidationException e) {error+=e.getMessage();}
        if(!error.isEmpty()) throw new ServiceException(error);
    }

    public Iterable<Message> getAll(){
        return repoMessage.findAll();
    }

    public Message getMessage(Long id){
        for(Message u:repoMessage.findAll())
            if(u.getId()==id) return u;
        return null;
    }

    public Iterable<ReplyMessage> conversation(Long id1,Long id2){
        ArrayList<ReplyMessage> conv = new ArrayList<ReplyMessage>();
        for (ReplyMessage mess : repoReplyMessage.findAll()) {
            for (Utilizator user : mess.getTo()) {
                if ((id1.equals(mess.getFrom().getId()) && id2.equals(user.getId()))
                        || (id2.equals(mess.getFrom().getId()) && id1.equals(user.getId()))) {
                    conv.add(mess);
                    break;
                }
            }
        }
        conv.sort(Comparator.comparing(Message::getDate));
        return conv;
    }
    public List<ReplyMessage> messagesFromUserPeriodOfTime(Long id_to, Long id_from, LocalDate d1, LocalDate d2){
        return repoReplyMessage.getConvoWithUserBetweenDates(id_to,id_from,d1,d2);
    }
    public Iterable<ReplyMessage> messagesPeriodOfTime(Long id1, LocalDate d1, LocalDate d2){
        ArrayList<ReplyMessage> conv = new ArrayList<ReplyMessage>();
        for (ReplyMessage mess : repoReplyMessage.findAll()) {
            LocalDate ld= mess.getDate().toLocalDate();
            if(ld.isAfter(d1)&&ld.isBefore(d2))
                for (Utilizator user : mess.getTo()) {
                    if (id1.equals(user.getId())) {
                        conv.add(mess);
                        break;
                    }
                }
        }
        conv.sort(Comparator.comparing(Message::getDate));
        return conv;
    }
    public Iterable<ReplyMessageDTO> getConversationsGroup(Long id){
        Utilizator u=repoUsers.findOne(id);
        List<ReplyMessageDTO> list=new ArrayList<>();
        for(ReplyMessage mess:repoReplyMessage.findAll()) {
            if ((mess.getFrom().equals(u) || mess.getTo().contains(u))&&mess.getTo().size()>1){
                ReplyMessageDTO m=new ReplyMessageDTO(mess);
                list.add(m);
            }
        }
        list.sort(Comparator.comparing(ReplyMessageDTO::getDate,Comparator.reverseOrder()));
        return list;
    }

    public Iterable<UserDateMessageDTO> getUsersConversations(Long id){

        //Set<UserDateMessageDTO> users = new HashSet<UserDateMessageDTO>();
        Map<Long,UserDateMessageDTO> users=new TreeMap<>();
        for (ReplyMessage mess : repoReplyMessage.findAll()) {
            for (Utilizator user : mess.getTo()) {
                if(id.equals(mess.getFrom().getId())){
                    List<Utilizator> list=new ArrayList<>();list.add(user);
                    UserDateMessageDTO dto = new UserDateMessageDTO(list, mess.getDate(),mess.getId());
                    users.put(user.getId(),dto);
                }
                if(id.equals(user.getId())){
                    List<Utilizator> list=new ArrayList<>();list.add(mess.getFrom());
                    UserDateMessageDTO dto = new UserDateMessageDTO(list, mess.getDate(),mess.getId());
                    users.put(mess.getFrom().getId(),dto);
                }

            }
        }

        List<UserDateMessageDTO> sortedList=new ArrayList<>();
        users.values().forEach(tab -> sortedList.add(tab));
        //Collections.sort(sortedList);
        sortedList.sort(Comparator.comparing(UserDateMessageDTO::getDate,Comparator.reverseOrder()));
        return sortedList;

    }
    public ReplyMessage getLastMessage(Long id1,Long id2){
        final ReplyMessage[] r = {null};
        conversation(id1,id2).forEach(x-> r[0] =x);
        return r[0];
    }

    //paging

    private int page = -1;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }
    public int getPageSizeMessages(){return size;}
    public List<ReplyMessage> getNextMessages(long userID1,long userID2,long lastID) {
        this.page++;
        return getMessagesOnPage(this.page,userID1,userID2,lastID);
    }

    public List<ReplyMessage> getMessagesOnPage(int page,long userID1,long userID2,long lastID) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<ReplyMessage> studentPage = repoReplyMessage.findAllPage(pageable,userID1,userID2,lastID);
        return studentPage.getContent().collect(Collectors.toList());
    }

    //users you have convs with
    private int pageUsersConv = -1;
    private int sizeUsersConv = 1;

    public void setPageUsersConvSize(int size) {
        this.sizeUsersConv = size;
    }
    public int getPageSizeUsersConv() {
        return sizeUsersConv;
    }
    public void refreshPageUsersConv(){this.pageUsersConv=-1;}

    public List<UserDateMessageDTO> getNextUsersConv(long userID,long lastID) {
        this.pageUsersConv++;
        return getUsersConvOnPage(this.pageUsersConv,userID,lastID);
    }

    public List<UserDateMessageDTO> getUsersConvOnPage(int page,long userID,long lastID) {
        this.pageUsersConv=page;
        Pageable pageable = new PageableImplementation(page, this.sizeUsersConv);
        //Page<ReplyMessage> studentPage = repoReplyMessage.findAllPageUsersConversation(pageable,userID,lastID);

        Page<ReplyMessage> studentPage = repoReplyMessage.findAllPageUsersConversation(pageable,userID);
        return studentPage.getContent()
                .map(x->{
                    List<Utilizator> list=new ArrayList<Utilizator>();
                    list.add(repoUsers.findOne(x.getFrom().getId()));
                    UserDateMessageDTO dto=new UserDateMessageDTO(list,x.getDate(),x.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //group conv
    private int pageGroupConv = -1;
    private int sizeGroupConv = 1;

    public void setPageGroupConvSize(int size) {
        this.sizeGroupConv = size;
    }
    public int getPageGroupConvSize() {
        return sizeGroupConv;
    }
    public List<ReplyMessageDTO> getNextGroupConv(long userID,long lastID) {
        this.pageGroupConv++;
        return getGroupConvOnPage(this.pageUsersConv,userID,lastID);
    }

    public List<ReplyMessageDTO> getGroupConvOnPage(int page,long userID,long lastID) {
        this.pageGroupConv=page;
        Pageable pageable = new PageableImplementation(page, this.sizeGroupConv);
        Page<ReplyMessageDTO> studentPage = repoReplyMessage.findAllGroupConversationPaged(pageable,userID,lastID);
        return studentPage.getContent()
                .collect(Collectors.toList());
    }

    public ReplyMessage getLastMessageWithUser(Long userID1,Long userID2){
        return repoReplyMessage.getLastMessageWithUser(userID1,userID2);
    }
    public ReplyMessageDTO getLastMessageWithGroup(Long userID1){
        return repoReplyMessage.getLastMessageWithGroup(userID1);
    }
}
