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

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.client.WebSocketClientNegotiation;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import static net.saga.github.notifications.manager.service.net.NotificationsEvent.UPDATE_NOTIFICATIONS;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

/**
 *
 * @author summers
 */
public class WebsocketProvider {

    private static XnioWorker worker;

    static {
        try {
            worker = Xnio.getInstance().createWorker(OptionMap.builder()
                    .set(Options.WORKER_IO_THREADS, 2)
                    .set(Options.CONNECTION_HIGH_WATER, 1000000)
                    .set(Options.CONNECTION_LOW_WATER, 1000000)
                    .set(Options.WORKER_TASK_CORE_THREADS, 30)
                    .set(Options.WORKER_TASK_MAX_THREADS, 30)
                    .set(Options.TCP_NODELAY, true)
                    .set(Options.CORK, true)
                    .getMap());
        } catch (IOException | IllegalArgumentException ex) {
            Logger.getLogger(WebsocketProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public static WebSocketChannel getWebsocketClient(URI serverURI, String bearerToken, EventBus bus) {
        try {
            

            WebSocketClient.ConnectionBuilder builder = WebSocketClient.connectionBuilder(worker, new DefaultByteBufferPool(false, 2048), serverURI);
            builder.setClientNegotiation(new WebSocketClientNegotiation(null, null){
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("Authorization", Lists.newArrayList("bearer " + bearerToken));
                }
            });
            
            if ("wss".equals(serverURI.getScheme())) {
                setupSSLSocket(builder);
            }
            
            WebSocketChannel channel = builder.connect().get();
            channel.resumeReceives();
            
            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                    bus.post(UPDATE_NOTIFICATIONS);

                }
                
                @Override
                protected void onText(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
                    super.onText(webSocketChannel, messageChannel); //To change body of generated methods, choose Tools | Templates.
                    bus.post(UPDATE_NOTIFICATIONS);
                }

                @Override
                protected void onError(WebSocketChannel channel, Throwable error) {
                    super.onError(channel, error);
                    Logger.getLogger(WebsocketProvider.class.getName()).log(Level.SEVERE, error.getMessage(), error);

                }
            });

            return channel;
        } catch (IOException | CancellationException ex) {
            Logger.getLogger(WebsocketProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    private static void setupSSLSocket(WebSocketClient.ConnectionBuilder builder) {

        try {

            if (System.getProperty("insecure", "").equals("true")) {
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                final X509TrustManager insecureManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };

                // Create an ssl socket factory with our all-trusting manager
                sslContext.init(null, new TrustManager[]{insecureManager}, new SecureRandom());
                UndertowXnioSsl ssl = new UndertowXnioSsl(Xnio.getInstance(), OptionMap.EMPTY, sslContext);
                builder.setSsl(ssl);
            }

        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            Logger.getLogger(WebsocketProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
