package com.mycompany.motorphpayroll.service;
import com.mycompany.motorphpayroll.model.Employee;

public class SecurityService {
    public boolean isHR(Employee emp) {
        return emp != null && "HR".equalsIgnoreCase(emp.getPosition());
    }
}