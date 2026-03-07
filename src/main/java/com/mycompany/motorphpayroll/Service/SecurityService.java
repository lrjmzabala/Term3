package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import java.util.List;

public class SecurityService {

    private String immediateSupervisor;
    
    // Check if the current user is an Admin/HR
    public boolean canAccessAdmin(Employee user) {
        if (user == null) return false;
        String pos = user.getPosition().toUpperCase();
        return pos.contains("HR") || pos.contains("CHIEF");
    }

    // Check if the current user is a Supervisor
    public boolean isSupervisor(Employee user, List<Employee> allEmployees) {
        if (user == null) return false;
        String fullName = user.getLastName() + ", " + user.getFirstName();
        
        // If this user's name appears in any other employee's "Immediate Supervisor" field
        return allEmployees.stream()
                .anyMatch(emp -> fullName.equalsIgnoreCase(emp.getImmediateSupervisor()));
    }
    
    public String getImmediateSupervisor() {
    return this.immediateSupervisor; // Make sure your variable name matches CSV header
}
}