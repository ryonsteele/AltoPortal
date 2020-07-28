package com.alto.model.requests;


public class UserRemoveRequest {

  private String username;
  private Boolean usertype;


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Boolean getUsertype() {
    return usertype;
  }

  public void setUsertype(Boolean usertype) {
    this.usertype = usertype;
  }
}
