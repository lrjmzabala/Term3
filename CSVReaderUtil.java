package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CSVReaderUtil {
    public static final String EMPLOYEE_CSV = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";
    private static final Map<String, Employee> employeeCache = new HashMap<>(); // Consider updating this cache after modifications
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
        // This method can now directly use the cache after loadEmployeesToCache() is called
        // or re-read from CSV if the cache is not guaranteed to be up-to-date after CRUD operations.
        // For simplicity, let's make sure it reads from the latest CSV if not in cache.
        Employee emp = employeeCache.get(employeeId);
        if (emp != null) {
            return emp;
        } else {
            // Fallback to reading from CSV if not in cache (e.g., if cache not loaded yet or invalidated)
            return getEmployeeById(employeeId, EMPLOYEE_CSV);
        }
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
            System.err.println("Error reading employee file: " + e.getMessage()); // Use err for errors
        }

        System.out.println("No match found for Employee ID: " + employeeId);
        return null;
    }

    private static double parseDoubleSafely(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return 0.0;
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
        System.out.println("✅ Checking employee file path: " + employeeFile); // Use the parameter directly
        System.out.println("📂 Loading employee data...");
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(employeeFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                // System.out.println("Reading line: " + line); // Keep for debugging if needed
                String[] values = splitCSVLine(line);
                if (values.length >= 19) {
                    try { // Added a try-catch for individual row parsing
                        employees.add(new Employee(
                            values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(),
                            values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(),
                            values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim(),
                            values[12].trim(), parseDoubleSafely(values[13]), parseDoubleSafely(values[14]),
                            parseDoubleSafely(values[15]), parseDoubleSafely(values[16]),
                            parseDoubleSafely(values[17]), parseDoubleSafely(values[18])
                        ));
                    } catch (Exception e) {
                        System.err.println("Error parsing employee row: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.out.println("Warning: Skipping malformed row (not enough columns) → " + Arrays.toString(values));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employee file: " + e.getMessage());
            // Optionally, rethrow as a custom exception or handle more gracefully
        }
        System.out.println("Loaded " + employees.size() + " employees.");
        return employees;
    }

    public static List<Attendance> readAttendanceFromCSV(String attendanceFile) {
        List<Attendance> attendanceList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(attendanceFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = splitCSVLine(line);

                if (values.length >= 6) { // Ensure there are enough columns
                    try {
                        String employeeId = values[0].trim();
                        String lastname = values[1].trim();
                        String firstname = values[2].trim();
                        String date = values[3].trim();
                        String logIn = values[4].trim();
                        String logOut = values[5].trim();

                        attendanceList.add(new Attendance(employeeId, lastname, firstname, date, logIn, logOut));
                    } catch (Exception e) {
                        System.err.println("Warning: Skipping invalid attendance row → " + String.join(",", values) + " - " + e.getMessage());
                    }
                } else {
                    System.out.println("Warning: Skipping malformed attendance row → " + String.join(",", values));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance file: " + e.getMessage());
        }

        return attendanceList;
    }

    private static int parseIntegerSafely(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return 0;
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
     * This appends the new employee.
     * @param employee The Employee object to add.
     */
    public static void addEmployeeToCSV(Employee employee) {
        // This method should format the employee object into a CSV line
        // and append it to the file.
        try (FileWriter writer = new FileWriter(EMPLOYEE_CSV, true); // true for append mode
             PrintWriter out = new PrintWriter(writer)) { // Use PrintWriter for convenience

            // Create a formatted CSV line from the Employee object's fields
            String csvLine = String.join(",",
                employee.getEmployeeNumber(),
                escapeCSV(employee.getLastName()), // Escape commas/quotes in names/addresses
                escapeCSV(employee.getFirstName()),
                escapeCSV(employee.getBirthday()),
                escapeCSV(employee.getAddress()),
                escapeCSV(employee.getPhoneNumber()),
                escapeCSV(employee.getSssNumber()),
                escapeCSV(employee.getPhilhealthNumber()),
                escapeCSV(employee.getTinNumber()),
                escapeCSV(employee.getPagibigNumber()),
                escapeCSV(employee.getStatus()),
                escapeCSV(employee.getPosition()),
                escapeCSV(employee.getSupervisor()),
                String.valueOf(employee.getBasicSalary()), // Convert double to String
                String.valueOf(employee.getRiceSubsidy()),
                String.valueOf(employee.getPhoneAllowance()),
                String.valueOf(employee.getClothingAllowance()),
                String.valueOf(employee.getGrossSemiMonthlyRate()),
                String.valueOf(employee.getHourlyRate()) // Ensure this matches your Employee class field
            );
            out.println(csvLine); // Use println to add a new line

            System.out.println("✅ Employee added successfully to CSV: " + employee.getFullName());
            loadEmployeesToCache(); // Refresh cache after adding
        } catch (IOException e) {
            System.err.println("❌ Error writing to employee file: " + e.getMessage());
        }
    }

    /**
     * Updates an existing employee's record in the CSV file.
     * This requires rewriting the entire file.
     * @param updatedEmployee The Employee object with updated details.
     * @return true if employee was found and updated, false otherwise.
     */
    public static boolean updateEmployeeInCSV(Employee updatedEmployee) {
        List<Employee> allEmployees = readEmployeesFromCSV(EMPLOYEE_CSV);
        boolean found = false;

        for (int i = 0; i < allEmployees.size(); i++) {
            if (allEmployees.get(i).getEmployeeNumber().equals(updatedEmployee.getEmployeeNumber())) {
                allEmployees.set(i, updatedEmployee); // Replace the old employee object with the updated one
                found = true;
                break;
            }
        }

        if (found) {
            writeAllEmployeesToCSV(allEmployees);
            System.out.println("✅ Employee updated successfully in CSV: " + updatedEmployee.getFullName());
            loadEmployeesToCache(); // Refresh cache after updating
        } else {
            System.out.println("❌ Employee with ID " + updatedEmployee.getEmployeeNumber() + " not found for update.");
        }
        return found;
    }

    /**
     * Deletes an employee's record from the CSV file.
     * This requires rewriting the entire file excluding the deleted employee.
     * @param employeeNumber The employee number of the employee to delete.
     * @return true if employee was found and deleted, false otherwise.
     */
    public static boolean deleteEmployeeFromCSV(String employeeNumber) {
        List<Employee> allEmployees = readEmployeesFromCSV(EMPLOYEE_CSV);
        boolean removed = allEmployees.removeIf(emp -> emp.getEmployeeNumber().equals(employeeNumber));

        if (removed) {
            writeAllEmployeesToCSV(allEmployees);
            System.out.println("✅ Employee with ID " + employeeNumber + " deleted successfully from CSV.");
            loadEmployeesToCache(); // Refresh cache after deleting
        } else {
            System.out.println("❌ Employee with ID " + employeeNumber + " not found for deletion.");
        }
        return removed;
    }

    /**
     * Helper method to write a list of employees back to the CSV file.
     * This overwrites the existing file.
     * @param employees The list of employees to write.
     */
    private static void writeAllEmployeesToCSV(List<Employee> employees) {
        try (FileWriter writer = new FileWriter(EMPLOYEE_CSV, false); // false for overwrite mode
             PrintWriter out = new PrintWriter(writer)) {

            // Write header first
            out.println("Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-IBIG #,Status,Position,Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate");

            for (Employee emp : employees) {
                String csvLine = String.join(",",
                    emp.getEmployeeNumber(),
                    escapeCSV(emp.getLastName()),
                    escapeCSV(emp.getFirstName()),
                    escapeCSV(emp.getBirthday()),
                    escapeCSV(emp.getAddress()),
                    escapeCSV(emp.getPhoneNumber()),
                    escapeCSV(emp.getSssNumber()),
                    escapeCSV(emp.getPhilhealthNumber()),
                    escapeCSV(emp.getTinNumber()),
                    escapeCSV(emp.getPagibigNumber()),
                    escapeCSV(emp.getStatus()),
                    escapeCSV(emp.getPosition()),
                    escapeCSV(emp.getSupervisor()),
                    String.valueOf(emp.getBasicSalary()),
                    String.valueOf(emp.getRiceSubsidy()),
                    String.valueOf(emp.getPhoneAllowance()),
                    String.valueOf(emp.getClothingAllowance()),
                    String.valueOf(emp.getGrossSemiMonthlyRate()),
                    String.valueOf(emp.getHourlyRate())
                );
                out.println(csvLine);
            }
            System.out.println("CSV file overwritten successfully.");
        } catch (IOException e) {
            System.err.println("❌ Error writing all employees to file: " + e.getMessage());
        }
    }

    /**
     * Helper method to escape string values for CSV, handling commas and double quotes.
     * Encloses the field in double quotes if it contains a comma, double quote, or newline.
     * Doubles any existing double quotes within the field.
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        String escapedValue = value.replace("\"", "\"\""); // Escape internal quotes
        if (escapedValue.contains(",") || escapedValue.contains("\n") || escapedValue.contains("\"")) {
            return "\"" + escapedValue + "\""; // Enclose in quotes if it contains special characters
        }
        return escapedValue;
    }
}