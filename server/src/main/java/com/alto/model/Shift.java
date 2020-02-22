package com.alto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Created by fan.jin on 2016-10-15.
 */

@Entity
@Table(name = "SHIFTS")
public class Shift implements Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id")
  private String orderid;

  @Column(name = "username")
  private String username;

  @JsonIgnore
  @Column(name = "tempid")
  private String tempid;

  @Column(name = "status")
  private String status;

  @Column(name = "shift_starttime")
  private Timestamp shiftStartTime;

  @Column(name = "shift_endtime")
  private Timestamp shiftEndTime;

  @Column(name = "break_starttime")
  private Timestamp breakStartTime;

  @Column(name = "break_endtime")
  private Timestamp breakEndTime;

  @Column(name = "shift_starttime_actual")
  private Timestamp shiftStartTimeActual;

  @Column(name = "shift_start_signoff")
  private String shiftStartSignoff;

  @Column(name = "shift_endtime_actual")
  private Timestamp shiftEndTimeActual;

  @Column(name = "shift_end_signoff")
  private String shiftEndSignoff;

  @Column(name = "client_id")
  private String clientId;

  @Column(name = "client_name")
  private String clientName;

  @Column(name = "order_specialty")
  private String orderSpecialty;

  @Column(name = "order_certification")
  private String orderCertification;

  @Column(name = "floor")
  private String floor;

  @Column(name = "shift_number")
  private String shiftNumber;

  @Column(name = "note")
  private String note;

  @Column(name = "clockin_address")
  private String clockInAddress;

  @Column(name = "checkin_lat")
  private String checkinLat;

  @Column(name = "checkin_lon")
  private String checkinLon;

  @Column(name = "clockout_address")
  private String clockoutAddress;

  @Column(name = "checkout_lat")
  private String checkoutLat;

  @Column(name = "checkout_lon")
  private String checkoutLon;



  public Shift(){}

  public Shift(String orderid, String username, String tempid) {
    this.orderid = orderid;
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

  public String getOrderid() {
    return orderid;
  }

  public void setOrderid(String orderid) {
    this.orderid = orderid;
  }

  public String getTempid() {
    return tempid;
  }

  public void setTempid(String tempid) {
    this.tempid = tempid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Timestamp getShiftStartTime() {
    return shiftStartTime;
  }

  public void setShiftStartTime(Timestamp shiftStartTime) {
    this.shiftStartTime = shiftStartTime;
  }

  public Timestamp getShiftEndTime() {
    return shiftEndTime;
  }

  public void setShiftEndTime(Timestamp shiftEndTime) {
    this.shiftEndTime = shiftEndTime;
  }

  public Timestamp getBreakStartTime() {
    return breakStartTime;
  }

  public void setBreakStartTime(Timestamp breakStartTime) {
    this.breakStartTime = breakStartTime;
  }

  public Timestamp getBreakEndTime() {
    return breakEndTime;
  }

  public void setBreakEndTime(Timestamp breakEndTime) {
    this.breakEndTime = breakEndTime;
  }

  public Timestamp getShiftStartTimeActual() {
    return shiftStartTimeActual;
  }

  public void setShiftStartTimeActual(Timestamp shiftStartTimeActual) {
    this.shiftStartTimeActual = shiftStartTimeActual;
  }

  public Timestamp getShiftEndTimeActual() {
    return shiftEndTimeActual;
  }

  public void setShiftEndTimeActual(Timestamp shiftEndTimeActual) {
    this.shiftEndTimeActual = shiftEndTimeActual;
  }

  public String getClientId() {
    return clientId;
  }

  public String getShiftStartSignoff() {
    return shiftStartSignoff;
  }

  public void setShiftStartSignoff(String shiftStartSignoff) {
    this.shiftStartSignoff = shiftStartSignoff;
  }

  public String getShiftEndSignoff() {
    return shiftEndSignoff;
  }

  public void setShiftEndSignoff(String shiftEndSignoff) {
    this.shiftEndSignoff = shiftEndSignoff;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getOrderSpecialty() {
    return orderSpecialty;
  }

  public void setOrderSpecialty(String orderSpecialty) {
    this.orderSpecialty = orderSpecialty;
  }

  public String getOrderCertification() {
    return orderCertification;
  }

  public void setOrderCertification(String orderCertification) {
    this.orderCertification = orderCertification;
  }

  public String getFloor() {
    return floor;
  }

  public void setFloor(String floor) {
    this.floor = floor;
  }

  public String getShiftNumber() {
    return shiftNumber;
  }

  public void setShiftNumber(String shiftNumber) {
    this.shiftNumber = shiftNumber;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getClockInAddress() {
    return clockInAddress;
  }

  public void setClockInAddress(String clockInAddress) {
    this.clockInAddress = clockInAddress;
  }

  public String getCheckinLat() {
    return checkinLat;
  }

  public void setCheckinLat(String checkinLat) {
    this.checkinLat = checkinLat;
  }

  public String getCheckinLon() {
    return checkinLon;
  }

  public void setCheckinLon(String checkinLon) {
    this.checkinLon = checkinLon;
  }

  public String getClockoutAddress() {
    return clockoutAddress;
  }

  public void setClockoutAddress(String clockoutAddress) {
    this.clockoutAddress = clockoutAddress;
  }

  public String getCheckoutLat() {
    return checkoutLat;
  }

  public void setCheckoutLat(String checkoutLat) {
    this.checkoutLat = checkoutLat;
  }

  public String getCheckoutLon() {
    return checkoutLon;
  }

  public void setCheckoutLon(String checkoutLon) {
    this.checkoutLon = checkoutLon;
  }
}
