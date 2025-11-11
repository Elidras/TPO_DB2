package com.uade.tpo.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private final StringRedisTemplate tpl;

  public SessionService(StringRedisTemplate tpl) { this.tpl = tpl; }

  private String sKey(String sid) { return "tpo:session:" + sid; }
  private String usageKey(String uid, Instant t) {
    String ym = DateTimeFormatter.ofPattern("yyyyMM")
        .withZone(ZoneOffset.UTC)
        .format(t);
    return "tpo:usage:" + uid + ":" + ym;
  }

  /** Inicia sesión y devuelve el sessionId (sid). */
  public String start(String userId) {
    String sid = "SID-" + UUID.randomUUID().toString().substring(0, 8);
    String k = sKey(sid);
    tpl.opsForHash().putAll(k, Map.of(
        "sessionId", sid,
        "userId", userId,
        "start_at", Instant.now().toString(),
        "status", "ACTIVE"
    ));
    return sid;
  }

  /** Detiene sesión, guarda end/duration, acumula uso mensual (ms) y devuelve duración en ms. */
  public long stop(String sid) {
    String k = sKey(sid);
    String startStr = (String) tpl.opsForHash().get(k, "start_at");
    String uid = (String) tpl.opsForHash().get(k, "userId");
    if (startStr == null || uid == null) return 0L;

    Instant start = Instant.parse(startStr);
    Instant end = Instant.now();
    long ms = Duration.between(start, end).toMillis();

    tpl.opsForHash().put(k, "end_at", end.toString());
    tpl.opsForHash().put(k, "duration_ms", String.valueOf(ms));
    tpl.opsForHash().put(k, "status", "CLOSED");

    String uKey = usageKey(uid, end);
    long current = 0L;
    String cur = tpl.opsForValue().get(uKey);
    if (cur != null) {
      try { current = Long.parseLong(cur); } catch (NumberFormatException ignored) {}
    }
    tpl.opsForValue().set(uKey, String.valueOf(current + ms));

    return ms;
  }

  /** Lee uso acumulado del mes YYYYMM en ms. */
  public long usageMs(String userId, String yyyyMM) {
    String uKey = "tpo:usage:" + userId + ":" + yyyyMM;
    String cur = tpl.opsForValue().get(uKey);
    if (cur == null) return 0L;
    try { return Long.parseLong(cur); } catch (NumberFormatException e) { return 0L; }
  }
}
