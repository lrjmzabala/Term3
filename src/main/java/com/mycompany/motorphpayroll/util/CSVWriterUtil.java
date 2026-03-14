package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.mycompany.motorphpayroll.model.Employee;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterUtil {

    // ADD THIS METHOD - This is what AdminPanel needs to compile!
    public static void writeToCSV(String filename, String[] data) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                // Escape quotes and wrap in quotes to handle commas in addresses
                String field = (data[i] == null) ? "" : data[i].replace("\"", "\"\"");
                sb.append("\"").append(field).append("\"");
                if (i < data.length - 1) sb.append(",");
            }
            writer.println(sb.toString());
        }
    }

    // Keep your existing leave request logic
    public static void appendLeaveRequest(String filename, String employeeId, Date start, Date end, String reason) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    File file = new File(filename);
    boolean exists = file.exists();

    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
        // If the file is new, write the header first
        if (!exists) {
            writer.println("EmployeeID,StartDate,EndDate,Reason,Status");
        }

        String startDateStr = sdf.format(start);
        String endDateStr = sdf.format(end);
        
        // Format matches your Supervisor Portal columns: Emp ID, Start, End, Reason, Status
        writer.printf("%s,%s,%s,\"%s\",%s%n", 
            employeeId, 
            startDateStr, 
            endDateStr, 
            reason.replace("\"", "\"\""), 
            "PENDING");
            
    } catch (IOException e) {
        System.err.println("❌ Error: " + e.getMessage());
    }
}
    
    public static void rewriteCSV(String filename, List<Employee> employees) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
        for (Employee e : employees) {
            String[] data = {
                e.getEmployeeNumber(), e.getLastName(), e.getFirstName(), e.getBirthday(),
                e.getAddress(), e.getPhoneNumber(), e.getSssNumber(), e.getPhilhealthNumber(),
                e.getTinNumber(), e.getPagibigNumber(), e.getStatus(), e.getPosition(),
                e.getSupervisor(), 
                String.valueOf(e.getBasicSalary()),     // Must use String.valueOf
                String.valueOf(e.getRiceSubsidy()),     // Must use String.valueOf
                String.valueOf(e.getPhoneAllowance()),  // Must use String.valueOf
                String.valueOf(e.getClothingAllowance()),// Must use String.valueOf
                String.valueOf(e.getGrossSemiMonthlyRate()), 
                String.valueOf(e.getHourlyRate())
            };

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                String field = (data[i] == null) ? "" : data[i].replace("\"", "\"\"");
                sb.append("\"").append(field).append("\"");
                if (i < data.length - 1) sb.append(",");
            }
            writer.println(sb.toString());
        }
    }
}
    
    // You can keep appendEmployeeToCSV or delete it since writeToCSV handles it now
}