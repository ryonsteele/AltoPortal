package com.alto.model;


import java.util.List;

public class PushMessageRequest {

  private String msgBody;

  private List<String> temps;


  public String getMsgBody() {
    return msgBody;
  }

  public void setMsgBody(String msgBody) {
    this.msgBody = msgBody;
  }

  public List<String> getTemps() {
    return temps;
  }

  public void setTemps(List<String> temps) {
    this.temps = temps;
  }
}
