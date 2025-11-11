package com.uade.tpo.service;

import java.time.Duration;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderQueueService {
  private final StringRedisTemplate tpl;
  private static final String PENDING = "tpo:orders:pending";

  public OrderQueueService(StringRedisTemplate tpl) { this.tpl = tpl; }

  public void enqueueOrder(String orderJson) {
    tpl.opsForList().leftPush(PENDING, orderJson); // entra por la izquierda
  }

  public String blockingClaimNext(long timeoutSeconds) {
    ListOperations<String,String> ops = tpl.opsForList();
    return ops.rightPop(PENDING, Duration.ofSeconds(timeoutSeconds)); // sale por la derecha (FIFO)
  }
}
