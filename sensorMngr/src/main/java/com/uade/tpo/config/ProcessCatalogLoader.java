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
    upsert("top-hot-3",   "Top 3 países más calurosos",
           "Los 3 países más calurosos recogidos por los sensores", "15.0");
    upsert("top-cold-3",  "Top 3 países más fríos",
           "Los 3 países más fríos recogidos por los sensores",     "15.0");
    upsert("country-temp","Temperatura por país",
           "Temperatura promedio actual y extrema en un país específico", "10.0");
  }

  private void upsert(String id, String name, String description, String cost) {
    String h = "tpo:process:" + id;
    tpl.opsForHash().putAll(h, Map.of(
      "id", id, "name", name, "description", description, "cost", cost
    ));
    tpl.opsForSet().add("tpo:processes", id);
  }
}
