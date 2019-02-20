package com.github.johnnypark.webasyncscaling.services;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.springframework.stereotype.Component;

@Component
public class UserSessions {

  private Multimap<String,String>  map = TreeMultimap.create();

  public UserSessions() {
  }

  public int sessionCreated(String user, String session) {
    map.put(user, session);
    return map.get(user).size();
  }

  public int sessionDropped(String user, String session) {
    map.remove(user, session);
    return map.get(user).size();
  }

  public boolean hasSessions(String user) {
    return map.containsKey(user) ? map.get(user).size() > 0 : false;
  }


}
