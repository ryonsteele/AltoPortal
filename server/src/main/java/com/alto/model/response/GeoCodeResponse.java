package com.alto.model.response;

public class GeoCodeResponse {

    private String place_id;
    private String licence;
    private String osm_type;
    private String osm_id;
    private String lat;
    private String lon;
    private String display_name;
    private String type;
    private float importance;


        // Getter Methods

        public String getPlace_id() {
            return place_id;
        }

        public String getLicence() {
            return licence;
        }

        public String getOsm_type() {
            return osm_type;
        }

        public String getOsm_id() {
            return osm_id;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        public String getDisplay_name() {
            return display_name;
        }


        public String getType() {
            return type;
        }

        public float getImportance() {
            return importance;
        }

        // Setter Methods

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public void setLicence(String licence) {
            this.licence = licence;
        }

        public void setOsm_type(String osm_type) {
            this.osm_type = osm_type;
        }

        public void setOsm_id(String osm_id) {
            this.osm_id = osm_id;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }


        public void setType(String type) {
            this.type = type;
        }

        public void setImportance(float importance) {
            this.importance = importance;
        }
    }
