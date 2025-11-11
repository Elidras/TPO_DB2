package com.uade.tpo.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class InboxService {
  private final StringRedisTemplate tpl;
  private static final int MAX_INBOX = 200;

  public InboxService(StringRedisTemplate tpl) {
    this.tpl = tpl;
  }

  private String key(String uid) {
    return "tpo:user:" + uid + ":inbox";
  }

  // Añade un mensaje (JSON) al principio y recorta a los 200 más recientes
  public void deliver(String uid, String messageJson) {
    String k = key(uid);
    tpl.opsForList().leftPush(k, messageJson);
    tpl.opsForList().trim(k, 0, MAX_INBOX - 1);
  }

  // Lee un rango (0=el más nuevo)
  public List<String> read(String uid, int from, int to) {
    return tpl.opsForList().range(key(uid), from, to);
  }
}
