package com.uade.tpo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final ObjectMapper mapper = new ObjectMapper();

  public OrderController(OrderQueueService orders, InboxService inbox) {
    this.orders = orders; this.inbox = inbox;
  }

  // 1) Usuario encola un pedido
  @PostMapping("/orders")
  public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest body) throws Exception {
    if (body == null || body.userId == null || body.packageId == null)
      return ResponseEntity.badRequest().body("userId y packageId son obligatorios");
    orders.enqueueOrder(mapper.writeValueAsString(body));
    return ResponseEntity.ok().build();
  }

  // 2) Técnico reclama un pedido (espera hasta 5s si no hay)
  @PostMapping("/tech/claim")
  public ResponseEntity<?> claim() {
    String json = orders.blockingClaimNext(5);
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
