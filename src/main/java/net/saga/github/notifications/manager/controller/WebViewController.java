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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import io.datafx.controller.ViewController;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import net.saga.github.notifications.manager.net.NotificationsClient;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 *
 * @author summers
 */
@ViewController(value = "/fxml/OauthLoginWebView.fxml", title = "Sign-In")
public class WebViewController implements Initializable {

    @FXML
    private WebView loginWebView ;
    
    private NotificationsClient client = new NotificationsClient();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loginWebView.getEngine().setUserAgent("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
            
            loginWebView.getEngine().load(client.login());
            loginWebView.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    if (loginWebView.getEngine().getLocation().contains("openid-connect/oauth/oob")) {
                        try {
                            String code = loginWebView.getEngine().getLocation().split("code=")[1];
                            System.out.println(client.exchangeCode(code));
                        } catch (OAuthSystemException | OAuthProblemException ex) {
                            Logger.getLogger(WebViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        } catch (OAuthSystemException ex) {
            Logger.getLogger(WebViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

