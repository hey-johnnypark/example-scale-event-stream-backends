package com.github.johnnypark.webasyncscaling.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.johnnypark.webasyncscaling.model.UserNotification;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationsListenerService {

  Logger LOG = LoggerFactory.getLogger(NotificationsListenerService.class);

  @Autowired
  private NotificationsDispatcherService ndService;



  private ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "notifications")
  public void listen(String message) {
    try {
      UserNotification msg = objectMapper.readValue(message, UserNotification.class);
      ndService.notificationReceived(msg);
    } catch (IOException e) {
      LOG.warn("Failed de-serializing notification: {}", e.getMessage());
    }

  }

}
