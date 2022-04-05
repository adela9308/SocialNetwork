package socialnetwork.ui;

//import org.graalvm.compiler.debug.CSVUtil;
//import org.graalvm.compiler.debug.CSVUtil;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.UserFriendshipDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.exceptions.UserValidationException;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.PrieteniiFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.time.Month;
import java.util.List;
import java.util.Scanner;

public class UI {
    private UtilizatorService serv;
    private PrietenieService serv_pr;
    private FriendshipRequestService serv_req;
    private MessageService servMessage;
    private static Long idU;

    public UI(UtilizatorService serv,PrietenieService serv_pr,FriendshipRequestService serv_req,MessageService servMessage) {
        this.serv=serv;
        this.serv_pr=serv_pr;
        this.serv_req=serv_req;
        idU=serv.getLastID();
        this.servMessage=servMessage;
    }
    static void afisareUtilizatori(UtilizatorService serv){
        for(Utilizator u:serv.getAll()){
           // System.out.println(u);
           System.out.println(u.getId()+" "+u.getFirstName()+" "+u.getLastName());
        }
        System.out.println();
    }
    static void afisarePrieteniiAll(PrietenieService servP, UtilizatorService servU){
        for(Prietenie p:servP.getAll()){
            System.out.println("(ID:"+servU.getUtilizator(p.getId().getLeft()).getId()+" & "+
                    servU.getUtilizator(p.getId().getRight()).getId()+ ") "+
                    servU.getUtilizator(p.getId().getLeft()).getFirstName()+" "+
                    servU.getUtilizator(p.getId().getLeft()).getLastName()+" si "+
                    servU.getUtilizator(p.getId().getRight()).getFirstName()+" "+
                    servU.getUtilizator(p.getId().getRight()).getLastName()+" sunt prieteni") ;
        }
        System.out.println();
    }
    static void afisarePrieteni( PrietenieService servP ) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID: ");
        String idd = myObj.nextLine();
        String error = "";Long id;
        if(idd.isEmpty()) error+="ID cannot be null \n";
        if (error.isEmpty()) {
            try {
                id = Long.parseLong(idd);
            } catch (IllegalArgumentException e) { error += "ID cannot contain chars\n"; }
        }
        if(error.isEmpty())
        try {
            id = Long.parseLong(idd);
            for (UserFriendshipDTO dto : servP.userFriendships(id))
                System.out.println(dto);
            System.out.println();
        }catch (ServiceException e){ System.out.println(e.getMessage());}
    }
    static void afisarePrieteniMonth( PrietenieService servP ) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID: ");
        String idd = myObj.nextLine();
        String errorID = "",errorMonth="";Long id;
        if(idd.isEmpty()) errorID+="ID cannot be null \n";
        if (errorID.isEmpty()) {
            try {
                id = Long.parseLong(idd);
                System.out.print("Month: ");
                int monthnr=myObj.nextInt();
                Month m=Month.of(monthnr);

                for (UserFriendshipDTO dto : servP.userFriendshipsGivenMonth(id, m))
                    System.out.println(dto);
                System.out.println();
            } catch (Exception e) { errorID += "ID cannot contain chars\n"; }
        }
        System.out.println(errorID);


/*
        if(errorID.isEmpty())
        try {
            id = Long.parseLong(idd);
            //monthnr=Integer.parseInt(month);
            //m = Month.of(monthnr);
            for (UserFriendshipDTO dto : servP.userFriendshipsGivenMonth(id, m))
                System.out.println(dto);
            System.out.println();
        }catch (ServiceException e){ System.out.println(e.getMessage());}*/
    }

    static void optiuni(){
        System.out.println("1.Add utilizator");
        System.out.println("2.Remove utilizator");
        System.out.println("3.Afiseaza toti utilizatorii");
        System.out.println("4.Add prieten");
        System.out.println("5.Remove prieten");
        System.out.println("6.Afiseaza toate prieteniile");
        System.out.println("7.Afiseaza prietenii unui utilizator");
        System.out.println("8.Afiseaza prietenii unui utilizator dintr-o luna data");
        System.out.println("9.Numar de comunitati");
        System.out.println("10.Cea mai sociabila comunitate");
        System.out.println("11.Become a user");
        System.out.println("0.Exit");
    }
    static void addUtilizatorUI(UtilizatorService serv){
        Scanner myObj = new Scanner(System.in);
        //System.out.print("ID: ");Long id=Long.parseLong(myObj.nextLine());
       try {
           System.out.print("Nume: ");
           String nume = myObj.nextLine();
           System.out.print("Prenume: ");
           String prenume = myObj.nextLine();
           Utilizator u1 = new Utilizator(nume, prenume);
           //idU++;
           //u1.setId(idU);
           serv.addUtilizator(u1);
           System.out.println("Utilizator adaugat \n");
       }catch(ServiceException e) {System.out.println(e.getMessage()+"\n");}
    }
    static void addPrietenieUI(PrietenieService serv_pr){
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID1: ");String idd1=myObj.nextLine();
        System.out.print("ID2: ");String idd2=myObj.nextLine();
        String error = "",error1="",error2="";Long id1,id2;
        if(idd1.isEmpty()) error1+="ID1 cannot be null \n";
        if(idd2.isEmpty()) error2+="ID2 cannot be null \n";
        if (error1.isEmpty()) {
            try {
                id1 = Long.parseLong(idd1);
            } catch (IllegalArgumentException e) { error1 += "ID1 cannot contain chars\n"; }
        }
        if (error2.isEmpty()) {
            try {
                id2 = Long.parseLong(idd2);
            } catch (IllegalArgumentException e) { error2 += "ID2 cannot contain chars\n"; }
        }

        error=error1+error2;
        if(error.isEmpty()) {
            id1=Long.parseLong(idd1);
            id2=Long.parseLong(idd2);
            for (Prietenie p : serv_pr.getAll())
                if (p.getId().getLeft() == id1 && p.getId().getRight() == id2 ||
                        p.getId().getLeft() == id2 && p.getId().getRight() == id1)
                    error += "Prietenia deja exista\n";
        }
        if(error.isEmpty())
            try {
                id1=Long.parseLong(idd1);
                id2=Long.parseLong(idd2);
                serv_pr.addPrieten(id1, id2);
                System.out.println("Prietenie adaugata \n");
            }catch(ServiceException e) {error+=e.getMessage() +"\n";}
        if(!error.isEmpty()) System.out.println(error);
      //  catch(IllegalArgumentException s) {System.out.println(s.getMessage() +"\n");}

    }
    static void removeUserUI(UtilizatorService serv){
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID: ");
        String idd = myObj.nextLine();
        String error = "";Long id;
        if(idd.isEmpty()) error+="ID cannot be null \n";
        if (error.isEmpty()) {
            try {
                id = Long.parseLong(idd);
            } catch (IllegalArgumentException e) { error += "ID cannot contain chars\n"; }
        }
        if(error.isEmpty()){
            try {
                id = Long.parseLong(idd);
                serv.removeUtilizator(id);
                System.out.println("Utilizator sters \n");
            }catch (ServiceException e){error+=e.getMessage();}
        }
        if(!error.isEmpty()) {System.out.println(error +"\n");}
    }

    static void removePrietenieUI(PrietenieService serv_pr) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("ID1: ");String idd1=myObj.nextLine();
        System.out.print("ID2: ");String idd2=myObj.nextLine();
        String error = "",error1="",error2="";Long id1,id2;
        if(idd1.isEmpty()) error1+="ID1 cannot be null \n";
        if(idd2.isEmpty()) error2+="ID2 cannot be null \n";
        if (error1.isEmpty()) {
            try {
                id1 = Long.parseLong(idd1);
            } catch (IllegalArgumentException e) { error1 += "ID1 cannot contain chars\n"; }
        }
        if (error2.isEmpty()) {
            try {
                id2 = Long.parseLong(idd2);
            } catch (IllegalArgumentException e) { error2 += "ID2 cannot contain chars\n"; }
        }
        error=error1+error2;
        if(error.isEmpty()) {
            id1=Long.parseLong(idd1);
            id2=Long.parseLong(idd2);
            int ok=0;
            for (Prietenie p : serv_pr.getAll())
                if (p.getId().getLeft() == id1 && p.getId().getRight() == id2 ||
                        p.getId().getLeft() == id2 && p.getId().getRight() == id1) ok=1;
            if(ok==0) error += "Prietenia nu exista\n";
        }

        if(error.isEmpty())
        try {
            id1=Long.parseLong(idd1);
            id2=Long.parseLong(idd2);
            serv_pr.removePrieten(id1, id2);
            System.out.println("Relatie de prietenie stearsa \n");
        }catch(ServiceException e) {error+=e.getMessage() +"\n";}
        if(!error.isEmpty()) System.out.println(error);
       // catch(IllegalArgumentException s) {System.out.println(s.getMessage() +"\n");}
    }
    void ComunitateSociabilaUI(PrietenieService serv_pr,UtilizatorService serv){
        List<Long> ids=serv_pr.ComunitateSociabila();
        System.out.println("Cea mai sociabila comunitate contine userii: ");
        for(Long id:ids){
            System.out.println(serv.getUtilizator(id).getId()+" "+serv.getUtilizator(id).getFirstName()+
                    " "+serv.getUtilizator(id).getLastName());
        }
        System.out.println("si are 'lungimea' "+ids.size()+"\n");
    }
    void becomeUser( UtilizatorService servUser, PrietenieService servPr, FriendshipRequestService servReq){
        Scanner myObj = new Scanner(System.in);
        System.out.print("What user do you wish to become?ID: ");
        String idd = myObj.nextLine();
        String error = "";Long id;
        if(idd.isEmpty()) error+="ID cannot be null \n";
        if (error.isEmpty()) {
            try {
                id = Long.parseLong(idd);
            } catch (IllegalArgumentException e) { error += "ID cannot contain chars\n"; }
        }
        if(error.isEmpty()) {
            id = Long.parseLong(idd);
            if (servUser.getUtilizator(id) == null) error += "The user doesn't exist \n";
        }
        if(error.isEmpty()){
            id = Long.parseLong(idd);
            Utilizator u=servUser.getUtilizator(id);
            UIUser UIu=new UIUser(u,servUser,servPr,servReq,servMessage);
            UIu.start();
        }
        System.out.println(error);

    }
    public void start() {

        while(true){
            optiuni();
            Scanner myObj = new Scanner(System.in);
            System.out.print("Introduceti optiunea: ");//spatiu doar intre nr complex si operator
            int opt = Integer.parseInt(myObj.nextLine());
            switch(opt){
                case 1:
                    addUtilizatorUI(serv);
                    break;
                case 2:
                    removeUserUI(serv);
                    break;
                case 3:
                    afisareUtilizatori(serv);
                    break;
                case 4:
                    addPrietenieUI(serv_pr);
                    break;
                case 5:
                    removePrietenieUI(serv_pr);
                    break;
                case 6:
                    afisarePrieteniiAll(serv_pr,serv);
                    break;
                case 7:
                    afisarePrieteni(serv_pr);
                    break;
                case 8:
                    afisarePrieteniMonth(serv_pr);
                    break;
                case 9:
                    System.out.println("Numar comunitati: "+serv_pr.NumarComunitati()+"\n");
                    break;
                case 10:
                    ComunitateSociabilaUI(serv_pr,serv);
                    break;
                case 11:
                    becomeUser(serv,serv_pr,serv_req);
                    break;
                case 0:
                    System.out.println("Aplicatie inchisa...");
                    return;
            }
        }
    }
}
