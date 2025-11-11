package com.uade.tpo.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;

@RestController
@RequestMapping("/health")
public class RedisHealthController {

  private final StringRedisTemplate tpl;

  public RedisHealthController(StringRedisTemplate tpl) {
    this.tpl = tpl;
  }

  @GetMapping("/redis")
  public String ping() {
    // Escribe una clave temporal (10s). Si no hay error, hay conexión.
    tpl.opsForValue().set("tpo:ping", "ok", Duration.ofSeconds(10));
    return "PONG";
  }
}
