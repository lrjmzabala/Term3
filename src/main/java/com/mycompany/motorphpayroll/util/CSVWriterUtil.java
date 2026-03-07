package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVWriterUtil {

    /**
     * Appends a new employee to the employee CSV.
     */
    public static void appendEmployeeToCSV(String filename, Employee emp) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            // Updated to match your Employee model structure
            writer.printf("%s,%s,%s,%.2f%n", 
                emp.getEmployeeNumber(), 
                emp.getFullName(), 
                emp.getPosition(), 
                emp.getHourlyRate());
        } catch (IOException e) {
            System.err.println("❌ Error appending employee: " + e.getMessage());
        }
    }

    /**
     * Appends a leave request to the leave records CSV.
     * Format: EmployeeID,StartDate,EndDate,Reason,Status
     */
    public static void appendLeaveRequest(String filename, String employeeId, Date start, Date end, String reason) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            String startDateStr = sdf.format(start);
            String endDateStr = sdf.format(end);
            
            // We use quotes for the reason in case the user types a comma
            writer.printf("%s,%s,%s,\"%s\",%s%n", 
                employeeId, 
                startDateStr, 
                endDateStr, 
                reason.replace("\"", "\"\""), // Escape quotes
                "PENDING");
                
            System.out.println("✅ Leave request saved for Employee #" + employeeId);
        } catch (IOException e) {
            System.err.println("❌ Error saving leave request: " + e.getMessage());
        }
    }
}