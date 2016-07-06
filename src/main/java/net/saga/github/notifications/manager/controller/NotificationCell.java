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

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import net.saga.github.notifications.manager.vo.Notification;

/**
 *
 * @author summers
 */
public class NotificationCell extends ListCell<Notification> {

    @FXML
    private Text subject;

    @FXML
    private Text repository;

    @FXML
    private Text author;

    @FXML
    private Text seen;

    @FXML
    private ImageView thumbnail;
    
    @FXML
    private BorderPane notification;

    @Override
    protected void updateItem(Notification item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(render(item));
    }

    private Node render(Notification item) {
        
        if (seen == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NotificationCell.fxml"));
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException | RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
        
        if (item == null) {
            return notification;
        }
        
        repository.setText(item.getRepository().getName());
        author.setText(item.getUserId());
        subject.setText(item.getSubject().getTitle());
        seen.setVisible(!item.isUnread());
        //thumbnail.setImage(new Image(item.getRepository().getOwner().getAvatar_url().toString()));
        
        return notification;
    }

}
