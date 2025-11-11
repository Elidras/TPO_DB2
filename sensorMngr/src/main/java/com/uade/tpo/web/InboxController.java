package com.uade.tpo.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.service.InboxService;
import com.uade.tpo.web.dto.DeliverRequest; 

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class InboxController {

  private final InboxService inbox;
  private final ObjectMapper mapper = new ObjectMapper();

  public InboxController(InboxService inbox) {
    this.inbox = inbox;
  }

  // ENTREGAR un mensaje en el inbox de {uid}
  @PostMapping("/{uid}/inbox")
  public ResponseEntity<?> deliverToInbox(@PathVariable String uid,
                                          @RequestBody DeliverRequest body) throws JsonProcessingException {
    if (uid == null || uid.isBlank()) {
      return ResponseEntity.badRequest().body("uid es obligatorio en la URL");
    }
    if (body == null || body.title == null || body.title.isBlank()) {
      return ResponseEntity.badRequest().body("title es obligatorio en el cuerpo");
    }
    // Si no vino userId en el body, lo ponemos igual al uid de la URL
    if (body.userId == null || body.userId.isBlank()) {
      body.userId = uid;
    }
    String json = mapper.writeValueAsString(body);
    inbox.deliver(uid, json);
    return ResponseEntity.ok().build();
  }

  // LEER el inbox de {uid} por rango (0 = más nuevo)
  @GetMapping("/{uid}/inbox")
  public ResponseEntity<List<String>> readInbox(@PathVariable String uid,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "49") int to) {
    return ResponseEntity.ok(inbox.read(uid, from, to));
  }
}
