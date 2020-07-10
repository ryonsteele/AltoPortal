package com.alto.model;

import com.alto.model.response.ShiftResponse;

import java.util.List;

public class Historicals {

    private String hoursScheduled;
    private String hoursWorked;
    private String dateWindowBegin;
    private String dateWindowEnd;
    private List<ShiftResponse> shifts;

    public String getHoursScheduled() {
        return hoursScheduled;
    }

    public void setHoursScheduled(String hoursScheduled) {
        this.hoursScheduled = hoursScheduled;
    }

    public String getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getDateWindowBegin() {
        return dateWindowBegin;
    }

    public void setDateWindowBegin(String dateWindowBegin) {
        this.dateWindowBegin = dateWindowBegin;
    }

    public String getDateWindowEnd() {
        return dateWindowEnd;
    }

    public void setDateWindowEnd(String dateWindowEnd) {
        this.dateWindowEnd = dateWindowEnd;
    }

    public List<ShiftResponse> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftResponse> shifts) {
        this.shifts = shifts;
    }
}
