package com.alto.model;


import java.sql.Timestamp;

public class SessionsRequest {

  private String start;

  private String end;


  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }
}
