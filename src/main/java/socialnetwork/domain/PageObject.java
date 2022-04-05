package socialnetwork.domain;

import javafx.scene.control.CheckBox;
import socialnetwork.repository.database.EventDBRepository;
import socialnetwork.service.*;
import socialnetwork.utils.events.EventChangeEvent;
import socialnetwork.utils.events.FriendsChangeEvent;
import socialnetwork.utils.events.MessageChangeEvent;
import socialnetwork.utils.events.RequestsChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.util.List;

public class PageObject {
    private UtilizatorService userService;
    private MessageService messageService;
    private PrietenieService friendshipService;
    private FriendshipRequestService requestService;
    private EventService eventService;
    private Utilizator user;


    public PageObject(UtilizatorService userService, MessageService messageService,
                      PrietenieService friendshipService, FriendshipRequestService requestService,
                      Utilizator user, EventService eventService) {
        this.userService = userService;
        this.messageService = messageService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.eventService = eventService;
        this.user = user;
    }

    //--------------------------------------USER--------------------------------------------------------

    public Utilizator getUser() {
        return user;
    }

    public List<Utilizator> getUsersNonFriendsPaged(String name, long position) {
        return userService.getNextUsersNonFriendsFiltered(user.getId(), name, position);
    }

    public List<Utilizator> getRestOfUsersPaged(String name, long position) {
        return userService.getNextUsersNonFriends(user.getId(), name, position);
    }
    public List<Utilizator> getRestOfUsersPagedReport(String name, long position) {
        return userService.getNextUsersNonFriendsReport(user.getId(), name, position);
    }

    public Utilizator getUserByName(String firstName, String lastName) {
        return userService.getUtilizatorName(firstName, lastName);
    }

    public void setPageSizeRestOfUsers(int size) {
        userService.setPageSize(size);
    }

    public void setPageSizeNonFriends(int size) {
        userService.setPageSizeFiltered(size);
    }
    public void setPageSizeRestOfUsersReport(int size) {
        userService.setPageSizeReport(size);
    }

    public int getPageSizeUsersNonFriends(){
        return userService.getPageSizeFiltered();
    }

    public int getPageSizeUsersRestOfUsers(){
        return userService.getPageSize();
    }
    public int getPageSizeUsersRestOfUsersReport(){
        return userService.getPageSizeReport();
    }
    //-----------------------------------FRIENDSHIPS--------------------------------------------------

    public List<UserFriendshipDTO> getUsersFriendsPaged(long position) {
        return friendshipService.getNextFriends(user.getId(), position);
    }

    public void setPageSizeFriends(int size) {
        friendshipService.setPageSize(size);
    }

    public void addFriendshipObserver(Observer<FriendsChangeEvent> friendsEvent) {
        friendshipService.addObserver(friendsEvent);
    }

    public Prietenie removeFriend(long otherID) {
        return friendshipService.removePrieten(user.getId(), otherID);
    }

    public int getPageSizeFriends(){
        return friendshipService.getPageSize();
    }

    //------------------------------------MESSAGES-----------------------------------------------------

    public void addMessageObserver(Observer<MessageChangeEvent> messageEvent) {
        messageService.addObserver(messageEvent);
    }

    public List<ReplyMessage> getConversationWithUser(long userID, long position) {
        return messageService.getNextMessages(user.getId(), userID, position);
    }

    public List<UserDateMessageDTO> getUsersConversation(long position) {
        return messageService.getNextUsersConv(user.getId(), position);
        //de modificat
        //return messageService.getUsersConversations(user.getId());
    }
    public ReplyMessage getLastMessageWithUser(Long userID){
        return messageService.getLastMessageWithUser(user.getId(),userID);
    }
    public ReplyMessageDTO getLastMessageWithGroup(){
        return messageService.getLastMessageWithGroup(user.getId());
    }
    public void refreshPageUsersConversation(){
        messageService.refreshPageUsersConv();
    }

    public void setPageSizeMessages(int size) {
        messageService.setPageSize(size);
    }
    public int getPageSizeMessages() {
        return messageService.getPageSizeMessages();
    }

    public void setPageSizeUsersConv(int size) {
        messageService.setPageUsersConvSize(size);
    }
    public int getPageSizeUsersConv() {
        return messageService.getPageSizeUsersConv();
    }
    public void setPageSizeGroupConversation(int size){
        messageService.setPageGroupConvSize(size);
    }
    public int getPageSizeGroupConversation(){
        return messageService.getPageGroupConvSize();
    }
    public void sendMessage(Message message) {
        messageService.sendMessage(message);
    }

    public void replyMessage(ReplyMessage reply) {
        messageService.replyMessage(reply);
    }

    public List<ReplyMessageDTO> getGroupConversation(long position) {
        //de modificat
        return messageService.getNextGroupConv(user.getId(),position);
        //return messageService.getConversationsGroup(user.getId());
    }

    public Iterable<ReplyMessage> getMessagesFromUserPeriodOfTime(long userID2, LocalDate date1, LocalDate date2) {
        return messageService.messagesFromUserPeriodOfTime(user.getId(), userID2, date1, date2);
    }


    //--------------------------------FRIENDSHIPS REQUESTS------------------------------------------

    public void addRequestsObserver(Observer<RequestsChangeEvent> requestsEvent) {
        requestService.addObserver(requestsEvent);
    }

    public List<UserFriendshipRequestDTO> getFriendRequestsSentByUser(long position) {
        return requestService.getNextFRSent(user.getId(), position);
    }

    public List<UserFriendshipRequestDTO> getFriendRequestsReceivedByUser(long position) {
        return requestService.getNextFRReceived(user.getId(), position);
    }

    public void sendFriendRequest(FriendshipRequest request) {
        requestService.sendRequest(request);
    }

    public void setPageSizeFRReceived(int size) {
        requestService.setPageSizeReceived(size);
    }

    public void setPageSizeFRSent(int size) {
        requestService.setPageSizeSent(size);
    }

    public void deleteRequest(long userID2) {
        requestService.removeRequest(user.getId(), userID2);
    }

    public UtilizatorService getUserService() {
        return userService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public PrietenieService getFriendshipService() {
        return friendshipService;
    }

    public FriendshipRequestService getRequestService() {
        return requestService;
    }

    public int getPageSizeFRSent(){
        return requestService.getPageSizeFRSent();
    }
    public int getPageSizeFRReceived(){
        return requestService.getPageSizeFRReceived();
    }

    //-----------------------------------EVENTS-------------------------------------------------

    public void addEvent(Event event) {
        eventService.addEvent(event);
    }

    public void deleteEvent(long eventID) {
        eventService.deleteEvent(eventID);
    }

    public List<Utilizator> getAllParticipantToEvent(long eventID) {
        return eventService.getAllParticipantsToEvent(eventID);
    }

    public List<EventUserDTO> getAllEventsDTO(int size) {
        return eventService.getAllEventsDTO(user.getId());
    }

    public void setPageSizeEvents(int size) {
        eventService.setPageSize(size);
    }
    public int getPageSizeEvents() {
        return eventService.getPageSize();
    }

    public void participateToEvent(long eventID) {
        eventService.participateToEvent(user.getId(), eventID);
    }

    public void stopParticipatingToEvent(long eventID) {
        eventService.stopParticipatingToEvent(user.getId(), eventID);
    }

    public List<Long> getAllEventsParticipatingTo() {
        return eventService.getAllEventsParticipatingTo(user.getId());
    }

    public void notifyEvent(long eventID){
        eventService.notifyEvent(user.getId(),eventID);
    }

    public void stopNotifyingEvent(long eventID) {
        eventService.stopNotifyingEvent(user.getId(),eventID);
    }

    public List<Long> getAllNotifyingEvents() {
        return eventService.getAllNotifyingEvents(user.getId());
    }

    public List<EventUserDTO> getAllEventsPaged(long position){
        return eventService.getNextEvents(position,user.getId());
    }
    public Event getEventByID(long eventID){
        return eventService.getEventByID(eventID);
    }

    public List<CheckBox> getAllParticipatingCheckBox(){
        return eventService.getAllParticipatingCheckBox(user.getId());
    }
    public void addEventObserver(Observer<EventChangeEvent> eventEvent) {
        eventService.addObserver(eventEvent);
    }


}