package socialnetwork.controller;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class MessageAlert {
    static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
    Alert message=new Alert(type);
        message.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    message.setHeaderText(header);
    message.setContentText(text);
    message.initOwner(owner);
    message.showAndWait();
}

    static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("ErrorMessage");
        message.setContentText(text);
        message.showAndWait();
    }
}
