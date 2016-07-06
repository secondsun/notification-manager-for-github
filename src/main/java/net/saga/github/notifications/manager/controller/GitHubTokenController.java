/*
 * Copyright (C) 2016 Your Organisation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.saga.github.notifications.manager.controller;

import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.Icons525View;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javax.annotation.PostConstruct;
import net.saga.github.notifications.manager.MainApp;

/**
 *
 * @author summers
 */
@ViewController(value = "/fxml/GithubToken.fxml", title = "Provide GitHub Token")
public class GitHubTokenController  {

    @FXMLViewFlowContext
    ViewFlowContext context;
    @FXML
    Pane githubTokenPane;

    @FXML
    JFXTextField tokenField;

    @FXML
    JFXButton submitButton;
    private StackPane root;

    @PostConstruct
    public void init() {
        root = (StackPane) context.getRegisteredObject("ContentPane");
        MainApp.BUS.register(this);

        final RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        GlyphIcon icon = new Icons525View(Icons525.WARNING_SIGN);
        icon.setStyleClass("error");
        icon.setStyle(";");
        icon.setSize("1em");

        githubTokenPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        validator.setIcon(icon);
        tokenField.getValidators().add(validator);
        tokenField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                tokenField.validate();
            }
        });

        submitButton.setOnAction((ActionEvent event) -> {
            if (tokenField.validate()) {
                MainApp.ACCOUNT.setGithubToken(tokenField.getText());
            }
        });

    }

    @Subscribe
    public void handleAccountEvent(AccountEvent event) {
        if (event == AccountEvent.TOKEN_AVAILABLE) {
            MainApp.BUS.unregister(this);
            Platform.runLater(() -> {
                try {
                    Flow newFlow = new Flow(NotificationListController.class);
                    FlowHandler handler = newFlow.createHandler(context);
                    root.getChildren().setAll(handler.start());
                } catch (FlowException ex) {
                    Logger.getLogger(LoadingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

}
