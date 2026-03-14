package com.mycompany.motorphpayroll.model;

import com.mycompany.motorphpayroll.DAO.EmployeeDAO;
import java.io.IOException;

public class Admin extends Employee implements IAdmin {

    public Admin(String[] v) {
        // Unpack the array manually into the 19 arguments Employee expects
        super(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], 
              parseMoney(v[13]), parseMoney(v[14]), parseMoney(v[15]), 
              parseMoney(v[16]), parseMoney(v[17]), parseMoney(v[18]));
    }

    private static double parseMoney(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        return Double.parseDouble(value.replace(",", "").trim());
    }

    @Override
    public boolean canAccessModule(String m) {
        return m.equals("Admin") || m.equals("Attendance") || m.equals("Profile");
    }

    @Override
    public void deleteEmployee(String employeeId) {
        try {
            new EmployeeDAO().deleteEmployeeById(employeeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEmployeeRole(String employeeId, String newRole) {
        try {
            EmployeeDAO dao = new EmployeeDAO();
            // 1. Fetch the existing employee record
            Employee emp = dao.getEmployeeById(employeeId);
            
            if (emp != null) {
                // 2. Set the new role (assuming position field in Employee class)
                emp.setPosition(newRole);
                
                // 3. Persist the changes
                dao.updateEmployee(emp);
                System.out.println("Successfully updated role for ID: " + employeeId);
            } else {
                System.out.println("Employee ID " + employeeId + " not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}