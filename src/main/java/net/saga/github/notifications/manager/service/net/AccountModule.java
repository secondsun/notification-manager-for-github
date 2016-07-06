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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.saga.github.notifications.manager.controller.AccountEvent;
import net.saga.github.notifications.manager.service.auth.AuthEvent;
import net.saga.github.notifications.manager.service.auth.AuthModule;
import net.saga.github.notifications.manager.vo.ApplicationAccount;
import net.saga.github.notifications.service.persistence.ObjectMapperFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * This handles account stuff from the server
 *
 * @author summers
 */
public class AccountModule {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private static final String URL_BASE = "https://localhost:8443/github-notification-logger-0.1.0-SNAPSHOT/api/s/";
    private static final String ACCOUNTS_ENDPOINT = "accounts";
    private final AuthModule authModule;
    private final EventBus bus;

    public AccountModule(AuthModule module, EventBus bus) {
        this.authModule = module;
        this.bus = bus;
    }

    public Future<Boolean> hasGithubToken() {

        return EXECUTOR.submit(() -> {

            String bearerToken = authModule.getAccessToken().get();
            if (bearerToken.isEmpty()) {
                return false;
            } else {
                OkHttpClient client = OKHttpProvider.getHttpClient();
                Request request = new Request.Builder()
                        .url(URL_BASE + ACCOUNTS_ENDPOINT)
                        .addHeader("Authorization", "bearer " + bearerToken)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful() || response.code() != 200) {
                    if ((response.code() / 100) == 4) {
                        bus.post(NetworkEvent.AUTH_ERROR);
                    }
                    bus.post(NetworkEvent.NETWORK_ERROR);
                    return false;
                }

                ApplicationAccount account = ObjectMapperFactory.getObjectMapper().readValue(response.body().string(), ApplicationAccount.class);

                return !account.getGitHubToken().isEmpty();
            }
        });

    }

    public Future<String> setGithubToken(final String token) {
        return EXECUTOR.submit(() -> {

            try {
                String bearerToken = authModule.getAccessToken().get();
                if (bearerToken.isEmpty()) {
                    bus.post(AuthEvent.NOT_LOGGED_IN);
                    return "";
                } else {
                    try {
                        OkHttpClient client = OKHttpProvider.getHttpClient();
                        Request request = new Request.Builder()
                                .url(URL_BASE + ACCOUNTS_ENDPOINT)
                                .addHeader("Authorization", "bearer " + bearerToken)
                                .put(RequestBody.create(MediaType.parse("application/json"), token))
                                .build();

                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful() || response.code() != 200) {
                            if ((response.code() / 100) == 4) {
                                bus.post(NetworkEvent.AUTH_ERROR);
                            }
                            bus.post(NetworkEvent.NETWORK_ERROR);
                            return "";
                        }

                        ApplicationAccount account = ObjectMapperFactory.getObjectMapper().readValue(response.body().string(), ApplicationAccount.class);

                        bus.post(AccountEvent.TOKEN_AVAILABLE);
                        
                        return account.getGitHubToken();
                    } catch (IOException ex) {
                        Logger.getLogger(AccountModule.class.getName()).log(Level.SEVERE, null, ex);
                        bus.post(NetworkEvent.NETWORK_ERROR);
                        return "";
                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(AccountModule.class.getName()).log(Level.SEVERE, null, ex);
                bus.post(AuthEvent.NOT_LOGGED_IN);
                bus.post(NetworkEvent.NETWORK_ERROR);
                return "";
            }
        });
    }

}
