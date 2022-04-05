package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.exceptions.PrietenieValidationException;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.PrieteniiFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UI;
import sun.nio.ch.Util;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName1=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friends");
        //String fileName="data/users.csv";
       // Repository<Long,Utilizator> userFileRepository = new UtilizatorFile(fileName
         //       , new UtilizatorValidator());

        UserDBRepository userDBRepository=new UserDBRepository("users",
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres","postgres",new UtilizatorValidator());

       // Repository<Tuple<Long,Long>, Prietenie> friendFileRepository=new PrieteniiFile(fileName1,new PrietenieValidator());

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


        UtilizatorService serv=new UtilizatorService(userDBRepository,friendDBRepository);
        PrietenieService serv_pr=new PrietenieService(friendDBRepository,userDBRepository);
        FriendshipRequestService serv_req=new FriendshipRequestService(friendshipRequestDBRepository,userDBRepository,friendDBRepository);
        MessageService serv_message=new MessageService(messageDBRepository,replyMessageDBRepository,userDBRepository);
        UI ui=new UI(serv,serv_pr,serv_req,serv_message);
        //ui.start();
        MainFX.main(args);
    }
}


