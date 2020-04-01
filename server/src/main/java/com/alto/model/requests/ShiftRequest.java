package com.alto.model.requests;


import java.sql.Timestamp;

public class ShiftRequest {

  private Long tempId;

  private String username;

  private String lat;

  private String lon;

  private int shiftstatuskey;

  private String shiftSignoff;

  private String orderId;

  private String clockedAddy;

  private String clientId;

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

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public int getShiftstatuskey() {
    return shiftstatuskey;
  }

  public void setShiftstatuskey(int shiftstatuskey) {
    this.shiftstatuskey = shiftstatuskey;
  }

  public String getShiftSignoff() {
    return shiftSignoff;
  }

  public void setShiftSignoff(String shiftSignoff) {
    this.shiftSignoff = shiftSignoff;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getClockedAddy() {
    return clockedAddy;
  }

  public void setClockedAddy(String clockedAddy) {
    this.clockedAddy = clockedAddy;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
}
