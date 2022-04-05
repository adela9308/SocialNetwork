package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.Message;
import socialnetwork.domain.PageObject;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.service.*;

import java.io.IOException;

import javafx.stage.Stage;

public class LoginController {
    private UtilizatorService userService;
    private MessageService messageService;
    private FriendshipRequestService requestService;
    private PrietenieService friendshipService;
    private EventService eventService;
    public void setService(UtilizatorService userService,PrietenieService friendshipService,
                           FriendshipRequestService requestService,MessageService messageService,EventService eventService) {
        this.userService=userService;
        this.friendshipService=friendshipService;
        this.messageService=messageService;
        this.requestService=requestService;
        this.eventService=eventService;
        passwordTextField.setVisible(false);
        passwordTextField1.setVisible(false);
        passwordTextField2.setVisible(false);
    }
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField email1TextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField passwordTextField1;
    @FXML
    private TextField passwordTextField2;
    @FXML
    private PasswordField password1Field;
    @FXML
    private PasswordField password1Field2;
    @FXML
    private Button registerButton;

    @FXML
    private Button exitButton;

    @FXML
    void handleLogin() throws IOException {
        String mail=emailTextField.getText();
        String pass= passwordField.getText();
        try {
            Utilizator u=userService.getUtilizatorMailPassword(mail,pass);
            if(u==null) MessageAlert.showErrorMessage(null,"Non-existent user");
            else{
                openUserStage(u);
            }
        }catch (IllegalArgumentException e){MessageAlert.showErrorMessage(null,"Please enter only numbers!");}
        finally {
            emailTextField.clear();
            passwordField.clear();
        }
    }

    @FXML
    void handleEyePressed(){
        String pass= passwordField.getText();
        passwordField.setVisible(false);
        passwordTextField.setText(pass);
        passwordTextField.setVisible(true);
    }
    @FXML
    void handleEyeReleased(){
        String p=passwordTextField.getText();
        passwordTextField.setVisible(false);
        passwordField.setText(p);
        passwordField.setVisible(true);
    }

    @FXML
    void handleEyePressed1(){
        String pass= password1Field.getText();
        password1Field.setVisible(false);
        passwordTextField1.setText(pass);
        passwordTextField1.setVisible(true);
    }
    @FXML
    void handleEyeReleased1(){
        String p=passwordTextField1.getText();
        passwordTextField1.setVisible(false);
        password1Field.setText(p);
        password1Field.setVisible(true);
    }
    @FXML
    void handleEyePressed2(){
        String pass= password1Field2.getText();
        password1Field2.setVisible(false);
        passwordTextField2.setText(pass);
        passwordTextField2.setVisible(true);
    }
    @FXML
    void handleEyeReleased2(){
        String p=passwordTextField2.getText();
        passwordTextField2.setVisible(false);
        password1Field2.setText(p);
        password1Field2.setVisible(true);
    }
    @FXML
    void handleRegisterButton(){
        String f=firstNameTextField.getText();
        String l=lastNameTextField.getText();
        String em=email1TextField.getText();
        String p=password1Field.getText();
        String p2=password1Field2.getText();
        if(!p.equals(p2)) {MessageAlert.showErrorMessage(null,"The passwords do not match!");return;}
        try{
            Utilizator u=new Utilizator(f,l,em,p);
            userService.addUtilizator(u);
            openUserStage(userService.getUtilizatorMailPassword(em,p));

            firstNameTextField.clear();
            lastNameTextField.clear();
            email1TextField.clear();
            password1Field.clear();
            password1Field2.clear();

        }catch (ServiceException e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        firstNameTextField.clear();
        lastNameTextField.clear();
        email1TextField.clear();
        password1Field.clear();
        password1Field2.clear();

    }


    public void openUserStage(Utilizator u){
        try {
            //open new stage
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/userView.fxml"));
            AnchorPane root = loader.load();

            UserController ctrl = loader.getController();
            PageObject userPage=new PageObject(userService,messageService,friendshipService,requestService,u,eventService);
            ctrl.setService(userPage);

            Stage userStage = new Stage();
            userStage.setScene(new Scene(root, 1800, 850));
            userStage.setMaximized(true);
            userStage.setTitle("UserPage");
            userStage.show();
        }catch(IOException e){e.printStackTrace();}
    }
    @FXML
    void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();

    }


}
