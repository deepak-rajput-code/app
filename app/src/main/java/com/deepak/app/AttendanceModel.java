package com.deepak.app;
public class AttendanceModel {
    private final String date;
    private final String status;
    public AttendanceModel(String date, String status) {
        this.date = date;
        this.status = status;
    }
    public String getDate() {
        return date;
    }
    public String getStatus() {
        return status;
    }
}
