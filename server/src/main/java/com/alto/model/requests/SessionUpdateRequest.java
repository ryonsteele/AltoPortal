package com.alto.model.requests;

public class SessionUpdateRequest {

    private String shiftstart;
    private String shiftend;
    private String orderid;


    // Getter Methods

    public String getShiftstart() {
        return shiftstart;
    }

    public String getShiftend() {
        return shiftend;
    }

    public String getOrderid() {
        return orderid;
    }

    // Setter Methods

    public void setShiftstart(String shiftstart) {
        this.shiftstart = shiftstart;
    }

    public void setShiftend(String shiftend) {
        this.shiftend = shiftend;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}
