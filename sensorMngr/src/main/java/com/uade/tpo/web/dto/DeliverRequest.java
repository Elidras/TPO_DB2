package com.uade.tpo.web.dto;

public class DeliverRequest {
  public String userId;      // obligatorio
  public String orderId;     // obligatorio
  public String title;       // obligatorio
  public String content;     // obligatorio (texto libre del técnico)
  public String deliveredAt; // opcional; si falta, lo completamos
}
