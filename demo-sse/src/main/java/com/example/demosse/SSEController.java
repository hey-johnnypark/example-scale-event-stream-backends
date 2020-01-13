package com.example.demosse;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.websocket.server.PathParam;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Controller
public class SSEController {

  private static Logger LOG = LoggerFactory.getLogger(SSEController.class);
  private ExecutorService nonBlockingService = Executors.newCachedThreadPool();
  private Multimap<String, SseEmitter> emittersByUser = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());


  @GetMapping("/sse/{user}")
  public SseEmitter handleSse(@PathVariable(value = "user") String user) {
    SseEmitter emitter = new SseEmitter();
    emitter.onCompletion( () -> {
          emittersByUser.remove(user, emitter);
          LOG.info("Emitter {} removed", emitter);
    });
    emittersByUser.put(user, emitter);
    LOG.info("Add sse connection for {}", user);
    return emitter;
  }

  @KafkaListener(topics = "sse")
  private void listen(String user) {
    LOG.info("Received message {}", user);
    Optional.ofNullable(emittersByUser.get(user)).ifPresent(emitters -> {
      emitters.forEach(emitter -> {
        try {
          emitter.send(user);
          LOG.info("Sent message to {}", user);
        } catch (IllegalStateException e){
          System.out.println("Already closed");
        }
       catch (IOException e) {
          e.printStackTrace();
      }});
    });
  }

  @Bean
  public NewTopic sseTopic() {
    return TopicBuilder.name("sse")
        .build();
  }

}