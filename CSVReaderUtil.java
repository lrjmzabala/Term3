package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReaderUtil {
    private static final String EMPLOYEE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";
    private static final Map<String, Employee> employeeCache = new HashMap<>();
    private static final String ATTENDANCE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee DataHoursWorked - Employee Details.csv";
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
    
    public static void loadEmployeesToCache() {
    employeeCache.clear(); // Clear cache before reloading
    List<Employee> employees = readEmployeesFromCSV(EMPLOYEE_CSV);
    for (Employee emp : employees) {
        employeeCache.put(emp.getEmployeeNumber(), emp);
    }
}
    
    public static Employee getEmployeeById(String employeeId) {
        return employeeCache.getOrDefault(employeeId, null);
    }

    public static Employee getEmployeeById(String employeeId, String filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String line;
        br.readLine(); // Skip header
        
       while ((line = br.readLine()) != null) {
            String[] values = splitCSVLine(line);
            
            // Ensure correct number of fields and exact ID match
            if (values.length >= 19 && values[0].trim().equals(employeeId.trim())) {
                return new Employee(
                    values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(),
                    values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(),
                    values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim(),
                    values[12].trim(), parseDoubleSafely(values[13]), parseDoubleSafely(values[14]),
                    parseDoubleSafely(values[15]), parseDoubleSafely(values[16]), 
                    parseDoubleSafely(values[17]), parseDoubleSafely(values[18])
                );
            }
        }
    } catch (IOException e) {
        System.out.println("Error reading employee file: " + e.getMessage());
    }
    
    System.out.println("No match found for Employee ID: " + employeeId);
    return null;
}

    private static double parseDoubleSafely(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return 0.0; // Handle empty values
        return Double.parseDouble(value.replace(",", "").replaceAll("\"", "").trim());
    } catch (NumberFormatException e) {
        System.out.println("Warning: Invalid number format → '" + value + "' (defaulting to 0.0)");
        return 0.0;
        }
    }

    
    /**
 * Reads all employees from the CSV file and stores them in a list.
 * 
 * @param employeeFile The path to the employee CSV file.
 * @return A list of Employee objects parsed from the file.
 */
    
    public static List<Employee> readEmployeesFromCSV(String employeeFile) {
        System.out.println("✅ Checking employee file path: " + EMPLOYEE_CSV);
        System.out.println("📂 Loading employee data...");
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                System.out.println("Reading line: " + line);
                String[] values = splitCSVLine(line);
                if (values.length >= 19) {
                    employees.add(new Employee(
                        values[0].trim(),
                        values[1].trim(),
                        values[2].trim(),
                        values[3].trim(),
                        values[4].trim(),
                        values[5].trim(),
                        values[6].trim(),
                        values[7].trim(),
                        values[8].trim(),
                        values[9].trim(),
                        values[10].trim(),
                        values[11].trim(),
                        values[12].trim(),
                        parseDoubleSafely(values[13]),
                        parseDoubleSafely(values[14]),
                        parseDoubleSafely(values[15]),
                        parseDoubleSafely(values[16]),
                        parseDoubleSafely(values[17]),
                        parseDoubleSafely(values[18])
                    ));
                } else {
                    System.out.println("Warning: Skipping malformed row → " + Arrays.toString(values));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading employee file: " + e.getMessage());
        }

        return employees;
    }

    public static List<Attendance> readAttendanceFromCSV(String attendanceFile) {
    List<Attendance> attendanceList = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(attendanceFile))) {
        String line;
        br.readLine(); // Skip header

        while ((line = br.readLine()) != null) {
            // ✅ Ensure `values` is assigned
            String[] values = splitCSVLine(line);  

            if (values.length >= 6) {  // Ensure there are enough columns
                try {
                    String employeeId = values[0].trim();
                    String lastname = values[1].trim();
                    String firstname = values[2].trim();
                    String date = values[3].trim();
                    String logIn = values[4].trim();
                    String logOut = values[5].trim();

                    attendanceList.add(new Attendance(employeeId, lastname, firstname, date, logIn, logOut));
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid row → " + String.join(",", values));
                }
            } else {
                System.out.println("Warning: Skipping malformed row → " + String.join(",", values));
            }
        }
    } catch (IOException e) {
        System.out.println("Error reading attendance file: " + e.getMessage());
    }

    return attendanceList;

    }

    private static int parseIntegerSafely(String value) {
        try {
            return Integer.parseInt(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            System.out.println("Warning: Invalid number format → '" + value + "' (defaulting to 0)");
            return 0;
        }
    }

    private static String[] splitCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());

        return result.toArray(new String[0]);
    }
    
    /**
     * Adds a new employee to the CSV file.
     */
    public static void addEmployeeToCSV(Employee employee) {
        try (FileWriter writer = new FileWriter(EMPLOYEE_CSV, true)) {
            String newEmployee = String.join(",", employee.getEmployeeNumber(), 
                                              employee.getFirstName(), 
                                              employee.getLastName(), 
                                              "", "", "", "", 
                                              String.valueOf(employee.getHourlyRate()));
            writer.append(newEmployee).append("\n");
            System.out.println("✅ Employee added successfully: " + employee.getFullName());
        } catch (IOException e) {
            System.err.println("❌ Error writing to employee file: " + e.getMessage());
        }
    }
    
    
}