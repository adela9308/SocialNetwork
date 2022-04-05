package socialnetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainFX extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        UserDBRepository userDBRepository=new UserDBRepository("users",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new UtilizatorValidator());

        FriendshipsDBRepository friendDBRepository=new FriendshipsDBRepository("friendships",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new PrietenieValidator());
        FriendshipRequestDBRepository friendshipRequestDBRepository=new FriendshipRequestDBRepository("friendship_requests",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new FriendshipRequestValidator());

        MessagesDBRepository messageDBRepository=new MessagesDBRepository("messages",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new MessageValidator(), (UserDBRepository) userDBRepository);


        ReplyMessageDBRepository replyMessageDBRepository=new ReplyMessageDBRepository("reply_message",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new ReplyMessageValidator(),
                (UserDBRepository) userDBRepository,(MessagesDBRepository) messageDBRepository);

        EventDBRepository eventDBRepository=new EventDBRepository("events",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new EventValidator(),userDBRepository);

        UtilizatorService user_serv=new UtilizatorService(userDBRepository,friendDBRepository);
        PrietenieService serv_pr=new PrietenieService(friendDBRepository,userDBRepository);
        FriendshipRequestService serv_req=new FriendshipRequestService(friendshipRequestDBRepository,userDBRepository,friendDBRepository);
        MessageService serv_message=new MessageService(messageDBRepository,replyMessageDBRepository,userDBRepository);
        EventService serv_event=new EventService(eventDBRepository);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/loginView.fxml"));
            AnchorPane root = loader.load();

            LoginController ctrl = loader.getController();
            ctrl.setService(user_serv, serv_pr, serv_req, serv_message,serv_event);

            primaryStage.setScene(new Scene(root, 700, 510));
            primaryStage.setTitle("Login");
            primaryStage.show();//i give up cya bye
        }catch(
    IOException e){e.printStackTrace();}

    }

    public static void main(String[] args) {
        launch(args);
    }

}

