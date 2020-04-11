package com.alto.model.response;

public class ClientAddressResponse {

    private String clientId;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String county;



    // Getter Methods

    public String getClientId() {
        return clientId;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCounty() {
        return county;
    }

    // Setter Methods

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCounty(String county) {
        this.county = county;
    }


}
