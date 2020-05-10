package com.alto.model;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "PREFERENCES")
public class UserPreferences implements Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "temp_id")
  private Long tempid;

  @Column(name = "monday")
  private Boolean monday;

  @Column(name = "tuesday")
  private Boolean tuesday;

  @Column(name = "wednesday")
  private Boolean wednesday;

  @Column(name = "thursday")
  private Boolean thursday;

  @Column(name = "friday")
  private Boolean friday;

  @Column(name = "saturday")
  private Boolean saturday;

  @Column(name = "sunday")
  private Boolean sunday;

  @Column(name = "certs")
  private String certs;

  @Column(name = "region")
  private String region;



  public UserPreferences(){}

  public UserPreferences(Long tempid, String username) {
    this.username = username;
    this.tempid = tempid;
  }



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Long getTempid() {
    return tempid;
  }

  public void setTempid(Long tempid) {
    this.tempid = tempid;
  }

  public Boolean getMonday() {
    return monday;
  }

  public void setMonday(Boolean monday) {
    this.monday = monday;
  }

  public Boolean getTuesday() {
    return tuesday;
  }

  public void setTuesday(Boolean tuesday) {
    this.tuesday = tuesday;
  }

  public Boolean getWednesday() {
    return wednesday;
  }

  public void setWednesday(Boolean wednesday) {
    this.wednesday = wednesday;
  }

  public Boolean getThursday() {
    return thursday;
  }

  public void setThursday(Boolean thursday) {
    this.thursday = thursday;
  }

  public Boolean getFriday() {
    return friday;
  }

  public void setFriday(Boolean friday) {
    this.friday = friday;
  }

  public Boolean getSaturday() {
    return saturday;
  }

  public void setSaturday(Boolean saturday) {
    this.saturday = saturday;
  }

  public Boolean getSunday() {
    return sunday;
  }

  public void setSunday(Boolean sunday) {
    this.sunday = sunday;
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
}
