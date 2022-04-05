package socialnetwork.ui;

import org.postgresql.gss.GSSOutputStream;
import socialnetwork.domain.*;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UIUser {
    private UtilizatorService servUser;
    private PrietenieService servPrietenie;
    private FriendshipRequestService servRequest;
    private MessageService servMessage;
    private  Utilizator u;

    public UIUser(Utilizator u,UtilizatorService servUser, PrietenieService servPrietenie, FriendshipRequestService servRequest,MessageService servMessage) {
        this.u=u;
        this.servUser = servUser;
        this.servPrietenie = servPrietenie;
        this.servRequest = servRequest;
        this.servMessage=servMessage;
        System.out.println("You are now user "+u.getFirstName()+" "+u.getLastName()+"\n");
    }

    static void optiuni() {
        System.out.println("1.Send friend request");
        System.out.println("2.Show all my friend request");
        System.out.println("3.Answer to a friend request");
        System.out.println("4.Send a message");
        System.out.println("5.Display conversation with a user");
        System.out.println("6.Respond to a message");
        System.out.println("0.Exit");
    }
    void displayMessages(){
        for(Message m:servMessage.getAll()) {
            if (m.getFrom().equals(u) || m.getTo().contains(u))
                System.out.println(m.display());
        }
    }
    void sendFR(){
        Scanner myObj = new Scanner(System.in);
        System.out.print("Give the ID of the person you want to send a FR to: ");
        String idd = myObj.nextLine();
        String error = "";Long id;
        if(idd.isEmpty()) error+="ID cannot be null \n";
        if (error.isEmpty()) {
            try {
                id = Long.parseLong(idd);
            } catch (IllegalArgumentException e) { error += "ID cannot contain chars\n"; }
        }
        if(error.isEmpty()){
            try{
                id = Long.parseLong(idd);
                String s="pending";
                FriendshipRequest p=new FriendshipRequest(u.getId(),id,s);
                servRequest.sendRequest(p);
                System.out.println("Friendship request sent");

            }catch (ServiceException e){error+=e.getMessage();}
            System.out.println(error);
        }
    }
    void answerReq(){
        Scanner myObj = new Scanner(System.in);
        System.out.print("Respond to the friend request of the user with the ID: ");String idd = myObj.nextLine();
        Long id;
        System.out.print("rejected/approved:");String state=myObj.nextLine();
        if(idd.isEmpty()) System.out.println("ID cannot be null \n");
        else
        try {
            id = Long.parseLong(idd);
            FriendshipRequest p=new FriendshipRequest(u.getId(),id,state);
            servRequest.sendRequest(p);
            System.out.println("Friendship request "+state);
        } catch (IllegalArgumentException e) {
            System.out.println("ID cannot contain chars\n"); }
          catch(ServiceException e){
              System.out.println(e.getMessage());
          }
    }
    void showFR(){
        try {
            for (UserFriendshipRequestDTO dto : servRequest.userFriendshipRequest(u.getId()))
                System.out.println(dto);
            System.out.println();
        }catch (ServiceException e){ System.out.println(e.getMessage());}
    }
    void sendMessage(){
        Scanner myObj = new Scanner(System.in);
        System.out.println("IDs of the users you want to send a message to: ");
        List<Long> ids=new ArrayList<>();
        long id=-1;
        while(id!=0){
            System.out.print("<ID> or <0> to stop: ");
            try{
                id=myObj.nextLong();
                if(id!=0) ids.add(id);
            }catch(Exception e){
                System.out.println("Enter only numbers!\n");
                myObj.nextLine();
            }
        }
        String error="";
        List<Utilizator> toList=new ArrayList<Utilizator>();
        for(Long idd: ids){
            Utilizator u=servUser.getUtilizator(idd);
            if(u!=null) toList.add(u);
            else System.out.println("There is no user with the id "+idd+"\n");
        }
        if(toList.size()!=0) {
            myObj.nextLine();
            System.out.print("Enter the message you want to send: ");
            String mess;
            mess = myObj.nextLine();
            Message message = new Message(u, toList, mess);
            try {
                servMessage.sendMessage(message);
                System.out.println("Message sent\n");
            } catch (ServiceException e) {
                System.out.println(e.getMessage());
            }
        }

    }
    void displayConv(){
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID of the user you want to see the conversation with: ");
        long id;
        try {
            id =myObj.nextLong();
            System.out.println();
            boolean convoExists=false;
            for(ReplyMessage m:servMessage.conversation(u.getId(),id)) {
                if(m.getMessageObject()==null)
                        System.out.println(m);
                else System.out.println("Replied to<"+m.getMessageObject().getMessage()+">"+m);
                convoExists=true;
            }
            if(!convoExists)
                System.out.println("No conversation to display with this user...");
        }
        catch (Exception e){
            System.out.println("Please enter only numbers...");
            myObj.nextLine();
        }
    }
    void respondMessage(){
        Scanner myObj = new Scanner(System.in);
        int ok=0;
        displayMessages();
        System.out.print("ID of the message you want to respond to: ");
        long id=0;
        try {
            id =myObj.nextLong();
            System.out.println();
        }
        catch (Exception e){
            System.out.println("Please enter only numbers...");
            myObj.nextLine();
            ok=1;
        }

        Message m=servMessage.getMessage(id);
        if(m==null) {System.out.println("The message does not exist!\n");return;}
        int present=0;
        for(Message ms:servMessage.getAll()) {
            if ((ms.getFrom().equals(u) || ms.getTo().contains(u))&&ms.getId().equals(id))
                present=1;
        }
        if(present==0) {
            System.out.println("The message you want to respond to does not belong to you!\n");
            return;}

        if(ok==0){
            myObj.nextLine();
            System.out.print("Message you want to send: ");
            String mess=myObj.nextLine();

            String answ="y";
            if(m.getTo().size()>1) {
                System.out.print("This message is sent to multiple users. Do you want to reply to all of them?[y/n]");
                answ = myObj.nextLine();
                while (!answ.equals("y") && !answ.equals("n")) {
                    answ = myObj.nextLine();
                    if (!answ.equals("y") && !answ.equals("n"))
                        System.out.print("Answer with <y> for <yes> and <n> for <no>!\n");
                }
            }
                if(answ.equals("y")||
                        m.getTo().size()==1) {
                    ArrayList<Utilizator> list=new ArrayList<>();
                    for(Utilizator ut:m.getTo())
                        if(!ut.getId().equals(u.getId())) list.add(ut);
                    if(!m.getFrom().getId().equals(u.getId())) list.add(m.getFrom());
                    //for(Utilizator ut:list) System.out.println(ut);
                    ReplyMessage r = new ReplyMessage(u, list, mess, m);
                    servMessage.replyMessage(r);
                }
                else{
                    long idd=0;
                    if(!m.getFrom().getId().equals(u.getId()))
                    System.out.println(m.getFrom().getId()+" "+m.getFrom().getFirstName()+" "+m.getFrom().getLastName());
                    for(Utilizator ut:m.getTo())
                        if(!m.getFrom().getId().equals(ut.getId()))
                        System.out.println(ut.getId()+" "+ut.getFirstName()+" "+ut.getLastName());
                    System.out.print("ID of the user you want to send a reply message to: ");

                    try {
                        idd =myObj.nextLong();
                        System.out.println();
                    }
                    catch (Exception e){
                        System.out.println("Please enter only numbers...");
                        myObj.nextLine();
                        ok=1;
                    }
                    if(ok==0)
                    {
                        ArrayList<Utilizator> l=new ArrayList<>();
                        l.add(servUser.getUtilizator(idd));
                        ReplyMessage r = new ReplyMessage(u, l, mess, m);
                        try {
                            servMessage.replyMessage(r);
                        }catch(ServiceException e){
                            System.out.println(e.getMessage());}
                    }
                }
            }
    }
    public void start() {

            while(true){
                optiuni();
                Scanner myObj = new Scanner(System.in);
                System.out.print("Option: ");
                int opt = Integer.parseInt(myObj.nextLine());
                switch(opt){
                    case 1:
                        sendFR();
                        break;
                    case 2:
                        showFR();
                        break;
                    case 3:
                        answerReq();
                        break;
                    case 4:
                        sendMessage();
                        break;
                    case 5:
                        displayConv();
                        break;
                    case 6:
                        respondMessage();
                        break;
                    case 0:
                        System.out.println("You are no longer "+u.getFirstName()+" "+u.getLastName()+"...\n");
                        return;
                }
            }
    }

}
