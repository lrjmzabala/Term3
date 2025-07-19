package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.model.User;
import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.net.URL; // Added for resource loading

public class CSVReaderUtil {
    // FIX: These paths now match the renamed filenames in your 'resources' folder.
    public static final String EMPLOYEE_CSV_RESOURCE = "/employee_details.csv"; // CHANGED THIS LINE
    public static final String ATTENDANCE_CSV_RESOURCE = "/attendance_records.csv"; // CHANGED THIS LINE
    public static final String LOGIN_CREDENTIALS_CSV_RESOURCE = "/Login Credentials.csv";

    private static final Map<String, Employee> employeeCache = new HashMap<>();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");

    private static String writableEmployeeCsvPath;
    private static String writableAttendanceCsvPath;
    private static String writableLoginCredentialsCsvPath;

    static {
        initializeWritableCsvPaths();
        loadEmployeesToCache(); // Load employees from the writable path
    }

    public static void initializeWritableCsvPaths() {
        String userHome = System.getProperty("user.home");
        String appDir = userHome + File.separator + ".motorphpayroll"; // Hidden directory in user's home

        File appDirFile = new File(appDir);
        if (!appDirFile.exists()) {
            appDirFile.mkdirs(); // Create the directory if it doesn't exist
        }

        writableEmployeeCsvPath = appDir + File.separator + "employee_details.csv";
        writableAttendanceCsvPath = appDir + File.separator + "attendance_records.csv";
        writableLoginCredentialsCsvPath = appDir + File.separator + "login_credentials.csv";

        // Copy resources to writable paths if they don't exist
        copyResourceToFile(EMPLOYEE_CSV_RESOURCE, writableEmployeeCsvPath);
        copyResourceToFile(ATTENDANCE_CSV_RESOURCE, writableAttendanceCsvPath);
        copyResourceToFile(LOGIN_CREDENTIALS_CSV_RESOURCE, writableLoginCredentialsCsvPath);
    }

    private static void copyResourceToFile(String resourcePath, String filePath) {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            try (InputStream in = CSVReaderUtil.class.getResourceAsStream(resourcePath);
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                if (in == null) {
                    System.err.println("❌ Resource not found in JAR (classpath): " + resourcePath);
                    return;
                }
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("✅ Copied initial data from " + resourcePath + " to " + filePath);
            } catch (IOException e) {
                System.err.println("❌ Error copying resource " + resourcePath + " to " + filePath + ": " + e.getMessage());
            }
        } else {
            System.out.println("Existing data file found at: " + filePath + ". Not copying from resource.");
        }
    }

    /**
     * Reads user credentials from the "Login Credentials.csv" file.
     * The CSV is expected to have the format: username,password,role
     * Skips the first line assuming it's a header.
     *
     * @return A Map where the key is the username and the value is a User object.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static Map<String, User> readUsersFromLoginCSV() throws IOException {
        Map<String, User> userMap = new HashMap<>();
        // Use the writable path for reading login credentials
        try (BufferedReader reader = new BufferedReader(new FileReader(writableLoginCredentialsCsvPath))) {
            String line;
            boolean firstLine = true; // Flag to skip the header line

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip the header row
                    continue;
                }

                String[] parts = line.split(",");
                // Expecting 3 parts: username, password, role
                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim();
                    userMap.put(username, new User(username, password, role));
                } else {
                    System.err.println("Warning: Skipping malformed line in Login Credentials.csv: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("❌ Login Credentials file not found at " + writableLoginCredentialsCsvPath + ". An empty map will be returned. Error: " + e.getMessage());
            throw e; // Re-throw to inform the caller (LoginDialog)
        }
        return userMap;
    }

    public static String getWritableEmployeeCsvPath() {
        return writableEmployeeCsvPath;
    }

    public static String getWritableAttendanceCsvPath() {
        return writableAttendanceCsvPath;
    }
    public static String getWritableLoginCredentialsCsvPath() {
        return writableLoginCredentialsCsvPath;
    }


    public static void loadEmployeesToCache() {
        employeeCache.clear(); // Clear cache before reloading
        // Load from the writable path now
        List<Employee> employees = readEmployeesFromCSV(writableEmployeeCsvPath);
        for (Employee emp : employees) {
            employeeCache.put(emp.getEmployeeNumber(), emp);
        }
    }

    public static Employee getEmployeeById(String employeeId) {
        // Always try to get from cache first (which is loaded from writable path)
        Employee emp = employeeCache.get(employeeId);
        if (emp != null) {
            return emp;
        } else {
            // Fallback: If not in cache, it might mean the cache is stale or not fully loaded
            // In a robust app, ensure loadEmployeesToCache() is called when data might change.
            // For now, let's re-read from the writable CSV.
            return getEmployeeById(employeeId, writableEmployeeCsvPath);
        }
    }

    public static Employee getEmployeeById(String employeeId, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = splitCSVLine(line);

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
            System.err.println("Error reading employee file at " + filePath + ": " + e.getMessage());
        }

        System.out.println("No match found for Employee ID: " + employeeId + " in " + filePath);
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
     * @param filePath The path to the employee CSV file (now refers to the writable path).
     * @return A list of Employee objects parsed from the file.
     */
    public static List<Employee> readEmployeesFromCSV(String filePath) {
        System.out.println("✅ Checking employee file path: " + filePath);
        System.out.println("📂 Loading employee data...");
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = splitCSVLine(line);
                if (values.length >= 19) {
                    try {
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
        } catch (FileNotFoundException e) {
            System.err.println("❌ Employee file not found at " + filePath + ". It might be the first run or the file was deleted. An empty list will be returned.");
        }
        catch (IOException e) {
            System.err.println("Error reading employee file from " + filePath + ": " + e.getMessage());
        }
        System.out.println("Loaded " + employees.size() + " employees.");
        return employees;
    }

    public static List<Attendance> readAttendanceFromCSV(String filePath) {
        List<Attendance> attendanceList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = splitCSVLine(line);

                if (values.length >= 6) {
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
        } catch (FileNotFoundException e) {
            System.err.println("❌ Attendance file not found at " + filePath + ". An empty list will be returned.");
        }
        catch (IOException e) {
            System.err.println("Error reading attendance file from " + filePath + ": " + e.getMessage());
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
        try (FileWriter writer = new FileWriter(writableEmployeeCsvPath, true); // true for append mode
             PrintWriter out = new PrintWriter(writer)) {

            String csvLine = String.join(",",
                employee.getEmployeeNumber(),
                escapeCSV(employee.getLastName()),
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
                String.valueOf(employee.getBasicSalary()),
                String.valueOf(employee.getRiceSubsidy()),
                String.valueOf(employee.getPhoneAllowance()),
                String.valueOf(employee.getClothingAllowance()),
                String.valueOf(employee.getGrossSemiMonthlyRate()),
                String.valueOf(employee.getHourlyRate())
            );
            out.println(csvLine);

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
     * @return true if updated successfully, false otherwise.
     */
    public static boolean updateEmployeeInCSV(Employee updatedEmployee) {
        List<Employee> allEmployees = readEmployeesFromCSV(writableEmployeeCsvPath);
        boolean found = false;

        for (int i = 0; i < allEmployees.size(); i++) {
            if (allEmployees.get(i).getEmployeeNumber().equals(updatedEmployee.getEmployeeNumber())) {
                allEmployees.set(i, updatedEmployee);
                found = true;
                break;
            }
        }

        if (found) {
            writeAllEmployeesToCSV(allEmployees, writableEmployeeCsvPath); // Pass writable path
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
        List<Employee> allEmployees = readEmployeesFromCSV(writableEmployeeCsvPath);
        boolean removed = allEmployees.removeIf(emp -> emp.getEmployeeNumber().equals(employeeNumber));

        if (removed) {
            writeAllEmployeesToCSV(allEmployees, writableEmployeeCsvPath); // Pass writable path
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
     * @param filePath The path to write to (now dynamic).
     */
    private static void writeAllEmployeesToCSV(List<Employee> employees, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, false); // false for overwrite mode
             PrintWriter out = new PrintWriter(writer)) {

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
            System.out.println("CSV file overwritten successfully at: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error writing all employees to file " + filePath + ": " + e.getMessage());
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        String escapedValue = value.replace("\"", "\"\"");
        if (escapedValue.contains(",") || escapedValue.contains("\n") || escapedValue.contains("\"")) {
            return "\"" + escapedValue + "\"";
        }
        return escapedValue;
    }

    public static List<Attendance> getAllAttendanceRecords() {
        return readAttendanceFromCSV(writableAttendanceCsvPath);
    }
}