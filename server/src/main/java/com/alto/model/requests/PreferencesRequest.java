package com.alto.model.requests;


public class PreferencesRequest {

  private Long tempId;

  private String username;

  private Boolean mon;

  private Boolean tue;

  private Boolean wed;

  private Boolean thur;

  private Boolean fri;

  private Boolean sat;

  private Boolean sun;


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

  public Boolean getMon() {
    return mon;
  }

  public void setMon(Boolean mon) {
    this.mon = mon;
  }

  public Boolean getTue() {
    return tue;
  }

  public void setTue(Boolean tues) {
    this.tue = tues;
  }

  public Boolean getWed() {
    return wed;
  }

  public void setWed(Boolean wed) {
    this.wed = wed;
  }

  public Boolean getThur() {
    return thur;
  }

  public void setThur(Boolean thur) {
    this.thur = thur;
  }

  public Boolean getFri() {
    return fri;
  }

  public void setFri(Boolean fri) {
    this.fri = fri;
  }

  public Boolean getSat() {
    return sat;
  }

  public void setSat(Boolean sat) {
    this.sat = sat;
  }

  public Boolean getSun() {
    return sun;
  }

  public void setSun(Boolean sun) {
    this.sun = sun;
  }
}
