package com.uade.tpo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

  // Crea la conexión a Redis usando lo definido en application.properties
  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  // Cliente simple para leer/escribir Strings en Redis
  @Bean
  public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory cf) {
    return new StringRedisTemplate(cf);
  }
}       
