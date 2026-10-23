package com.jobtracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Application {
    private final int applicationId;
    private final String companyName;
    private final String position;
    private String status;
    private String applicationDate;
    private String notes;

    public Application(int applicationId, String companyName, String position,
                       String status, String applicationDate, String notes) {
        this.applicationId = applicationId;
        this.companyName = companyName;
        this.position = position;
        this.status = (status == null || status.isBlank()) ? "Applied" : status;
        this.applicationDate = (applicationDate == null || applicationDate.isBlank())
                ? LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : applicationDate;
        this.notes = notes == null ? "" : notes;
    }
    public int getApplicationId(){ return applicationId; }
    public String getCompanyName(){ return companyName; }
    public String getPosition(){ return position; }
    public String getStatus(){ return status; }
    public String getApplicationDate(){ return applicationDate; }
    public String getNotes(){ return notes; }
    public void updateStatus(String newStatus){ this.status = newStatus; }
    public void updateDetails(String companyName, String position, String status, String date, String notes) {
        this.status = status; this.applicationDate = date; this.notes = notes;
    }
    public void addNotes(String note){ if (notes.isEmpty()) notes = note; else notes += " " + note; }
}
