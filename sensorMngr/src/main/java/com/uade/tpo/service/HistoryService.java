package com.uade.tpo.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HistoryService {

  private final StringRedisTemplate tpl;
  private final ObjectMapper mapper = new ObjectMapper();

  public HistoryService(StringRedisTemplate tpl) { this.tpl = tpl; }

  private String key(String uid) { return "tpo:history:" + uid; }

  public void append(String userId, String orderId, String processId,
                     String status, String techId) {
    try {
      var json = mapper.writeValueAsString(Map.of(
        "orderId", orderId,
        "processId", processId,
        "executed_at", Instant.now().toString(),
        "status", status,
        "techId", techId
      ));
      tpl.opsForList().leftPush(key(userId), json); // más reciente primero
    } catch (Exception ignored) {}
  }

  public List<String> read(String userId, int from, int to) {
    return tpl.opsForList().range(key(userId), from, to);
  }
}
