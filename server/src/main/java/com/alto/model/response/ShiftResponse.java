package com.alto.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "orderId",
        "status",
        "shiftStartTime",
        "shiftEndTime",
        "tempId",
        "firstName",
        "lastName",
        "clientId",
        "clientName",
        "regionName",
        "orderSpecialty",
        "orderCertification",
        "floor",
        "shiftNumber",
        "note",
        "payrollNumber",
        "lessLunchMin",
        "dateTimeCreated",
        "takenBy",
        "bookedByUserId",
        "orderTypeId",
        "orderType",
        "city",
        "state",
        "zipCode",
        "orderSourceID",
        "orderSourceName",
        "lt_orderID",
        "dateTimeModified",
        "subjectID",
        "subject",
        "vms"
})
public class ShiftResponse {

    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("shiftStartTime")
    private String shiftStartTime;
    @JsonProperty("shiftEndTime")
    private String shiftEndTime;
    @JsonProperty("tempId")
    private String tempId;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("clientName")
    private String clientName;
    @JsonProperty("regionName")
    private String regionName;
    @JsonProperty("orderSpecialty")
    private String orderSpecialty;
    @JsonProperty("orderCertification")
    private String orderCertification;
    @JsonProperty("floor")
    private String floor;
    @JsonProperty("shiftNumber")
    private String shiftNumber;
    @JsonProperty("note")
    private String note;
    @JsonProperty("payrollNumber")
    private String payrollNumber;
    @JsonProperty("lessLunchMin")
    private String lessLunchMin;
    @JsonProperty("dateTimeCreated")
    private String dateTimeCreated;
    @JsonProperty("takenBy")
    private String takenBy;
    @JsonProperty("bookedByUserId")
    private String bookedByUserId;
    @JsonProperty("orderTypeId")
    private String orderTypeId;
    @JsonProperty("orderType")
    private String orderType;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("zipCode")
    private String zipCode;
    @JsonProperty("orderSourceID")
    private String orderSourceID;
    @JsonProperty("orderSourceName")
    private String orderSourceName;
    @JsonProperty("lt_orderID")
    private String ltOrderID;
    @JsonProperty("dateTimeModified")
    private String dateTimeModified;
    @JsonProperty("subjectID")
    private String subjectID;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("vms")
    private String vms;

    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("shiftStartTime")
    public String getShiftStartTime() {
        return shiftStartTime;
    }

    @JsonProperty("shiftStartTime")
    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    @JsonProperty("shiftEndTime")
    public String getShiftEndTime() {
        return shiftEndTime;
    }

    @JsonProperty("shiftEndTime")
    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    @JsonProperty("tempId")
    public String getTempId() {
        return tempId;
    }

    @JsonProperty("tempId")
    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("clientId")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("clientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("clientName")
    public String getClientName() {
        return clientName;
    }

    @JsonProperty("clientName")
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @JsonProperty("regionName")
    public String getRegionName() {
        return regionName;
    }

    @JsonProperty("regionName")
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @JsonProperty("orderSpecialty")
    public String getOrderSpecialty() {
        return orderSpecialty;
    }

    @JsonProperty("orderSpecialty")
    public void setOrderSpecialty(String orderSpecialty) {
        this.orderSpecialty = orderSpecialty;
    }

    @JsonProperty("orderCertification")
    public String getOrderCertification() {
        return orderCertification;
    }

    @JsonProperty("orderCertification")
    public void setOrderCertification(String orderCertification) {
        this.orderCertification = orderCertification;
    }

    @JsonProperty("floor")
    public String getFloor() {
        return floor;
    }

    @JsonProperty("floor")
    public void setFloor(String floor) {
        this.floor = floor;
    }

    @JsonProperty("shiftNumber")
    public String getShiftNumber() {
        return shiftNumber;
    }

    @JsonProperty("shiftNumber")
    public void setShiftNumber(String shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    @JsonProperty("note")
    public void setNote(String note) {
        this.note = note;
    }

    @JsonProperty("payrollNumber")
    public String getPayrollNumber() {
        return payrollNumber;
    }

    @JsonProperty("payrollNumber")
    public void setPayrollNumber(String payrollNumber) {
        this.payrollNumber = payrollNumber;
    }

    @JsonProperty("lessLunchMin")
    public String getLessLunchMin() {
        return lessLunchMin;
    }

    @JsonProperty("lessLunchMin")
    public void setLessLunchMin(String lessLunchMin) {
        this.lessLunchMin = lessLunchMin;
    }

    @JsonProperty("dateTimeCreated")
    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    @JsonProperty("dateTimeCreated")
    public void setDateTimeCreated(String dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @JsonProperty("takenBy")
    public String getTakenBy() {
        return takenBy;
    }

    @JsonProperty("takenBy")
    public void setTakenBy(String takenBy) {
        this.takenBy = takenBy;
    }

    @JsonProperty("bookedByUserId")
    public String getBookedByUserId() {
        return bookedByUserId;
    }

    @JsonProperty("bookedByUserId")
    public void setBookedByUserId(String bookedByUserId) {
        this.bookedByUserId = bookedByUserId;
    }

    @JsonProperty("orderTypeId")
    public String getOrderTypeId() {
        return orderTypeId;
    }

    @JsonProperty("orderTypeId")
    public void setOrderTypeId(String orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    @JsonProperty("orderType")
    public String getOrderType() {
        return orderType;
    }

    @JsonProperty("orderType")
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("zipCode")
    public String getZipCode() {
        return zipCode;
    }

    @JsonProperty("zipCode")
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @JsonProperty("orderSourceID")
    public String getOrderSourceID() {
        return orderSourceID;
    }

    @JsonProperty("orderSourceID")
    public void setOrderSourceID(String orderSourceID) {
        this.orderSourceID = orderSourceID;
    }

    @JsonProperty("orderSourceName")
    public String getOrderSourceName() {
        return orderSourceName;
    }

    @JsonProperty("orderSourceName")
    public void setOrderSourceName(String orderSourceName) {
        this.orderSourceName = orderSourceName;
    }

    @JsonProperty("lt_orderID")
    public String getLtOrderID() {
        return ltOrderID;
    }

    @JsonProperty("lt_orderID")
    public void setLtOrderID(String ltOrderID) {
        this.ltOrderID = ltOrderID;
    }

    @JsonProperty("dateTimeModified")
    public String getDateTimeModified() {
        return dateTimeModified;
    }

    @JsonProperty("dateTimeModified")
    public void setDateTimeModified(String dateTimeModified) {
        this.dateTimeModified = dateTimeModified;
    }

    @JsonProperty("subjectID")
    public String getSubjectID() {
        return subjectID;
    }

    @JsonProperty("subjectID")
    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    @JsonProperty("subject")
    public String getSubject() {
        return subject;
    }

    @JsonProperty("subject")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("vms")
    public String getVms() {
        return vms;
    }

    @JsonProperty("vms")
    public void setVms(String vms) {
        this.vms = vms;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("orderId", orderId).append("status", status).append("shiftStartTime", shiftStartTime).append("shiftEndTime", shiftEndTime).append("tempId", tempId).append("firstName", firstName).append("lastName", lastName).append("clientId", clientId).append("clientName", clientName).append("regionName", regionName).append("orderSpecialty", orderSpecialty).append("orderCertification", orderCertification).append("floor", floor).append("shiftNumber", shiftNumber).append("note", note).append("payrollNumber", payrollNumber).append("lessLunchMin", lessLunchMin).append("dateTimeCreated", dateTimeCreated).append("takenBy", takenBy).append("bookedByUserId", bookedByUserId).append("orderTypeId", orderTypeId).append("orderType", orderType).append("city", city).append("state", state).append("zipCode", zipCode).append("orderSourceID", orderSourceID).append("orderSourceName", orderSourceName).append("ltOrderID", ltOrderID).append("dateTimeModified", dateTimeModified).append("subjectID", subjectID).append("subject", subject).append("vms", vms).toString();
    }

}
