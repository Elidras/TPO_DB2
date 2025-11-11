package com.uade.tpo.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ofertas")
public class ProcessController {

  private final StringRedisTemplate tpl;

  public ProcessController(StringRedisTemplate tpl) {
    this.tpl = tpl;
  }

  // GET /ofertas -> lista todo el catálogo
  @GetMapping
  public List<Map<Object, Object>> list() {
    Set<String> ids = tpl.opsForSet().members("tpo:processes");
    if (ids == null || ids.isEmpty()) return List.of();

    return ids.stream()
      .map(id -> tpl.opsForHash().entries("tpo:process:" + id))
      .collect(Collectors.toList());
  }

  // GET /ofertas/{id} -> una oferta concreta
  @GetMapping("/{id}")
  public Map<Object, Object> get(@PathVariable String id) {
    return tpl.opsForHash().entries("tpo:process:" + id);
  }
}
