package com.alto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "healthcaresource")
public class HCSConfiguration {

    private String username;
    private String password;
    private String baseurl;
    private String locationUrl;
    private String droidFCMKey;





    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getDroidFCMKey() {
        return droidFCMKey;
    }

    public void setDroidFCMKey(String droidFCMKey) {
        this.droidFCMKey = droidFCMKey;
    }
}
