package com.mycompany.motorphpayroll.model;

import java.util.List;

public interface ISupervisor {
    void approveLeave(String employeeId, String date);
    void denyLeave(String employeeId, String date);
    List<String[]> getSubordinateLeaveRequests(String supervisorName, List<Employee> allEmployees);
}