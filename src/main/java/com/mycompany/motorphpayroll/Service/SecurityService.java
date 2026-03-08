package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.model.Employee;
import java.util.List;

public class SecurityService {

    // Only HR roles see the Admin Panel
    public boolean canAccessAdmin(Employee user) {
        if (user == null) return false;
        String pos = user.getPosition().toUpperCase();
        return pos.contains("HR");
    }

    // Only those listed as an 'Immediate Supervisor' in the data see the Supervisor Portal
    public boolean isSupervisor(Employee loggedInUser, List<Employee> allEmployees) {
    // 1. Create a "normalized" search string from the user's name
    // Format: "Lastname,Firstname"
    String target = normalize(loggedInUser.getLastName() + "," + loggedInUser.getFirstName());
    
    for (Employee emp : allEmployees) {
        // 2. Normalize the supervisor name from the CSV
        String supervisor = normalize(emp.getSupervisor());
        
        // 3. Compare them
        if (supervisor.equalsIgnoreCase(target)) {
            return true;
        }
    }
    return false;
}

// This helper method is the key to solving the mismatch
private String normalize(String input) {
    if (input == null || input.equals("N/A")) return "";
    
    // Step A: Replace any space around a comma with just a comma
    // This turns "Mata, Christian" into "Mata,Christian"
    String result = input.replaceAll("\\s*,\\s*", ",");
    
    // Step B: Trim remaining whitespace and lower case it
    return result.trim().toLowerCase();
}
}