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

import com.jfoenix.controls.JFXProgressBar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.annotation.PostConstruct;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;

/**
 *
 * @author summers
 */
@ViewController(value = "/fxml/Loading.fxml", title = "Loading")
public class LoadingController  {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private Text text;

    @FXML
    private VBox container;

    @FXML
    private JFXProgressBar progress;

    @PostConstruct
    public void init() {
        progress.prefWidthProperty().bind(container.widthProperty());
        text.wrappingWidthProperty().bind(container.widthProperty());

        StackPane root = (StackPane) context.getRegisteredObject("ContentPane");

        new Thread(() -> {
            try {
                Thread.sleep(
                        3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LoadingController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                try {
                    Flow newFlow = new Flow(WebViewController.class);
                    root.getChildren().setAll(newFlow.start());
                } catch (FlowException ex) {
                    Logger.getLogger(LoadingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }).start();
        
        
    }

    
}
