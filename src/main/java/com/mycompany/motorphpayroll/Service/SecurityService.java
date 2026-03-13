package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import java.util.List;

public class SecurityService {

    // HR Access: Admin Panel
    public boolean canAccessAdmin(Employee user) {
        if (user == null) return false;
        return user.getPosition().toUpperCase().contains("HR");
    }

    // Finance Access: Edit Salary
    public boolean canEditSalary(Employee user) {
        if (user == null) return false;
        return user.getPosition().toUpperCase().contains("FINANCE");
    }

    // IT Access: Create/Update Logins
    public boolean canManageLogins(Employee user) {
        if (user == null) return false;
        return user.getPosition().toUpperCase().contains("IT");
    }

    // Supervisor Access: Approve Leave
    public boolean isSupervisor(Employee loggedInUser, List<Employee> allEmployees) {
        if (loggedInUser == null) return false;
        String target = normalize(loggedInUser.getLastName() + "," + loggedInUser.getFirstName());
        
        for (Employee emp : allEmployees) {
            String supervisor = normalize(emp.getSupervisor());
            if (!supervisor.isEmpty() && supervisor.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

// This helper method is the key to solving the mismatch
private String normalize(String input) {
    if (input == null || input.equals("N/A")) return "";
    
    // 1. Remove all double quotes
    String cleaned = input.replace("\"", "");
    
    // 2. Replace any space around a comma with just a comma
    // This turns "Mata, Christian" into "Mata,Christian"
    String result = cleaned.replaceAll("\\s*,\\s*", ",");
    
    // 3. Trim remaining whitespace and lower case it
    return result.trim().toLowerCase();
}
}