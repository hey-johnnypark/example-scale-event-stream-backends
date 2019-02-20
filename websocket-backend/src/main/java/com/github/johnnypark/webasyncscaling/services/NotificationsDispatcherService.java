package com.github.johnnypark.webasyncscaling.services;

import com.github.johnnypark.webasyncscaling.model.Greeting;
import com.github.johnnypark.webasyncscaling.model.UserNotification;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class NotificationsDispatcherService implements MessageListener<UserNotification> {

  private Logger LOG = LoggerFactory.getLogger(NotificationsDispatcherService.class);

  @Autowired
  private MessageSendingOperations<String> messagingTemplate;

  @Autowired
  private UserSessions activeUsers;

  @Autowired
  private HazelcastInstance hazelcast;

  private Map<String,String> registrations = new ConcurrentHashMap<>();

  public void sessionConnected(String user, String sessionId){
    if (activeUsers.sessionCreated(user, sessionId) == 1){
      LOG.info("sessionConnected(): Subscribes to topic [{}]", user);
      ITopic<UserNotification> topic = hazelcast.getTopic(user);
      registrations.put(user, topic.addMessageListener(this));
    }
  }

  public void sessionDisconnected(String user, String sessionId){
    if (activeUsers.sessionDropped(user, sessionId) == 0){
      LOG.info("sessionDropped(): Unsubscribes from topic [{}]", user);
      hazelcast.getTopic(user).removeMessageListener(registrations.remove(user));
    }
  }

  public void notificationReceived(UserNotification notification){
    LOG.info("notificationReceived(): {}", notification);
    hazelcast.getTopic(notification.getName()).publish(notification);
  }


  @Override
  public void onMessage(Message<UserNotification> message) {
    Greeting greeting = new Greeting(message.getMessageObject().getMessage());
    String destination = "/queue/user/" + message.getMessageObject().getName();
    messagingTemplate.convertAndSend(destination, greeting);
  }
}
