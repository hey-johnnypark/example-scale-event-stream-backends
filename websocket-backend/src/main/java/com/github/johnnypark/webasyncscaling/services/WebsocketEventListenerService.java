package com.github.johnnypark.webasyncscaling.services;

import com.google.common.collect.Maps;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
public class WebsocketEventListenerService implements ApplicationListener {

  private Map<String,String> sessionUserIds = Maps.newHashMap();

  @Autowired
  private NotificationsDispatcherService ndService;

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof SessionConnectEvent) {
      handleSessionConnect((SessionConnectEvent) event);
    } else if (event instanceof SessionConnectedEvent) {
      handleSessionConnected((SessionConnectedEvent) event);
    } else if (event instanceof SessionDisconnectEvent) {
      handleSessionDisconnected((SessionDisconnectEvent) event);
    }
  }

  private void handleSessionConnect(SessionConnectEvent event) {
    // Links the session to the Login
    StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
    sessionUserIds.put(header.getSessionId(), header.getLogin());
  }

  private void handleSessionConnected(SessionConnectedEvent event) {
    // Registers session connected with login and sessionId
    StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = header.getSessionId();
    ndService.sessionConnected(sessionUserIds.get(sessionId), sessionId);
  }

  private void handleSessionDisconnected(SessionDisconnectEvent event) {
    // Registers session disconnect with login and sessionId
    StompHeaderAccessor header = StompHeaderAccessor.wrap((event).getMessage());
    String sessionId = header.getSessionId();
    ndService.sessionDisconnected(sessionUserIds.remove(sessionId), sessionId);
  }


}