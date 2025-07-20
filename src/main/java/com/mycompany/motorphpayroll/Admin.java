package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.model.Employee;
import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Admin {
    private String employeeCsvFilePath = "src/main/resources/MotorPH Employee Data.csv"; 

    public Map<String, Employee> loadEmployees() {
        Map<String, Employee> employees = new HashMap<>();
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(employeeCsvFilePath))) {
            br.readLine(); // Skip header line if present

            while ((line = br.readLine()) != null) { 
                String[] data = line.split(cvsSplitBy);
                
                if (data.length >= 20) {
                    try {
                        String employeeNumber = data[0].trim();
                        String lastName = data[1].trim();
                        String firstName = data[2].trim();
                        String birthday = data[3].trim(); 
                        String address = data[4].trim();
                        String phoneNumber = data[5].trim();
                        String sssNumber = data[6].trim();
                        String philhealthNumber = data[7].trim();
                        String tinNumber = data[8].trim();
                        String pagibigNumber = data[9].trim();
                        String status = data[10].trim();
                        String position = data[11].trim();
                        String supervisor = data[12].trim();
                        double basicSalary = Double.parseDouble(data[13].trim());
                        double riceSubsidy = Double.parseDouble(data[14].trim());
                        double phoneAllowance = Double.parseDouble(data[15].trim());
                        double clothingAllowance = Double.parseDouble(data[16].trim());
                        double grossSemiMonthlyRate = Double.parseDouble(data[17].trim());
                        double hourlyRate = Double.parseDouble(data[18].trim()); 

                        Employee employee = new Employee(employeeNumber, lastName, firstName, birthday, address,
                                phoneNumber, sssNumber, philhealthNumber, tinNumber, pagibigNumber,
                                status, position, supervisor, basicSalary, riceSubsidy, phoneAllowance,
                                clothingAllowance, grossSemiMonthlyRate, hourlyRate);
                        employees.put(employeeNumber, employee);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numeric data for employee: " + data[0] + ". Details: " + e.getMessage());
                        // Handle error, e.g., skip this line or log it
                    }
                } else {
                    System.err.println("Skipping malformed line in CSV: " + line);
                }
            }
        } catch (IOException e) { // This catches the IOException
            System.err.println("Error reading employee data file: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for more details
           
            // JOptionPane.showMessageDialog(null, "Error loading employee data: " + e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);
        }
        return employees;
    }

}