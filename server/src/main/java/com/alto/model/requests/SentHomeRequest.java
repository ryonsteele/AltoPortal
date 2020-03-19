package com.alto.model.requests;


public class SentHomeRequest {

  private Long tempId;

  private String username;

  private String clientName;

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

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }
}
