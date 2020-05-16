package com.alto.model.requests;


public class TokenRequest {

  private String username;

  private String devicetoken;




  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDevicetoken() {
    return devicetoken;
  }

  public void setDevicetoken(String devicetoken) {
    this.devicetoken = devicetoken;
  }
}
