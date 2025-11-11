package com.uade.tpo.web.dto;

import java.util.Map;

public class OrderCreateRequest {
  public String userId;
  public String processId;          
  public String packageId;          
  public Map<String,String> params; 
  public String notes;
}
