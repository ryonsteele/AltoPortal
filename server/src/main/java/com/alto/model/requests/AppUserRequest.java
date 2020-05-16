package com.alto.model.requests;


public class AppUserRequest {

  private Long id;

  private String username;

  private String password;

  private String firstname;

  private String lastname;

  private String tempId;

  private String devicetoken;

  private String devicetype;

  private String certs;

  private String region;

  private Boolean firstTime;


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getTempId() {
    return tempId;
  }

  public void setTempId(String tempId) {
    this.tempId = tempId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDevicetoken() {
    return devicetoken;
  }

  public void setDevicetoken(String devicetoken) {
    this.devicetoken = devicetoken;
  }

  public String getDevicetype() {
    return devicetype;
  }

  public void setDevicetype(String devicetype) {
    this.devicetype = devicetype;
  }

  public String getCerts() {
    return certs;
  }

  public void setCerts(String certs) {
    this.certs = certs;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Boolean getFirstTime() {
    return firstTime;
  }

  public void setFirstTime(Boolean firstTime) {
    this.firstTime = firstTime;
  }
}
