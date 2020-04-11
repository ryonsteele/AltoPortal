package com.alto.model;


import java.io.Serializable;
import java.sql.Timestamp;


public class Sessions implements Serializable {

  private String orderid;

  private String username;

  private String tempid;

  private String status;

  private String shiftStartTime;

  private String shiftEndTime;

  private String breakStartTime;

  private String breakEndTime;

  private String shiftStartTimeActual;

  private String shiftStartSignoff;

  private String shiftEndTimeActual;

  private String shiftEndSignoff;

  private String clientId;

  private String clientName;

  private String orderSpecialty;

  private String orderCertification;

  private String floor;

  private String shiftNumber;

  private String clockInAddress;

  private String checkinLat;

  private String checkinLon;

  private String clockoutAddress;

  private String checkoutLat;

  private String checkoutLon;

  private String tempName;



  public Sessions(){}

  public Sessions(String orderid, String username, String tempid, String status, String shiftStartTime, String shiftEndTime,
                  String breakStartTime, String breakEndTime, String shiftStartTimeActual, String shiftEndTimeActual,
                  String shiftStartSignoff, String shiftEndSignoff, String clientId, String clientName, String orderSpecialty,
                  String orderCertification, String shiftNumber, String clockInAddress, String checkinLat,
                  String checkinLon, String clockoutAddress, String checkoutLat, String checkoutLon) {

    this.orderid = orderid;
    this.username = username;
    this.tempid = tempid;
    this.status = status;
    this.shiftStartTime = shiftStartTime;
    this.shiftEndTime = shiftEndTime;
    this.breakStartTime = breakStartTime;
    this.breakEndTime = breakEndTime;
    this.shiftStartTimeActual = shiftStartTimeActual;
    this.shiftEndTimeActual = shiftEndTimeActual;
    this.shiftStartSignoff = shiftStartSignoff;
    this.shiftEndSignoff = shiftEndSignoff;
    this.clientId = clientId;
    this.clientName = clientName;
    this.orderSpecialty = orderSpecialty;
    this.orderCertification = orderCertification;
    this.shiftNumber = shiftNumber;
    this.clockInAddress = clockInAddress;
    this.checkinLat = checkinLat;
    this.checkinLon = checkinLon;
    this.clockoutAddress = clockoutAddress;
    this.checkoutLat = checkoutLat;
    this.checkoutLon = checkoutLon;

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

  public String getShiftStartTime() {
    return shiftStartTime;
  }

  public void setShiftStartTime(String shiftStartTime) {
    this.shiftStartTime = shiftStartTime;
  }

  public String getShiftEndTime() {
    return shiftEndTime;
  }

  public void setShiftEndTime(String shiftEndTime) {
    this.shiftEndTime = shiftEndTime;
  }

  public String getBreakStartTime() {
    return breakStartTime;
  }

  public void setBreakStartTime(String breakStartTime) {
    this.breakStartTime = breakStartTime;
  }

  public String getBreakEndTime() {
    return breakEndTime;
  }

  public void setBreakEndTime(String breakEndTime) {
    this.breakEndTime = breakEndTime;
  }

  public String getShiftStartTimeActual() {
    return shiftStartTimeActual;
  }

  public void setShiftStartTimeActual(String shiftStartTimeActual) {
    this.shiftStartTimeActual = shiftStartTimeActual;
  }

  public String getShiftEndTimeActual() {
    return shiftEndTimeActual;
  }

  public void setShiftEndTimeActual(String shiftEndTimeActual) {
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

  public String getTempName() {
    return tempName;
  }

  public void setTempName(String tempName) {
    this.tempName = tempName;
  }
}
