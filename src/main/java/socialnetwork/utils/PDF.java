package socialnetwork.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.io.FileOutputStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PDF {
    private MessageService messageService;
    private PrietenieService friendshipService;
    private UtilizatorService userService;
    private String path="D:\\UNI_UBB\\MAP\\LABORATOARE\\raport.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 25,
            5);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL);
    private static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 16);
    private static Font smallBoldFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,Font.BOLD);
    private static Font smallFontUnderliened=new Font(Font.FontFamily.TIMES_ROMAN, 18,5);

    public PDF(MessageService messageService, PrietenieService friendshipService, UtilizatorService userService) {
        this.messageService = messageService;
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    private static void addTitlePage(Document document,String title,String subject,String description)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        Paragraph t=new Paragraph(title, catFont);
        t.setAlignment(Paragraph.ALIGN_CENTER);
        preface.add(t);
        addEmptyLine(preface, 3);
        Paragraph s=new Paragraph(
                "Generated raport on " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_NICE) +": \n"+
                        subject+" ", smallFont);
        preface.add(s);
        addEmptyLine(preface, 2);
        preface.add(new Paragraph(description, smallBoldFont));

        addEmptyLine(preface, 2);

        document.add(preface);
        // Start a new page
        //document.newPage();
    }



    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    public void raport1(String pathh,Utilizator u, LocalDate d1,LocalDate d2){

        List<String> listMessages =new ArrayList<>();
        for(ReplyMessage r:messageService.messagesPeriodOfTime(u.getId(),d1,d2)){
            listMessages.add(r.getDate().format(Constants.DATE_TIME_FORMATTER)+"\n"+r.getFrom().getFirstName()+" "+r.getFrom().getLastName()+": "+ r.getMessage()+" \n \n");
        }
        List<String> listFriendships=new ArrayList<>();
        for(Prietenie p:friendshipService.friendshipsPeriodOfTime(u.getId(),d1,d2)){
            String name=null;
            if(p.getId().getLeft().equals(u.getId())){
                name=userService.getUtilizator(p.getId().getRight()).getFirstName()+" "+userService.getUtilizator(p.getId().getRight()).getLastName();
            }
            else name=userService.getUtilizator(p.getId().getLeft()).getFirstName()+" "+userService.getUtilizator(p.getId().getLeft()).getLastName();
            listFriendships.add(p.getDate().format(Constants.DATE_TIME_FORMATTER)+"\n"+name+" \n \n");
        }

        try {
            Document document = new Document();
            if(pathh!=null)
                PdfWriter.getInstance(document, new FileOutputStream(pathh));
            else PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            addTitlePage(document,"Raport: Messages and friendships","     Messages received by "+u.getFirstName()+" "
                    +u.getLastName()+" and new friendships","   Messages received in the period of time "+d1.format(Constants.DATE_FORMATTER)+" -> "+
                    d2.format(Constants.DATE_FORMATTER)+"\n");
            //add content

            Paragraph paragraph = new Paragraph();
            if(listMessages.size()==0) paragraph.add(new Paragraph("   No messages to show",subFont));
                for(String t: listMessages) {
                paragraph.add(new Paragraph(t, subFont));
            }
            addEmptyLine(paragraph, 5);
            paragraph.add(new Paragraph("   New friendships in the period ot time "+d1.format(Constants.DATE_FORMATTER)+" -> "+
                    d2.format(Constants.DATE_FORMATTER)+"\n",smallBoldFont));
            if(listFriendships.size()==0) paragraph.add(new Paragraph("   No new friendships",subFont));
            for(String t: listFriendships) {
                paragraph.add(new Paragraph(t, subFont));
            }

            document.add(paragraph);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void raport2(String pathh,Utilizator to, Utilizator from, LocalDate d1,LocalDate d2){

        List<String> listMessages =new ArrayList<>();
        for(ReplyMessage r:messageService.messagesFromUserPeriodOfTime(to.getId(), from.getId(),d1,d2)){
            listMessages.add(r.getDate().format(Constants.DATE_TIME_FORMATTER)+"\n"+r.getFrom().getFirstName()+" "+r.getFrom().getLastName()+": "+ r.getMessage()+" \n \n");
        }
        try {
            Document document = new Document();
            if(pathh!=null)
                PdfWriter.getInstance(document, new FileOutputStream(pathh));
            else PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            addTitlePage(document,"Raport: Messages","     Messages received by "+to.getFirstName()+" "
                    +to.getLastName()+" from "+from.getFirstName()+" "+from.getLastName()+" ","    Messages received in the period of time "+d1.format(Constants.DATE_FORMATTER)+" -> "+
                    d2.format(Constants.DATE_FORMATTER)+"\n");
            //add content
            Paragraph paragraph = new Paragraph();
            if(listMessages.size()==0) paragraph.add(new Paragraph("   No messages to show",subFont));
            for(String t: listMessages) {
                paragraph.add(new Paragraph(t, subFont));
            }
            document.add(paragraph);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public <T> void generatePDF(String pathh, Iterable<T> list, String titlu, String subject, String description){
        try {
            Document document = new Document();
            if(pathh!=null)
                PdfWriter.getInstance(document, new FileOutputStream(pathh));
            else PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            addTitlePage(document,titlu,subject,description);
            addContent(document,list);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private <T> void addContent(Document document,Iterable <T> list) throws DocumentException {

        Paragraph paragraph = new Paragraph();
        for(T t:list) {
            paragraph.add(new Paragraph(t.toString(), subFont));
        }
        addEmptyLine(paragraph, 5);

        document.add(paragraph);

    }

}
