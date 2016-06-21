package net.saga.github.notifications.manager;

import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javax.annotation.PostConstruct;

@org.datafx.controller.FXMLController(value = "/fxml/Main.fxml", title = "Notification Manager for GitHub")
public class FXMLController implements Initializable {

    @FXML
    private JFXPopup toolbarPopup;

    @FXML
    private StackPane root;
    @FXML
    private JFXRippler optionsRippler;
    @FXML
    private StackPane optionsBurger;

    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @PostConstruct
    public void init() {
        // init Popup 
        toolbarPopup.setPopupContainer(root);
        toolbarPopup.setSource(optionsRippler);
        optionsBurger.setOnMouseClicked((e) -> {
            toolbarPopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -12, 15);
        });
    }

}
