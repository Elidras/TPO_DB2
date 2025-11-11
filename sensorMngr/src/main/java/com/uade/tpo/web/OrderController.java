package com.uade.tpo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.service.HistoryService;
import com.uade.tpo.service.InboxService;
import com.uade.tpo.service.OrderQueueService;
import com.uade.tpo.web.dto.DeliverRequest;
import com.uade.tpo.web.dto.OrderCreateRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class OrderController {

  private final OrderQueueService orders;
  private final InboxService inbox;
  private final StringRedisTemplate tpl;
  private final HistoryService history;                 // <-- NUEVO
  private final ObjectMapper mapper = new ObjectMapper();

  public OrderController(OrderQueueService orders,
                         InboxService inbox,
                         StringRedisTemplate tpl,
                         HistoryService history) {      // <-- NUEVO
    this.orders = orders;
    this.inbox = inbox;
    this.tpl = tpl;
    this.history = history;                             // <-- NUEVO
  }

  // 1) Usuario encola un pedido
  @PostMapping("/orders")
  public ResponseEntity<?> create(@RequestBody OrderCreateRequest body) throws Exception {
    if (body == null) {
      return ResponseEntity.badRequest().body("cuerpo requerido");
    }
    // Acepta processId (nuevo) o packageId (legacy)
    String pid = (body.processId != null && !body.processId.isBlank())
        ? body.processId.trim()
        : (body.packageId != null ? body.packageId.trim() : null);

    if (body.userId == null || body.userId.isBlank() || pid == null || pid.isBlank()) {
      return ResponseEntity.badRequest().body("userId y processId/packageId son obligatorios");
    }

    // Validar que el proceso exista en el catálogo (/ofertas) -> Redis
    boolean existe = Boolean.TRUE.equals(tpl.hasKey("tpo:process:" + pid));
    if (!existe) {
      return ResponseEntity.badRequest().body("processId inválido: no está en /ofertas");
    }

    String paramsJson = (body.params == null) ? "{}" : mapper.writeValueAsString(body.params);
    String oid = orders.createAndEnqueue(body.userId.trim(), pid, paramsJson, body.notes);
    return ResponseEntity.ok(java.util.Map.of("orderId", oid));
  }

  // 2) Técnico reclama un pedido (espera hasta 5s si no hay)
  @PostMapping("/tech/claim")
  public ResponseEntity<?> claim(
      @RequestHeader(value = "X-User", required = false) String user,
      @RequestHeader(value = "X-Role", required = false) String role) {

    // validación simple de cabeceras
    if (role == null || !role.equalsIgnoreCase("TECH")) {
      return ResponseEntity.status(403).body("X-Role debe ser TECH");
    }
    if (user == null || user.isBlank()) {
      return ResponseEntity.badRequest().body("X-User es obligatorio");
    }

    String json = orders.claimNextWith(user, 5); // marca IN_PROGRESS
    return (json == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(json);
  }

  // 3) Técnico entrega el informe al inbox del usuario y completa la orden + historial
  @PostMapping("/tech/deliver")
  public ResponseEntity<?> deliver(
      @RequestHeader(value = "X-User", required = false) String techId,
      @RequestHeader(value = "X-Role", required = false) String role,
      @RequestBody DeliverRequest body) throws Exception {

    // Validación de headers (técnico)
    if (role == null || !role.equalsIgnoreCase("TECH")) {
      return ResponseEntity.status(403).body("X-Role debe ser TECH");
    }
    if (techId == null || techId.isBlank()) {
      return ResponseEntity.badRequest().body("X-User es obligatorio");
    }

    // Validación de payload
    if (body == null
        || body.userId == null || body.userId.isBlank()
        || body.orderId == null || body.orderId.isBlank()
        || body.title == null || body.title.isBlank()
        || body.content == null || body.content.isBlank()) {
      return ResponseEntity.badRequest().body("userId, orderId, title y content son obligatorios");
    }

    // Timestamp si no vino
    if (body.deliveredAt == null || body.deliveredAt.isBlank()) {
      body.deliveredAt = java.time.Instant.now().toString();
    }

    // Mensaje no prearmado, con trazabilidad
    String messageId = "MSG-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    var msg = new java.util.LinkedHashMap<String,Object>();
    msg.put("messageId",  messageId);
    msg.put("type",       "process_result");
    msg.put("orderId",    body.orderId);
    msg.put("userId",     body.userId);
    msg.put("title",      body.title);
    msg.put("content",    body.content);
    msg.put("createdAt",  body.deliveredAt);
    msg.put("from",       techId);

    // 1) Guardar mensaje en el inbox del usuario
    String json = mapper.writeValueAsString(msg);
    inbox.deliver(body.userId, json);

    // 2) Marcar la orden como COMPLETED y mover índices (pending -> done)
    orders.complete(body.orderId);

    // 3) Historial de ejecución (append a tpo:history:{uid})
    String orderKey = "tpo:order:" + body.orderId;
    String processId = (String) tpl.opsForHash().get(orderKey, "processId");
    history.append(body.userId, body.orderId, processId, "COMPLETED", techId);

    // 4) Respuesta
    return ResponseEntity.ok(java.util.Map.of(
        "messageId", messageId,
        "orderId", body.orderId,
        "status", "COMPLETED"
    ));
  }
}
