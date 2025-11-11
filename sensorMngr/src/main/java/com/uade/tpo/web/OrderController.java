package com.uade.tpo.web;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.service.InboxService;
import com.uade.tpo.service.OrderQueueService;
import com.uade.tpo.web.dto.DeliverRequest;
import com.uade.tpo.web.dto.OrderCreateRequest;

@RestController
@RequestMapping
public class OrderController {

  private final OrderQueueService orders;
  private final InboxService inbox;
  private final StringRedisTemplate tpl;          // <--- NUEVO
  private final ObjectMapper mapper = new ObjectMapper();

  public OrderController(OrderQueueService orders,
                         InboxService inbox,
                         StringRedisTemplate tpl) {   // <--- NUEVO
    this.orders = orders;
    this.inbox = inbox;
    this.tpl = tpl;                                  // <--- NUEVO
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


  // 3) Técnico entrega el informe al inbox del usuario
  @PostMapping("/tech/deliver")
  public ResponseEntity<?> deliver(@RequestBody DeliverRequest body) throws Exception {
    if (body == null || body.userId == null || body.title == null)
      return ResponseEntity.badRequest().body("userId y title son obligatorios");
    inbox.deliver(body.userId, mapper.writeValueAsString(body));
    return ResponseEntity.ok().build();
  }
}
