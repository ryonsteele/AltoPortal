package com.alto.model.requests;


public class InterestRequest {

  private Long tempId;

  private String username;

  private String orderId;


  public Long getTempId() {
    return tempId;
  }

  public void setTempId(Long tempId) {
    this.tempId = tempId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

}
