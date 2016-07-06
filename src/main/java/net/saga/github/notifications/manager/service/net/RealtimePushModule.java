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
package net.saga.github.notifications.manager.service.net;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.undertow.websockets.core.WebSocketChannel;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.saga.github.notifications.manager.service.auth.AuthModule;
import net.saga.github.notifications.manager.vo.Notification;
import net.saga.github.notifications.service.persistence.HibernateModule;
import net.saga.github.notifications.service.persistence.ObjectMapperFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * This module will maintain connections on the WebSocket and route messages
 * from it to the application proper.
 *
 * @author summers
 */
public class RealtimePushModule {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    private static final String WEBSOCKET_URL = "wss://localhost:8443/github-notification-logger-0.1.0-SNAPSHOT/api/wss/realtime";
    private static final String URL_BASE = "https://localhost:8443/github-notification-logger-0.1.0-SNAPSHOT/api/s/";
    private static final String NOTIFICATIONS_ENDPOINT = "notifications";

    private final AuthModule authModule;
    private final EventBus bus;
    private final HibernateModule hibernate;
    private final List<Notification> notifications = new ArrayList<>();
    private WebSocketChannel wssClient;

    public RealtimePushModule(AuthModule auth, HibernateModule hibernate, EventBus bus) {
        this.authModule = auth;
        this.bus = bus;
        this.hibernate = hibernate;
    }

    public void init() {
        bus.register(this);
        EXECUTOR.submit(() -> {
            try {
                String bearerToken = authModule.getAccessToken().get();
                if (bearerToken.isEmpty()) {
                    bus.post(NetworkEvent.AUTH_ERROR);
                } else {
                    try {
                        OkHttpClient client = OKHttpProvider.getHttpClient();
                        Request request = new Request.Builder()
                                .url(URL_BASE + NOTIFICATIONS_ENDPOINT)
                                .addHeader("Authorization", "bearer " + bearerToken)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful() || response.code() != 200) {
                            if ((response.code() / 100) == 4) {
                                bus.post(NetworkEvent.AUTH_ERROR);
                            }
                            bus.post(NetworkEvent.NETWORK_ERROR);
                            return;
                        }
                        
                        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
                        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Notification.class);

                        notifications.addAll(mapper.readValue(response.body().string(), type));
                        bus.post(NotificationsEvent.NEW_NOTIFICATIONS);
                        startupWebsocket(bearerToken);
                    } catch (IOException | RuntimeException ex) {
                        Logger.getLogger(RealtimePushModule.class.getName()).log(Level.SEVERE, null, ex);
                        bus.post(NetworkEvent.NETWORK_ERROR);
                    }

                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(RealtimePushModule.class.getName()).log(Level.SEVERE, null, ex);
                bus.post(NetworkEvent.NETWORK_ERROR);
            }
        });
    }

    @Subscribe
    public void fetchUpdates(NotificationsEvent event) {
        if (event == NotificationsEvent.UPDATE_NOTIFICATIONS) {
            try {
                String bearerToken = authModule.getAccessToken().get();
                if (bearerToken.isEmpty()) {
                    bus.post(NetworkEvent.AUTH_ERROR);
                } else {
                    try {
                        OkHttpClient client = OKHttpProvider.getHttpClient();
                        Request request = new Request.Builder()
                                .url(URL_BASE + NOTIFICATIONS_ENDPOINT)
                                .addHeader("Authorization", "bearer " + bearerToken)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful() || response.code() != 200) {
                            if ((response.code() / 100) == 4) {
                                bus.post(NetworkEvent.AUTH_ERROR);
                            }
                            bus.post(NetworkEvent.NETWORK_ERROR);
                            return;
                        }

                        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
                        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Notification.class);

                        notifications.addAll(ObjectMapperFactory.getObjectMapper().readValue(response.body().string(), type));
                        bus.post(NotificationsEvent.NEW_NOTIFICATIONS);
                    } catch (IOException ex) {
                        Logger.getLogger(RealtimePushModule.class.getName()).log(Level.SEVERE, null, ex);
                        bus.post(NetworkEvent.NETWORK_ERROR);
                    }

                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(RealtimePushModule.class.getName()).log(Level.SEVERE, null, ex);
                bus.post(NetworkEvent.NETWORK_ERROR);
            }
        }
    }

    private void startupWebsocket(String bearerToken) throws InterruptedException {
        wssClient = WebsocketProvider.getWebsocketClient(URI.create(WEBSOCKET_URL), bearerToken, bus);
    }

    public List<Notification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

}
