package com.alto.model.requests;


import java.util.List;

public class PushMessageRequest {

  private String msgBody;

  private String audit;

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

  public String getAudit() {
    return audit;
  }

  public void setAudit(String audit) {
    this.audit = audit;
  }
}
