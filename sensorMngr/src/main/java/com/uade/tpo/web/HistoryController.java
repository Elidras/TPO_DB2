package com.uade.tpo.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.service.HistoryService;

@RestController
@RequestMapping("/users")
public class HistoryController {

  private final HistoryService history;

  public HistoryController(HistoryService history) {
    this.history = history;
  }

  @GetMapping("/{uid}/history")
  public ResponseEntity<List<String>> history(
      @PathVariable String uid,
      @RequestParam(defaultValue = "0") int from,
      @RequestParam(defaultValue = "49") int to) {

    var data = history.read(uid, from, to);
    if (data == null || data.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(data);
  }
}
