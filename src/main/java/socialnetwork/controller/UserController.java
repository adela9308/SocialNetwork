package socialnetwork.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.scene.control.CheckBox;
import socialnetwork.domain.*;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.utils.Constants;
import socialnetwork.utils.PDF;
import socialnetwork.utils.events.Event;
import socialnetwork.utils.observer.Observer;

import java.awt.*;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UserController implements Observer {

    @Override
    public void update(Event event) {
    }

    PageObject page;
    ObservableList<UserFriendshipDTO> modelShowFriends = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUsersAddFriend = FXCollections.observableArrayList();
    ObservableList<UserFriendshipRequestDTO> modelFRReceived = FXCollections.observableArrayList();
    ObservableList<UserFriendshipRequestDTO> modelFRSent = FXCollections.observableArrayList();

    ObservableList<UserDateMessageDTO> modelConversations = FXCollections.observableArrayList();
    ObservableList<ReplyMessage> modelChat = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUSers = FXCollections.observableArrayList();
    UserDateMessageDTO selectedConversationListView = null;
    ObservableList<ReplyMessageDTO> modelConversationsGroup = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUserRaport = FXCollections.observableArrayList();
    Window stage=null;
    ObservableList<EventUserDTO> modelEvents=FXCollections.observableArrayList();
    ObservableList<String> modelUpcomingEvents=FXCollections.observableArrayList();

    public void setService(PageObject page){
        this.page=page;
        tabPane.getSelectionModel().select(0);
        searchTextField.textProperty().addListener(e -> handleType());
       nameLabel.setText( page.getUser().getFirstName()+" "+page.getUser().getLastName() );
        //observer
        page.addMessageObserver(messageChangeEvent -> {
            refreshChat();
            refreshGroupChat();
            refreshUsersConversation();
        });
        page.addFriendshipObserver(friendsChangeEvent -> {
            refreshShowFriends();
            refreshAddFriendSearch();
            refreshReportSearch();
            refreshSendNewMessageSearch();

        });
        page.addRequestsObserver(requestsChangeEvent -> {
            refreshFRReceived();
            refreshFRSent();
        });
        page.addEventObserver(eventChangeEvent -> {
            //refreshEvents();
        });

        notification();
        this.scrollEvent();
        chiceBoxListener();

    }

    @FXML
    private Label nameLabel;
    @FXML
    private ListView<UserFriendshipRequestDTO> friendReqListview;
    @FXML
    private Button logoutButton;
    @FXML
    private Button sendButton;
    @FXML
    private TableView<UserFriendshipDTO> friendsTableView;
    @FXML
    private TableView<EventUserDTO> eventsTableView;
    @FXML
    private TableColumn<UserFriendshipDTO, String> emailColumn;
    @FXML
    private TableColumn<UserFriendshipDTO, String> firstNameColumn;
    @FXML
    private TableColumn<UserFriendshipDTO, String> lastNameColumn;
    @FXML
    private TableColumn<UserFriendshipDTO, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<EventUserDTO,String > titleColumn;
    @FXML
    private TableColumn<EventUserDTO,LocalDateTime > dateEventColumn;
    @FXML
    private TableColumn<EventUserDTO,String > participatingColumn;
    @FXML
    private TableColumn<EventUserDTO, Checkbox> notifyColumn;
    @FXML
    private ListView<Utilizator> searchListView;
    @FXML
    private ListView<UserFriendshipRequestDTO> deleteFRListView;
    @FXML
    private ListView<UserDateMessageDTO> conversationsListView;
    @FXML
    private ListView<ReplyMessage> chatListView;
    @FXML
    private ListView<Utilizator> usersListView;
    @FXML
    private ListView<ReplyMessageDTO> replyAllListView;
    @FXML
    private ListView<Utilizator> raportListView;
    @FXML
    private ListView<String> upcomingEventsListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private TextField searchTextField2;
    @FXML
    private TextField newMessageTextField;
    @FXML
    private TextField replyAllTextField;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextField pathTextField;
    @FXML
    private TextField secondUserTextField;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label chatLabel;
    @FXML
    private DatePicker datePickerEnd;
    @FXML
    private DatePicker datePickerBegin;
    @FXML
    private Spinner<LocalTime> startTimeSpinner;
    @FXML
    private Spinner<LocalTime> endTimeSpinner;
    @FXML
    private DatePicker startDateEvent;
    @FXML
    private DatePicker endDateEvent;
    @FXML
    private TextField eventTitleTextField;
    @FXML
    private TextField eventDescriptionTextField;
    @FXML
    private ChoiceBox<String> reportChoiceBox;
    @FXML
    private Label reportLabel;

    //----------------------------REFRESH----------------------------------------------------------------------------------
    private void refreshShowFriends(){
        int last=page.getPageSizeFriends();
        page.setPageSizeFriends(friendsTableView.getItems().size());
        modelShowFriends.setAll(page.getUsersFriendsPaged(0));
        page.setPageSizeFriends(last);
    }
    private void refreshFRSent(){
        int last=page.getPageSizeFRSent();
        page.setPageSizeFRSent(deleteFRListView.getItems().size());
        modelFRSent.setAll(page.getFriendRequestsSentByUser(0));
        page.setPageSizeFRSent(last);
    }
    private void refreshFRReceived(){
        int last=page.getPageSizeFRReceived();
        page.setPageSizeFRSent(friendReqListview.getItems().size());
        modelFRReceived.setAll(page.getFriendRequestsReceivedByUser(0));
        page.setPageSizeFRReceived(last);
    }
    private void refreshAddFriendSearch(){
        int last=page.getPageSizeUsersNonFriends();
        page.setPageSizeNonFriends(searchListView.getItems().size());
        modelUsersAddFriend.setAll(page.getUsersNonFriendsPaged(searchTextField.getText(),0));
        page.setPageSizeNonFriends(last);
    }
    private void refreshSendNewMessageSearch(){
        int last=page.getPageSizeUsersRestOfUsers();
        page.setPageSizeRestOfUsers(usersListView.getItems().size());
        modelUSers.setAll(page.getRestOfUsersPaged(searchTextField2.getText(),0));
        page.setPageSizeRestOfUsers(last);
    }
    private void refreshReportSearch(){
        int last=page.getPageSizeUsersRestOfUsersReport();
        page.setPageSizeRestOfUsersReport(raportListView.getItems().size());
        modelUserRaport.setAll(page.getRestOfUsersPagedReport(secondUserTextField.getText(),0));
        page.setPageSizeRestOfUsersReport(last);
    }
    private void refreshUsersConversation(){
        page.refreshPageUsersConversation();
        int last=page.getPageSizeUsersConv();
        page.setPageSizeUsersConv(conversationsListView.getItems().size());
        modelConversations.setAll(page.getUsersConversation(0));
        page.setPageSizeUsersConv(last);
     }
    private void refreshChat(){
        if(selectedConversationListView!=null) {
             modelChat.add(page.getLastMessageWithUser(selectedConversationListView.getUser().get(0).getId()));
        }
    }
    private void refreshGroupChat(){
        modelConversationsGroup.add(page.getLastMessageWithGroup());
    }
    private void refreshEvents(){
        int last=page.getPageSizeEvents();
        page.setPageSizeEvents(eventsTableView.getItems().size());
        modelEvents.setAll(page.getAllEventsPaged(0));
        page.setPageSizeEvents(last);
    }

    //---------------------------------------SCROLL EVENTS
    private void scrollEvent(){
        //show friends
        friendsTableView.addEventFilter(ScrollEvent.ANY, event->{
            if(friendsTableView.getItems().size()>0) {
                List<UserFriendshipDTO> friends = new ArrayList<>(modelShowFriends);
                friends.addAll(page.getUsersFriendsPaged(friendsTableView.getItems().get(friendsTableView.getItems().size() - 1).getIdUser()));
                List<UserFriendshipDTO> friendsList = new ArrayList<>(friends);
                modelShowFriends.setAll(friendsList);
            }
        });

        //send new message users
        usersListView.addEventFilter(ScrollEvent.ANY , event -> {
            if(usersListView.getItems().size()>0) {
                Iterable<Utilizator> users = page.getRestOfUsersPaged(searchTextField2.getText().toLowerCase(), usersListView.getItems().get(usersListView.getItems().size() - 1).getId());
                List<Utilizator> usersList = StreamSupport.stream(users.spliterator(), false)
                        .collect(Collectors.toList());
                modelUSers.addAll(usersList);
            }
        });

        //add new friend user
        searchListView.addEventFilter(ScrollEvent.ANY , event -> {
            if(searchListView.getItems().size()>0) {
                Iterable<Utilizator> users = page.getUsersNonFriendsPaged(searchTextField.getText().toLowerCase(), searchListView.getItems().get(searchListView.getItems().size() - 1).getId());
                List<Utilizator> usersList = StreamSupport.stream(users.spliterator(), false)
                        .collect(Collectors.toList());
                modelUsersAddFriend.addAll(usersList);
            }
        });



        //friend requests sent
        deleteFRListView.addEventFilter(ScrollEvent.ANY , event -> {
            if(deleteFRListView.getItems().size()>0) {
                Iterable<UserFriendshipRequestDTO> sent = page.getFriendRequestsSentByUser(deleteFRListView.getItems().get(deleteFRListView.getItems().size() - 1).getId());//esti dusa
                List<UserFriendshipRequestDTO> sentList = StreamSupport.stream(sent.spliterator(), false)
                        .collect(Collectors.toList());
                modelFRSent.addAll(sentList);
            }
        });

        //friend requests received
        friendReqListview.addEventFilter(ScrollEvent.ANY , event -> {
            if(friendReqListview.getItems().size()>0) {
                Iterable<UserFriendshipRequestDTO> recv = page.getFriendRequestsReceivedByUser(friendReqListview.getItems().get(friendReqListview.getItems().size() - 1).getId());
                List<UserFriendshipRequestDTO> recvList = StreamSupport.stream(recv.spliterator(), false)
                        .collect(Collectors.toList());
                modelFRReceived.addAll(recvList);
            }
        });

        //raport users
        raportListView.addEventFilter(ScrollEvent.ANY,event->{
            if(raportListView.getItems().size()>0){
                Iterable<Utilizator> users=page.getRestOfUsersPaged(secondUserTextField.getText().toLowerCase(),raportListView.getItems().get(raportListView.getItems().size()-1).getId());
                List<Utilizator> userList=StreamSupport.stream(users.spliterator(),false)
                        .collect(Collectors.toList());
                modelUserRaport.addAll(userList);
            }
        });

        //users you have conversations with
        conversationsListView.addEventFilter(ScrollEvent.ANY,event->{
            if(conversationsListView.getItems().size()>0){
                System.out.println(conversationsListView.getItems().get(conversationsListView.getItems().size()-1));
                Long u= conversationsListView.getItems().get(conversationsListView.getItems().size()-1).getMessageID();
                List<UserDateMessageDTO> users=page.getUsersConversation(u);
                modelConversations.addAll(users);
            }
        });

        //conversation with a user
        chatListView.addEventFilter(ScrollEvent.ANY , event -> {
            if(chatListView.getItems().size()>0) {
                List<ReplyMessage> messageList = new ArrayList<>(modelChat);
                System.out.println(chatListView.getItems().get(0));
                messageList.addAll(0,page.getConversationWithUser(selectedConversationListView.getUser().get(0).getId(),
                                chatListView.getItems().get(0).getId()));
                modelChat.setAll(messageList);
            }
        });

        //group conversation
        replyAllListView.addEventFilter(ScrollEvent.ANY,event->{
            if(replyAllListView.getItems().size()>0){
                List<ReplyMessageDTO> messageList = new ArrayList<>(modelConversationsGroup);
                messageList.addAll( 0, page.getGroupConversation(replyAllListView.getItems().get(0).getM().getId()));
                modelConversationsGroup.setAll(messageList);
            }
        });


        //events
        eventsTableView.addEventFilter(ScrollEvent.ANY, event->{
            if(eventsTableView.getItems().size()>0) {
                List<EventUserDTO> events = new ArrayList<>(modelEvents);
                events.addAll(page.getAllEventsPaged(eventsTableView.getItems().get(eventsTableView.getItems().size() - 1).getEventID()));
                List<EventUserDTO> eventsList = new ArrayList<>(events);
                modelEvents.setAll(eventsList);

                eventParticipatingCheckBoxEventHandler();
                eventNotificationCheckBoxEventHandler();
            }
        });

    }

    //-----------EVENTS NOTIFICATIONS
    public void notification(){
        LocalDateTime date= LocalDateTime.now().plusDays(7);
        String message="";
        for(EventUserDTO dto:page.getAllEventsDTO(0))
            if(dto.getNotify().isSelected()&&dto.getStart_date().isBefore(date))
                message+=dto.toString();
        if(!message.isEmpty())
             MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Upcoming events",message);
    }

    @FXML
    void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }
//---------------------------------------------------SHOW + DELETE FRIENDS-----------------------------------------------------------------------
    @FXML
    private Button showFriendsButton;
    //show friends model
    private void initModelShowFriends() {
        page.setPageSizeFriends(9);
        showFriendsButton.disableProperty();
        List<UserFriendshipDTO> friends = page.getUsersFriendsPaged(0);
        modelShowFriends.setAll(friends);
    }

    @FXML
    void handleShowFriends(ActionEvent event) {
        friendsTableView.setPlaceholder(new Label("You don't have any friends yet"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<UserFriendshipDTO,String>("Email"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<UserFriendshipDTO, String>("FirstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<UserFriendshipDTO, String>("LastName"));
        dateColumn.setCellFactory(column->new TableCell<UserFriendshipDTO, LocalDateTime>(){
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(Constants.DATE_FORMATTER.format(item));
                }
            }
        });
        dateColumn.setCellValueFactory(new PropertyValueFactory<UserFriendshipDTO,LocalDateTime>("Date"));
        initModelShowFriends();
        friendsTableView.setItems(modelShowFriends);
        tabPane.getSelectionModel().select(1);

    }

    @FXML
    void handleDeleteFriend() {
        UserFriendshipDTO selected = friendsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            page.removeFriend(selected.getIdUser());
            modelShowFriends.remove(selected);
        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
    }

    //------------------------------------------------------------ADD FRIEND----------------------------------------------------------------------
    @FXML
    void handleAddFriend() {
        searchTextField.clear();
        page.setPageSizeNonFriends(11);
        List<Utilizator> users = page.getUsersNonFriendsPaged(null,0);
        modelUsersAddFriend.setAll(users);
        searchListView.setItems(modelUsersAddFriend);
        tabPane.getSelectionModel().select(2);
    }

    //add friend search
    @FXML
    void handleType() {

        String nume = searchTextField.getText();
        List<Utilizator> users = page.getUsersNonFriendsPaged(nume.toLowerCase(),0 );
        if (searchTextField.getText().isEmpty())
            modelUsersAddFriend.clear();
        else {
            modelUsersAddFriend.setAll(users);
            searchListView.setItems(modelUsersAddFriend);
        }
    }

    @FXML
    void handleSendFR(ActionEvent event) {
        Utilizator selected = searchListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long id = page.getUserByName(selected.getFirstName(), selected.getLastName()).getId();
            try {
                FriendshipRequest p = new FriendshipRequest(page.getUser().getId(), id, "pending");
                page.sendFriendRequest(p);
                searchListView.getItems().remove(selected);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
                searchListView.getSelectionModel().clearSelection();
            }
        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
        searchListView.getSelectionModel().clearSelection();

    }

    //-----------------------------------------------FR RECEIVED + SENT----------------------------------------------------------------
    //FR receivved
    @FXML
    void handleFriendReq() {
        page.setPageSizeFRReceived(9);
        List<UserFriendshipRequestDTO> friends = page.getFriendRequestsReceivedByUser(0);
        modelFRReceived.setAll(friends);
        friendReqListview.setItems(modelFRReceived);
        tabPane.getSelectionModel().select(3);
    }

    //accept FR
    @FXML
    void handleAccept() {
        UserFriendshipRequestDTO selected = friendReqListview.getSelectionModel().getSelectedItem();
        ObservableList<UserFriendshipRequestDTO> friendReq = friendReqListview.getItems();
        if (selected != null) {
            try {
                FriendshipRequest p = new FriendshipRequest(page.getUser().getId(), selected.getId(), "approved");
                page.sendFriendRequest(p);
               //friendReqListview.getItems().remove(selected);
                List<UserFriendshipRequestDTO> friends = page.getFriendRequestsReceivedByUser(0);
                modelFRReceived.clear();
                modelFRReceived.setAll(friends);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
    }

    //reject FR
    @FXML
    void handleReject() {
        UserFriendshipRequestDTO selected = friendReqListview.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FriendshipRequest p = new FriendshipRequest(page.getUser().getId(), selected.getId(), "rejected");
                page.sendFriendRequest(p);
                List<UserFriendshipRequestDTO> friends = page.getFriendRequestsReceivedByUser(0);
                modelFRReceived.clear();
                modelFRReceived.setAll(friends);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
    }

    @FXML
    void handleDeleteRequest() {
        page.setPageSizeFRSent(9);
        List<UserFriendshipRequestDTO> reqSent =page.getFriendRequestsSentByUser(0);
        modelFRSent.setAll(reqSent);
        deleteFRListView.setItems(modelFRSent);
        tabPane.getSelectionModel().select(4);
    }

    @FXML
    void handleDeleteFR() {
        UserFriendshipRequestDTO selected = deleteFRListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            page.deleteRequest(selected.getId());
            deleteFRListView.getItems().remove(selected);
        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
    }
//-----------------------------------------------------MESSAGES-----------------------------------------------------------------------------------
    @FXML
    void handleMessage() {
        sendButton.setText("Send");
        page.setPageSizeUsersConv(7);
        page.refreshPageUsersConversation();
        List<UserDateMessageDTO> users = page.getUsersConversation(Long.MAX_VALUE);

        chatListView.getItems().clear();
        modelChat.clear();
        modelConversations.setAll(users);
        conversationsListView.setItems(modelConversations);

        tabPane.getSelectionModel().select(5);
    }

    //conversation with a particular user
    @FXML
    void handleChatListView() {
        page.setPageSizeMessages(7);
        UserDateMessageDTO selected = conversationsListView.getSelectionModel().getSelectedItem();
        if (selected != null) selectedConversationListView = selected;
        if (selectedConversationListView != null) {
            modelChat.clear();
            chatLabel.setText("Chat with " + selectedConversationListView.getUser().get(0).getFirstName() + " " +
                    selectedConversationListView.getUser().get(0).getLastName());

            List<ReplyMessage> conv = page.getConversationWithUser(selectedConversationListView.getUser().get(0).getId(),Long.MAX_VALUE);
            modelChat.addAll(conv);
            chatListView.setItems(modelChat);

        } else MessageAlert.showErrorMessage(null, "You haven't selected anything");
        conversationsListView.getSelectionModel().clearSelection();
    }
    @FXML
    void handleReplyMessageChangeButtonListView(){
        sendButton.setText("Reply");
    }
    @FXML
    void handleSendMessage() {
        sendButton.setText("Send");
        if (selectedConversationListView != null) {
            String message = messageTextField.getText();
            if (message.length() == 0) return;
            UserDateMessageDTO selected = selectedConversationListView;
            List<Utilizator> to = new ArrayList<>();
            to.add(selected.getUser().get(0));
            ReplyMessage selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat == null) {
                Message mess = new Message(page.getUser(), to, message);
                try {
                    page.sendMessage(mess);
                } catch (ServiceException e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
            }
            else{
                ReplyMessage r = new ReplyMessage(page.getUser(), to, message, selectedChat);
                try {
                    page.replyMessage(r);
                } catch (ServiceException e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                    chatListView.getSelectionModel().clearSelection();
                    return;
                }
                chatListView.getSelectionModel().clearSelection();
            }
        } else MessageAlert.showErrorMessage(null, "You haven't selected any user to send a message to");
        messageTextField.clear();
    }

    @FXML
    void handleSendNewMessage() {
        searchTextField2.clear();
        page.setPageSizeRestOfUsers(12);
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<Utilizator> users=page.getRestOfUsersPaged(null,0);
        modelUSers.setAll(users);
        usersListView.setItems(modelUSers);
        tabPane.getSelectionModel().select(6);
    }

    //handle search send new message
    @FXML
    void handleTypeUsers(){
        String nume = searchTextField2.getText();
        Iterable<Utilizator> users = page.getRestOfUsersPaged(nume.toLowerCase(),0);
        if(nume.isEmpty()) modelUSers.clear();
        else {
            modelUSers.setAll(StreamSupport.stream(users.spliterator(), false)
                    .collect(Collectors.toList()));
            usersListView.setItems(modelUSers);
        }
    }

    @FXML
    void handleSendNewM() {
        List<Utilizator> selected = usersListView.getSelectionModel().getSelectedItems();
        if (selected.size() != 0) {
            String message = newMessageTextField.getText();
            if (message.length() == 0) return;
            Message mess = new Message(page.getUser(), selected, message);
            try {
                page.sendMessage(mess);
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
            newMessageTextField.clear();

        } else MessageAlert.showErrorMessage(null, "You haven't selected any user to send a message to");
        newMessageTextField.clear();
        usersListView.getSelectionModel().clearSelection();
    }

//-------------------------------------------------GROUP MESSAGES-------------------------------------------------------------------------
    @FXML
    void handleReplyMessage() {
        page.setPageSizeGroupConversation(3);
        List<ReplyMessageDTO> users =page.getGroupConversation(Long.MAX_VALUE);
        modelConversationsGroup.setAll(users);
        replyAllListView.setItems(modelConversationsGroup);
        tabPane.getSelectionModel().select(7);
    }

    @FXML
    void handleReplyAllButton() {
        String message = replyAllTextField.getText();
        if (message.isEmpty()) return;
        ReplyMessageDTO mess = replyAllListView.getSelectionModel().getSelectedItem();
        if (mess == null) {
            MessageAlert.showErrorMessage(null, "You haven't selected any message to send a reply to");
            return;
        }
        Message m = mess.getM();
        ArrayList<Utilizator> list = new ArrayList<>();
        for (Utilizator ut : m.getTo())
            if (!ut.getId().equals(page.getUser().getId())) list.add(ut);
        if (!m.getFrom().getId().equals(page.getUser().getId())) list.add(m.getFrom());
        ReplyMessage r = new ReplyMessage(page.getUser(), list, message, m);
        try {
            page.replyMessage(r);
            List<ReplyMessageDTO> ev = page.getGroupConversation(Long.MAX_VALUE);
            replyAllListView.setItems(modelConversationsGroup);

        } catch (ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        replyAllTextField.clear();
        replyAllListView.getSelectionModel().clearSelection();

    }

    //------------------------------------REPORTS--------------------------------------------------------
    @FXML
    void handleRaportsButton(){
        secondUserTextField.clear();
        page.setPageSizeRestOfUsers(10);

        reportLabel.setText("Select the report");
        reportChoiceBox.getSelectionModel().clearSelection();

        pathTextField.setText("D:\\UNI_UBB\\MAP\\LABORATOARE\\raport.pdf");
        List<Utilizator> users = page.getRestOfUsersPaged(null,0);
        modelUserRaport.setAll(users);
        raportListView.setItems(modelUserRaport);
        tabPane.getSelectionModel().select(8);
    }
    @FXML
    void handleTypeRaportListView(){
        String nume = secondUserTextField.getText();
        Iterable<Utilizator> users =page.getRestOfUsersPaged(nume.toLowerCase(),0);
        if(nume.isEmpty()) modelUserRaport.clear();
        else {
            modelUserRaport.setAll(StreamSupport.stream(users.spliterator(), false)
                    .collect(Collectors.toList()));
            raportListView.setItems(modelUserRaport);
        }
    }
    void handleRaport1Button(){
        LocalDate d1=datePickerBegin.getValue();
        LocalDate d2=datePickerEnd.getValue();
        String path=pathTextField.getText();
        if(d1==null||d2==null){
            MessageAlert.showErrorMessage(null, "Please select the start/end date!");
            return;
        }
        PDF pdf=new PDF(page.getMessageService(),page.getFriendshipService(),page.getUserService());
        pdf.raport1(path,page.getUser(),d1,d2);
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(path);
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
        datePickerBegin.getEditor().clear();
        datePickerEnd.getEditor().clear();
        secondUserTextField.clear();
        pathTextField.setText("D:\\UNI_UBB\\MAP\\LABORATOARE\\raport.pdf");
        raportListView.getSelectionModel().clearSelection();
    }


    void handleRaport2Button(){
        LocalDate d1=datePickerBegin.getValue();
        LocalDate d2=datePickerEnd.getValue();
        Utilizator u2=raportListView.getSelectionModel().getSelectedItem();
        String path=pathTextField.getText();
        if(d1==null||d2==null){
            MessageAlert.showErrorMessage(null, "Please select the start/end date!");
            return;
        }
        if(u2==null){
            MessageAlert.showErrorMessage(null, "You must select a user!");
            return;
        }

//        List<String> list=new ArrayList<>();
//        for(ReplyMessage r:page.getMessagesFromUserPeriodOfTime( u2.getId(),d1,d2)){
//            list.add(r.getDate().format(Constants.DATE_TIME_FORMATTER)+"\n"+ r.getMessage()+" \n \n");
//        }
        PDF pdf=new PDF(page.getMessageService(),page.getFriendshipService(),page.getUserService());
        pdf.raport2(path,page.getUser(), u2,d1,d2);
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(path);
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
        datePickerBegin.getEditor().clear();
        datePickerEnd.getEditor().clear();
        secondUserTextField.clear();
        pathTextField.setText("D:\\UNI_UBB\\MAP\\LABORATOARE\\raport.pdf");
        raportListView.getSelectionModel().clearSelection();
    }
    @FXML
    void handlePathButton(){
        DirectoryChooser directoryChooser=new DirectoryChooser();
        File selected=directoryChooser.showDialog(stage);
        if(selected!=null)
            pathTextField.setText(selected.getAbsolutePath()+"raport.pdf");

    }
    void chiceBoxListener() {
        String r1="Received messages from a selected user";
        String r2="Received messages and friend requests";
        reportChoiceBox.getItems().addAll(r1,r2);

        //reportChoiceBox.setValue(r1);
        reportChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number s1,Number s2) {
                if(!reportChoiceBox.getSelectionModel().isEmpty()){
                    reportLabel.setText("");
                    if(reportChoiceBox.getItems().get((Integer) s2).equals(r1)) handleRaport2Button();
                    else handleRaport1Button();
                }

            }
        });
    }

    //--------------------------------EVENTS-----------------------------------------------

    @FXML
    void handleShowAllEvents(){
        initUpcomingEvents();
        tabPane.getSelectionModel().select(9);
    }
    @FXML
    void handleAddNewEvent(){
        tabPane.getSelectionModel().select(10);
    }

    void initUpcomingEvents(){
        LocalDateTime date= LocalDateTime.now().plusDays(7);
        String message="";
        for(EventUserDTO dto:page.getAllEventsDTO(0))
            if(dto.getParticipating().isSelected()&&dto.getStart_date().isBefore(date))
                message+="The event <"+dto.getTitle()+"> will take place soon. \n Start date: "+dto.getStart_date().format(Constants.DATE_TIME_FORMATTER_NICE) +"\n";
        modelUpcomingEvents.clear();
        upcomingEventsListView.getItems().clear();
            if(!message.isEmpty())
              modelUpcomingEvents.setAll(message);
        upcomingEventsListView.setItems(modelUpcomingEvents);
    }
    @FXML
    void handleEvents(){
        initUpcomingEvents();
        titleColumn.setCellValueFactory(new PropertyValueFactory<EventUserDTO,String>("title"));
        participatingColumn.setCellValueFactory(new PropertyValueFactory<EventUserDTO,String>("participating"));
        notifyColumn.setCellValueFactory(new PropertyValueFactory<EventUserDTO,Checkbox>("notify"));
        dateEventColumn.setCellFactory(column->new TableCell<EventUserDTO,LocalDateTime>(){
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(Constants.DATE_TIME_FORMATTER.format(item));
                }
            }
        });
        dateEventColumn.setCellValueFactory(new PropertyValueFactory<EventUserDTO,LocalDateTime>("start_date"));

        page.setPageSizeEvents(7);
        Iterable<EventUserDTO> events = page.getAllEventsPaged(0);
        List<EventUserDTO> list = StreamSupport.stream(events.spliterator(), false)
                .collect(Collectors.toList());
        modelEvents.setAll(list);

        eventsTableView.setItems(modelEvents);

        eventParticipatingCheckBoxEventHandler();
        eventNotificationCheckBoxEventHandler();

        initTimeSpinner(startTimeSpinner);
        initTimeSpinner(endTimeSpinner);
        tabPane.getSelectionModel().select(9);

    }
    private void initTimeSpinner(Spinner<LocalTime> spinner){
        SpinnerValueFactory<LocalTime> value = new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new LocalTimeStringConverter(FormatStyle.SHORT));
                setValue(LocalTime.now());
            }
            @Override
            public void decrement(int steps) {
                LocalTime time = getValue();
                setValue(time.minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                LocalTime time =  getValue();
                setValue(time.plusMinutes(steps));
            }
        };

        spinner.setValueFactory(value);
        spinner.setEditable(false);
    }

    @FXML
    void handleAddEvent(){
        String title=eventTitleTextField.getText();
        String description=eventDescriptionTextField.getText();
        if(startDateEvent.getValue()==null || endDateEvent.getValue()==null)
        {MessageAlert.showErrorMessage(null,"Please select the start/end date of the event!");
            return;}
        if(startTimeSpinner.getValue()==null||endTimeSpinner.getValue()==null)
        {
            MessageAlert.showErrorMessage(null,"Please select the start/end time of the evet!");
            return;
        }
        LocalDateTime start,end;
        start=LocalDateTime.of(startDateEvent.getValue(),startTimeSpinner.getValue());
        end=LocalDateTime.of(endDateEvent.getValue(),endTimeSpinner.getValue());
        socialnetwork.domain.Event event=new socialnetwork.domain.Event(title,description,start,end,page.getUser());
        try{
            page.addEvent(event);
            EventUserDTO dto=new EventUserDTO(title,start,event.getId(),0L,0L);
            List<EventUserDTO> ev = page.getAllEventsDTO(0);
            modelEvents.clear();
            modelEvents.setAll(ev);

        }catch (ServiceException e){
            eventTitleTextField.clear();eventDescriptionTextField.clear();startDateEvent.getEditor().clear();
            endDateEvent.getEditor().clear();startTimeSpinner.getEditor().clear();endTimeSpinner.getEditor().clear();
            MessageAlert.showErrorMessage(null,e.getMessage());}
        eventTitleTextField.clear();eventDescriptionTextField.clear();startDateEvent.getEditor().clear();
        endDateEvent.getEditor().clear();startTimeSpinner.getEditor().clear();endTimeSpinner.getEditor().clear();
    }

    @FXML
    void handleShowEvent(){
       socialnetwork.domain.Event selected= page.getEventByID(eventsTableView.getSelectionModel().getSelectedItem().getEventID());
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Event INFO",selected.getInfoEvent());
    }
    @FXML
    void handleDeleteEventButton(){
        socialnetwork.domain.Event selected= page.getEventByID(eventsTableView.getSelectionModel().getSelectedItem().getEventID());
        eventsTableView.getSelectionModel().clearSelection();
        if(page.getUser().getId()!=selected.getOwner().getId()) {
            MessageAlert.showErrorMessage(null, "You cannot delete an event if you aren't the one who created it!");
            return;
        }
        try{
            page.deleteEvent(selected.getId());
            List<EventUserDTO> ev = page.getAllEventsDTO(0);
            modelEvents.clear();
            modelEvents.setAll(ev);
        }catch(ServiceException e){ MessageAlert.showErrorMessage(null,e.getMessage());}
    }



    void eventParticipatingCheckBoxEventHandler() {
        System.out.println("intrat");
        eventsTableView.getItems().stream()
                .forEach(x->{
                    EventHandler eh = new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (event.getSource() instanceof CheckBox) {
                                if(x.getParticipating().isSelected()) {
                                    page.participateToEvent(x.getEventID());
                                    page.notifyEvent(x.getEventID());
                                }
                                else {
                                    page.stopParticipatingToEvent(x.getEventID());
                                    page.stopNotifyingEvent(x.getEventID());
                                }
                                initUpcomingEvents();
                                x.getParticipating().setSelected(x.getParticipating().isSelected());
                                x.getNotify().setSelected(x.getParticipating().isSelected());

                            }
                        }
                    };

                    x.getParticipating().setOnAction(eh);
                });
    }

    void eventNotificationCheckBoxEventHandler() {
        eventsTableView.getItems().stream()
                .forEach(x->{
                    EventHandler eh = new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (event.getSource() instanceof CheckBox) {
                                if(x.getNotify().isSelected()) {
                                    page.notifyEvent(x.getEventID());
                                }
                                else {
                                    page.stopNotifyingEvent(x.getEventID());
                                }
                                x.getNotify().setSelected(x.getNotify().isSelected());

                            }
                        }
                    };

                    x.getNotify().setOnAction(eh);
                });
    }
}
