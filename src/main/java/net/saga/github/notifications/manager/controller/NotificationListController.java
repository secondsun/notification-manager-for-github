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
import io.datafx.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javax.annotation.PostConstruct;
import net.saga.github.notifications.manager.MainApp;
import net.saga.github.notifications.manager.service.net.NotificationsEvent;
import net.saga.github.notifications.manager.vo.Notification;

/**
 *
 * @author summers
 */
@ViewController(value = "/fxml/NotificationsList.fxml", title = "Notification List")
public class NotificationListController {
    
    @FXML
    private ListView notificationsList;
    
    ObservableList<Notification> observableList = FXCollections.observableArrayList();
    
    @PostConstruct
    public void init() {
        observableList.addAll(MainApp.NOTIFICATIONS.getNotifications());
        notificationsList.setItems(observableList);
        notificationsList.setCellFactory(new Callback<ListView<Notification>, javafx.scene.control.ListCell<Notification>>() {
            @Override
            public ListCell<Notification> call(ListView<Notification> listView) {
                return new NotificationCell();
            }
        });
        MainApp.BUS.register(this);
        MainApp.NOTIFICATIONS.init();
    }
    
    @Subscribe
    public void updateNotifications(NotificationsEvent event) {
        if (event == NotificationsEvent.NEW_NOTIFICATIONS) {
            observableList.clear();
            observableList.addAll(MainApp.NOTIFICATIONS.getNotifications());
            notificationsList.refresh();
        }
    }
    
}
