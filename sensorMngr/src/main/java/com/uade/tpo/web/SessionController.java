package com.uade.tpo.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.service.SessionService;

@RestController
@RequestMapping("/sessions")
public class SessionController {

  private final SessionService sessions;

  public SessionController(SessionService sessions) {
    this.sessions = sessions;
  }

  // Inicia una sesión de consulta privada (custom query)
  // Ej: POST /sessions/start?userId=u123
  @PostMapping("/start")
  public ResponseEntity<?> start(@RequestParam String userId) {
    if (userId == null || userId.isBlank()) {
      return ResponseEntity.badRequest().body("userId es obligatorio");
    }
    String sid = sessions.start(userId.trim());
    return ResponseEntity.ok(Map.of("sessionId", sid, "status", "ACTIVE"));
  }

  // Detiene la sesión y devuelve duración en ms
  // Ej: POST /sessions/SID-xxxx/stop
  @PostMapping("/{sid}/stop")
  public ResponseEntity<?> stop(@PathVariable String sid) {
    long ms = sessions.stop(sid);
    if (ms <= 0) return ResponseEntity.badRequest().body("sesión inexistente o sin start_at");
    return ResponseEntity.ok(Map.of("sessionId", sid, "duration_ms", ms, "status", "CLOSED"));
  }

  // Lee el uso acumulado del mes (ms) para facturación
  // Ej: GET /sessions/usage/u123?yyyyMM=202511
  @GetMapping("/usage/{uid}")
  public ResponseEntity<?> usage(@PathVariable String uid, @RequestParam String yyyyMM) {
    long ms = sessions.usageMs(uid, yyyyMM);
    return ResponseEntity.ok(Map.of("userId", uid, "yyyyMM", yyyyMM, "usage_ms", ms));
  }
}
