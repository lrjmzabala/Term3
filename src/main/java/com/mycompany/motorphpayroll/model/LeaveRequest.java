package com.mycompany.motorphpayroll.model;

public class LeaveRequest {
    private String employeeId, startDate, endDate, status; // "Pending", "Approved", "Denied"

    public LeaveRequest(String employeeId, String startDate, String endDate) {
        this.employeeId = employeeId; this.startDate = startDate; 
        this.endDate = endDate; this.status = "Pending";
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeId() { return employeeId; }
    // ... add getters for dates
}