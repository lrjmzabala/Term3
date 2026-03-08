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
    public boolean isSupervisor(Employee user, List<Employee> allEmployees) {
        if (user == null || allEmployees == null) return false;

        // Build target: "mata, christian"
        String target = (user.getLastName().trim() + ", " + user.getFirstName().trim()).toLowerCase();
        
        System.out.println("DEBUG: Searching for supervisor: [" + target + "]");

        return allEmployees.stream().anyMatch(emp -> {
            String sup = emp.getImmediateSupervisor();
            if (sup == null || sup.equalsIgnoreCase("N/A")) return false;

            String cleanSup = sup.trim().toLowerCase();
            
            // Check if match exists
            if (cleanSup.contains(target.replace(" ", ""))) {
                System.out.println("DEBUG: MATCH FOUND! Employee [" + emp.getLastName() + "] lists [" + sup + "] as supervisor.");
                return true;
            }
            return false;
        });
    }
}