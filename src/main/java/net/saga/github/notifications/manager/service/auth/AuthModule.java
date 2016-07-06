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
package net.saga.github.notifications.manager.service.auth;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.saga.github.notifications.manager.service.net.NetworkEvent;
import net.saga.github.notifications.service.persistence.PropertyManager;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

/**
 *
 * This module maintains the OAuth2 tokens, automatically refreshes, etc.
 *
 * @author summers
 */
public class AuthModule {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private static final String URL_BASE = "https://localhost:8443/github-notification-logger-0.1.0-SNAPSHOT/api/s/";
    private static final String NOTIFICATIONS_ENDPOINT = "notifications";
    private static final String ACCOUNTS_ENDPOINT = "accounts";

    private static final String AUTHZ_URL = "https://auth.sagaoftherealms.net/auth";
    private static final String AUTHZ_ENDPOINT = "/realms/github-notifications/protocol/openid-connect/auth";
    private static final String ACCESS_TOKEN_ENDPOINT = "/realms/github-notifications/protocol/openid-connect/token";
    private static final String REFRESH_TOKEN_ENDPOINT = "/realms/github-notifications/protocol/openid-connect/token";
    private static final String AUTHZ_CLIENT_ID = "Notifications_For_GitHub_Server";
    private static final String AUTHZ_REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

    private String bearerToken = "";
    private static final String ACCESS_TOKEN = "NotificationsClient.ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "NotificationsClient.REFRESH_TOKEN";
    private static final String EXPIRES_AT = "NotificationsClient.ESPIRES_AT";
    private final EventBus bus;

    public AuthModule(EventBus bus) {
        this.bus = bus;
    }

    public void init() {
        bus.register(this);
        loadBearerToken();
        if (bearerToken.isEmpty()) {
            bus.post(AuthEvent.NOT_LOGGED_IN);
        } else if (!isExpired()) {
            bus.post(AuthEvent.LOGGED_IN);
        } else {
            refreshToken();
        }

    }

    public String getLoginUrl() throws OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(AUTHZ_URL + AUTHZ_ENDPOINT)
                .setClientId(AUTHZ_CLIENT_ID)
                .setRedirectURI(AUTHZ_REDIRECT_URL)
                .setResponseType("code")
                .buildQueryMessage();

        return request.getLocationUri();
    }

    public boolean isLoggedIn() {
        return !isBlank(bearerToken) && !isExpired();
    }

    public Future<String> exchangeCode(String code) {
        return EXECUTOR.submit(() -> {
            try {
                OAuthClientRequest request = OAuthClientRequest
                        .tokenLocation(AUTHZ_URL + ACCESS_TOKEN_ENDPOINT)
                        .setClientId(AUTHZ_CLIENT_ID)
                        .setRedirectURI(AUTHZ_REDIRECT_URL)
                        .setGrantType(GrantType.AUTHORIZATION_CODE)
                        .setCode(code)
                        .buildBodyMessage();

                OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

                OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);

                bearerToken = oAuthResponse.getAccessToken();
                PropertyManager.write(ACCESS_TOKEN, bearerToken);
                PropertyManager.write(REFRESH_TOKEN, oAuthResponse.getRefreshToken());
                PropertyManager.write(EXPIRES_AT, getExpiresAt(oAuthResponse));

                bus.post(AuthEvent.LOGGED_IN);
                return bearerToken;
            } catch (OAuthSystemException | OAuthProblemException ex) {
                Logger.getLogger(AuthModule.class.getName()).log(Level.SEVERE, null, ex);
                bearerToken = "";
                PropertyManager.write(ACCESS_TOKEN, "");
                PropertyManager.write(REFRESH_TOKEN, "");
                PropertyManager.write(EXPIRES_AT, now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                bus.post(AuthEvent.NOT_LOGGED_IN);
            } finally {
            }
            return "";
        });

    }

    private String getExpiresAt(OAuthJSONAccessTokenResponse oAuthResponse) {
        LocalDateTime time = LocalDateTime.now().plusSeconds(oAuthResponse.getExpiresIn());
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Subscribe
    public void onNetworkEvent(NetworkEvent event) {
        if (event == NetworkEvent.AUTH_ERROR) {
            bearerToken = "";
        }
        bus.post(AuthEvent.NOT_LOGGED_IN);
    }

    private boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expries = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(getExpires(), LocalDateTime::from);
        return now.isAfter(expries);
    }

    private String getExpires() {
        return PropertyManager.read(EXPIRES_AT).orElse(now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private Future<String> refreshToken() {
        return EXECUTOR.submit(() -> {
            try {
                String refreshToken = PropertyManager.read(REFRESH_TOKEN).orElse("");
                if (refreshToken.isEmpty()) {
                    bus.post(AuthEvent.NOT_LOGGED_IN);
                    return "";
                }

                OAuthClientRequest request = OAuthClientRequest
                        .tokenLocation(AUTHZ_URL + REFRESH_TOKEN_ENDPOINT)
                        .setClientId(AUTHZ_CLIENT_ID)
                        .setRedirectURI(AUTHZ_REDIRECT_URL)
                        .setGrantType(GrantType.REFRESH_TOKEN)
                        .setRefreshToken(refreshToken)
                        .buildBodyMessage();

                OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

                OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);

                bearerToken = oAuthResponse.getAccessToken();
                PropertyManager.write(ACCESS_TOKEN, bearerToken);
                PropertyManager.write(REFRESH_TOKEN, oAuthResponse.getRefreshToken());
                PropertyManager.write(EXPIRES_AT, getExpiresAt(oAuthResponse));

                bus.post(AuthEvent.LOGGED_IN);
                return bearerToken;
            } catch (OAuthSystemException | OAuthProblemException exception) {
                Logger.getLogger(AuthModule.class.getName()).log(Level.SEVERE, null, exception);
                bearerToken = "";
                PropertyManager.write(ACCESS_TOKEN, "");
                PropertyManager.write(REFRESH_TOKEN, "");
                PropertyManager.write(EXPIRES_AT, now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                bus.post(AuthEvent.NOT_LOGGED_IN);
                return "";
            }
        });
    }

    public Future<String> getAccessToken() {
        return EXECUTOR.submit(() -> {
            if (isLoggedIn()) {
                return bearerToken;
            } else {
               if (bearerToken.isEmpty()) {
                   return "";
               } else {
                   return refreshToken().get();
               }
            }
        });
        
    }

    private void loadBearerToken() {
        bearerToken = PropertyManager.read(ACCESS_TOKEN).orElse("");
    }

}
