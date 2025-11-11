package com.uade.tpo.config;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ProcessCatalogLoader {
  private final StringRedisTemplate tpl;

  public ProcessCatalogLoader(StringRedisTemplate tpl) { this.tpl = tpl; }

  @PostConstruct
  public void preloadCatalog() {
    String setKey = "tpo:processes";
    if (Boolean.FALSE.equals(tpl.hasKey(setKey)) || Boolean.TRUE.equals(tpl.opsForSet().size(setKey) == 0)) {
      add("top-hot-3",   "Top 3 países más calurosos", "Los 3 paises mas calurosos recogidos por los sensores",  "15.0");
      add("top-cold-3",  "Top 3 países más fríos",     "Los 3 paises mas frios recogidos por los sensores",  "15.0");
      add("country-temp","Temperatura por país",       "Temperatura en un país dado",  "10.0");
    }
  }

  private void add(String id, String name, String description, String cost) {
    String h = "tpo:process:" + id;
    tpl.opsForHash().putAll(h, Map.of(
      "id", id,
      "name", name,
      "description", description,
      "cost", cost
    ));
    tpl.opsForSet().add("tpo:processes", id);
  }
}
