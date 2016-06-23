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
package net.saga.github.notifications.manager.net;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.saga.github.notifications.manager.persistence.PropertyManager;
import org.apache.commons.lang.StringUtils;
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
 * @author summers
 */
public class NotificationsClient {

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

    public String login() throws OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(AUTHZ_URL + AUTHZ_ENDPOINT)
                .setClientId(AUTHZ_CLIENT_ID)
                .setRedirectURI(AUTHZ_REDIRECT_URL)
                .setResponseType("code")
                .buildQueryMessage();

        return request.getLocationUri();
    }

    public boolean isLoggedIn() {
        return !isBlank(bearerToken);
    }
    
    public String getAccount() {
        if (StringUtils.isBlank(bearerToken)) {
            throw new IllegalStateException("There is no bearer token.");
        }
        throw new IllegalStateException("Not Implemented");
    }

    public String setAccountGitHubToken() {
        if (StringUtils.isBlank(bearerToken)) {
            throw new IllegalStateException("There is no bearer token.");
        }
        throw new IllegalStateException("Not Implemented");
    }

    public String getNotifications() {
        if (StringUtils.isBlank(bearerToken)) {
            throw new IllegalStateException("There is no bearer token.");
        }
        throw new IllegalStateException("Not Implemented");
    }

    public String exchangeCode(String code) throws OAuthSystemException, OAuthProblemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(AUTHZ_URL + ACCESS_TOKEN_ENDPOINT)
                .setClientId(AUTHZ_CLIENT_ID)
                .setRedirectURI(AUTHZ_REDIRECT_URL)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setCode(code)
                .buildBodyMessage();

        
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
 
        OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);

        System.out.println(oAuthResponse);
        PropertyManager.write(ACCESS_TOKEN, oAuthResponse.getAccessToken());
        PropertyManager.write(REFRESH_TOKEN, oAuthResponse.getRefreshToken());
        PropertyManager.write(EXPIRES_AT, getExpiresAt(oAuthResponse));
        bearerToken = oAuthResponse.getAccessToken();
        return oAuthResponse.getAccessToken();        
    }
    
    private String getExpiresAt(OAuthJSONAccessTokenResponse oAuthResponse) {
        LocalDateTime time = LocalDateTime.now().plusSeconds(oAuthResponse.getExpiresIn());
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
