package com.uade.tpo.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderQueueService {

  private final StringRedisTemplate tpl;
  private final ObjectMapper mapper = new ObjectMapper();

  // Claves
  private static final String QUEUE_PENDING = "tpo:orders:pending";       // cola global FIFO
  private static final String ORDER_PREFIX  = "tpo:order:";               // HASH por orden
  private static final String USER_PENDING_PREFIX = "tpo:user:";          // listas por usuario

  public OrderQueueService(StringRedisTemplate tpl) {
    this.tpl = tpl;
  }

  /* ---------------- Compatibilidad con tu versión anterior ---------------- */

  /** Encola un JSON “crudo” (compatibilidad). */
  public void enqueueOrder(String orderJson) {
    tpl.opsForList().leftPush(QUEUE_PENDING, orderJson);
  }

  /** Reclama de la cola con timeout (compatibilidad, versión long). */
  public String blockingClaimNext(long timeoutSeconds) {
    String val = tpl.opsForList().rightPop(QUEUE_PENDING, Duration.ofSeconds(timeoutSeconds));
    return val; // si antes guardabas JSON directo en la cola, seguirá funcionando
  }

  /* ---------------- Nuevas funciones completas (recomendadas) ------------- */

  private String orderKey(String oid) { return ORDER_PREFIX + oid; }
  private String userPendingKey(String uid){ return USER_PENDING_PREFIX + uid + ":orders:pending"; }
  private String userDoneKey(String uid){ return USER_PENDING_PREFIX + uid + ":orders:done"; }

  /**
   * Crea el HASH de la orden y la encola en pendientes.
   * Devuelve el orderId (ej: ORD-ab12cd34).
   */
  public String createAndEnqueue(String userId, String processId, String paramsJson, String notes) {
    String oid = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
    String k = orderKey(oid);

    tpl.opsForHash().putAll(k, Map.of(
      "orderId", oid,
      "userId", userId,
      "processId", processId,
      "params", paramsJson != null ? paramsJson : "{}",
      "notes", notes != null ? notes : "",
      "status", "PENDING",
      "createdAt", Instant.now().toString()
    ));

    // Cola global y lista por usuario
    tpl.opsForList().leftPush(QUEUE_PENDING, oid);
    tpl.opsForList().leftPush(userPendingKey(userId), oid);
    return oid;
  }

  /** Reclama el siguiente orderId de la cola global (versión int). */
  public String blockingClaimNext(int timeoutSeconds) {
    return tpl.opsForList().rightPop(QUEUE_PENDING, timeoutSeconds, TimeUnit.SECONDS);
  }

  // --- NUEVO: reclamar con techId y marcar IN_PROGRESS ---
public String claimNextWith(String techId, int timeoutSeconds) {
  // saca el orderId de la cola global (FIFO)
  String oid = tpl.opsForList().rightPop(QUEUE_PENDING, timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);
  if (oid == null) return null;

  String k = orderKey(oid);
  // marca estado y auditoría
  tpl.opsForHash().put(k, "status", "IN_PROGRESS");
  tpl.opsForHash().put(k, "claimed_by", techId != null ? techId : "");
  tpl.opsForHash().put(k, "claimed_at", java.time.Instant.now().toString());

  // devuelve el pedido como JSON para que el técnico sepa qué tomó
  return getOrderJson(oid);
}


  /** Obtiene el HASH completo de una orden como JSON. */
  public String getOrderJson(String orderId) {
    Map<Object,Object> h = tpl.opsForHash().entries(orderKey(orderId));
    try {
      return mapper.writeValueAsString(h);
    } catch (Exception e) {
      return "{\"orderId\":\"" + orderId + "\"}";
    }
  }

  /** Marca la orden como completada y mueve índices del usuario. */
  public void complete(String orderId) {
    String k = orderKey(orderId);
    String uid = (String) tpl.opsForHash().get(k, "userId");
    tpl.opsForHash().put(k, "status", "COMPLETED");
    tpl.opsForHash().put(k, "completedAt", Instant.now().toString());
    if (uid != null) {
      tpl.opsForList().remove(userPendingKey(uid), 1, orderId);
      tpl.opsForList().leftPush(userDoneKey(uid), orderId);
    }
  }
}
