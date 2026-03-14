package com.mycompany.motorphpayroll.model;

public interface IAdmin {
    void deleteEmployee(String employeeId);
    void updateEmployeeRole(String employeeId, String newRole);
}