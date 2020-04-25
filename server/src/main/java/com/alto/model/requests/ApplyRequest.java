package com.alto.model.requests;

import java.util.ArrayList;

public class ApplyRequest {

    private String firstname;
    private String email;
    private String lastname;
    private String street;
    private String city;
    private String state;
    private String zip;
    ArrayList< String > certs = new ArrayList < String > ();
    ArrayList < String > specs = new ArrayList < String > ();
    private String primary;
    private String secondary;
    private String filekey;


    // Getter Methods

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public String getLastname() {
        return lastname;
    }

    public String getStreet() {
        return street;
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

    public String getPrimary() {
        return primary;
    }

    public String getSecondary() {
        return secondary;
    }

    // Setter Methods

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public ArrayList<String> getCerts() {
        return certs;
    }

    public void setCerts(ArrayList<String> certs) {
        this.certs = certs;
    }

    public ArrayList<String> getSpecs() {
        return specs;
    }

    public void setSpecs(ArrayList<String> specs) {
        this.specs = specs;
    }

    public String getFilekey() {
        return filekey;
    }

    public void setFilekey(String filekey) {
        this.filekey = filekey;
    }
}
