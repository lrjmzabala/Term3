package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.model.User;
import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.net.URL; 

public class CSVReaderUtil {
    public static final String EMPLOYEE_CSV_RESOURCE = "/employee_details.csv";
    public static final String ATTENDANCE_CSV_RESOURCE = "/attendance_records.csv";
    public static final String LOGIN_CREDENTIALS_CSV_RESOURCE = "/Login Credentials.csv"; 

    private static final Map<String, Employee> employeeCache = new HashMap<>();
    // ADDED: Cache for users
    private static final Map<String, User> userCache = new HashMap<>();
    private static final Map<String, List<Attendance>> attendanceCache = new HashMap<>(); // Cache for attendance records, by employee ID

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");

    private static String writableEmployeeCsvPath;
    private static String writableAttendanceCsvPath;
    private static String writableLoginCredentialsCsvPath; // Path for the writable user file

    // Static initializer block: This code runs once when the class is loaded.
    static {
        initializeWritableCsvPaths(); // Set up paths and copy resources if needed
        loadAllDataToCache(); // Load all data into caches
    }

    // This method sets up the writable paths and copies initial CSVs from resources
    public static void initializeWritableCsvPaths() {
        String userHome = System.getProperty("user.home");
        String appDir = userHome + File.separator + ".motorphpayroll";

        File appDirFile = new File(appDir);
        if (!appDirFile.exists()) {
            appDirFile.mkdirs(); // Create the directory if it doesn't exist
        }

        writableEmployeeCsvPath = appDir + File.separator + "employee_details.csv";
        writableAttendanceCsvPath = appDir + File.separator + "attendance_records.csv";
        writableLoginCredentialsCsvPath = appDir + File.separator + "login_credentials.csv"; // Use a standardized name for the writable copy

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
     * Consolidates all data loading into a single method.
     * This should be called at application start or when data needs refreshing.
     */
    // ADDED: New method to load all data
    public static void loadAllDataToCache() {
        System.out.println("🔄 Loading all data to caches...");
        loadEmployeesToCache(); // Populates employeeCache
        loadUsersToCache();     // Populates userCache
        loadAttendanceToCache(); // Populates attendanceCache
        System.out.println("✅ All data loaded to caches.");
    }

    // MODIFIED: This method now populates the userCache
    public static void loadUsersToCache() {
        userCache.clear(); // Clear existing cache before reloading
        try (BufferedReader reader = new BufferedReader(new FileReader(writableLoginCredentialsCsvPath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header row
                }

                String[] parts = splitCSVLine(line); 
                if (parts.length >= 3) { 
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim();
                    userCache.put(username, new User(username, password, role));
                } else {
                    System.err.println("Warning: Skipping malformed line in login_credentials.csv: " + line);
                }
            }
            System.out.println("Loaded " + userCache.size() + " user credentials into cache.");
        } catch (FileNotFoundException e) {
            System.err.println("❌ Login Credentials file not found at " + writableLoginCredentialsCsvPath + ". User cache will be empty. Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Error reading login credentials file: " + e.getMessage());
        }
    }

    // ADDED: New method to get user by username from cache
    public static Optional<User> getUserByUsername(String username) {
        // Ensure cache is populated, although static block should handle it
        if (userCache.isEmpty() && new File(writableLoginCredentialsCsvPath).exists()) {
             // Only attempt to reload if cache is empty and file exists
             loadUsersToCache();
        }
        return Optional.ofNullable(userCache.get(username));
    }

    // Method to read users directly from CSV (if needed for AdminPanel, but generally use cache)
    // Kept for backward compatibility if other parts of the app rely on it returning a Map
    public static Map<String, User> readUsersFromLoginCSV() throws IOException {
        // This method will now simply return a copy of the userCache
        // or reload if necessary for specific use cases (e.g., if you only modify this file directly)
        loadUsersToCache(); // Ensure cache is up-to-date
        return new HashMap<>(userCache);
    }

    public static void addUserToLoginCSV(User user) throws IOException {
        try (FileWriter writer = new FileWriter(writableLoginCredentialsCsvPath, true); // true for append mode
             PrintWriter out = new PrintWriter(writer)) {
            String csvLine = String.join(",",
                escapeCSV(user.getUsername()),
                escapeCSV(user.getPassword()),
                escapeCSV(user.getRole())
            );
            out.println(csvLine);
            System.out.println("✅ User added successfully to login credentials: " + user.getUsername());
            loadUsersToCache(); // Reload cache after modifying file
        } catch (IOException e) {
            System.err.println("❌ Error writing user to login credentials file: " + e.getMessage());
            throw e;
        }
    }

    public static boolean updateUserInLoginCSV(User updatedUser) throws IOException {
        // Retrieve users from the current cache for modification
        List<User> allUsers = new ArrayList<>(userCache.values());
        boolean found = false;

        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(updatedUser.getUsername())) {
                allUsers.set(i, updatedUser); // Replace the old user with the updated one
                found = true;
                break;
            }
        }

        if (found) {
            writeAllUsersToLoginCSV(allUsers, writableLoginCredentialsCsvPath);
            System.out.println("✅ User updated successfully in login credentials: " + updatedUser.getUsername());
            loadUsersToCache(); // Reload cache after modifying file
        } else {
            System.out.println("❌ User " + updatedUser.getUsername() + " not found for update.");
        }
        return found;
    }

    public static void writeAllUsersToLoginCSV(List<User> users, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, false); // false for overwrite mode
             PrintWriter out = new PrintWriter(writer)) {
            out.println("Username,Password,Role"); // Write header
            for (User user : users) {
                String csvLine = String.join(",",
                    escapeCSV(user.getUsername()),
                    escapeCSV(user.getPassword()),
                    escapeCSV(user.getRole())
                );
                out.println(csvLine);
            }
            System.out.println("Login credentials CSV file overwritten successfully at: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error writing all users to login credentials file " + filePath + ": " + e.getMessage());
            throw e;
        }
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
        employeeCache.clear();
        List<Employee> employees = readEmployeesFromCSV(writableEmployeeCsvPath);
        for (Employee emp : employees) {
            employeeCache.put(emp.getEmployeeNumber(), emp);
        }
        System.out.println("Loaded " + employeeCache.size() + " employees into cache.");
    }

    // This method now also populates the attendanceCache
    public static void loadAttendanceToCache() {
        attendanceCache.clear(); // Clear existing cache before reloading
        List<Attendance> allAttendance = readAttendanceFromCSV(writableAttendanceCsvPath);
        for (Attendance att : allAttendance) {
            attendanceCache.computeIfAbsent(att.getEmployeeNumber(), k -> new ArrayList<>()).add(att);
        }
        System.out.println("Loaded " + allAttendance.size() + " attendance records into cache for " + attendanceCache.size() + " employees.");
    }

    //This method now gets from attendanceCache first
    public static List<Attendance> getAllAttendanceRecords() {
        // Return a new list containing all values from the attendance cache
        List<Attendance> allRecords = new ArrayList<>();
        attendanceCache.values().forEach(allRecords::addAll);
        return allRecords;
    }

    // MODIFIED: This method now gets from attendanceCache first
    public static Attendance getAttendanceRecord(String employeeNumber, String date) {
        List<Attendance> employeeAttendance = attendanceCache.get(employeeNumber);
        if (employeeAttendance != null) {
            for (Attendance att : employeeAttendance) {
                if (att.getDate().equals(date)) {
                    return att;
                }
            }
        }
        // If not found in cache, fallback to reading file (though ideally, cache should be definitive)
        return getAttendanceRecordFromFile(employeeNumber, date);
    }

    // ADDED: Private helper to read single attendance record from file (fallback)
    private static Attendance getAttendanceRecordFromFile(String employeeNumber, String date) {
        try (BufferedReader br = new BufferedReader(new FileReader(writableAttendanceCsvPath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = splitCSVLine(line);
                if (values.length >= 6) {
                    String currentEmpId = values[0].trim();
                    String currentDate = values[3].trim(); // Date is at index 3

                    if (currentEmpId.equals(employeeNumber) && currentDate.equals(date)) {
                        String lastname = values[1].trim();
                        String firstname = values[2].trim();
                        String logIn = values[4].trim();
                        String logOut = values[5].trim();
                        return new Attendance(employeeNumber, lastname, firstname, date, logIn, logOut);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("❌ Attendance file not found at " + writableAttendanceCsvPath + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Error reading attendance file: " + e.getMessage());
        }
        return null; // No record found
    }


    public static Employee getEmployeeById(String employeeId) {
        // Try to get from cache first
        Employee emp = employeeCache.get(employeeId);
        if (emp != null) {
            return emp;
        } else {
            // If not in cache, fallback to reading from file, then add to cache
            emp = getEmployeeByIdFromFile(employeeId, writableEmployeeCsvPath);
            if (emp != null) {
                employeeCache.put(emp.getEmployeeNumber(), emp); // Add to cache for future access
            }
            return emp;
        }
    }

    // ADDED: Private helper to read single employee from file (fallback)
    private static Employee getEmployeeByIdFromFile(String employeeId, String filePath) {
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

    public static void addEmployeeToCSV(Employee employee) throws IOException { // Add throws IOException
        try (FileWriter writer = new FileWriter(writableEmployeeCsvPath, true);
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
            loadEmployeesToCache(); // Reload cache after modifying file
        } catch (IOException e) {
            System.err.println("❌ Error writing to employee file: " + e.getMessage());
            throw e; // Re-throw to inform the caller (AdminPanel)
        }
    }

    public static boolean updateEmployeeInCSV(Employee updatedEmployee) {
        List<Employee> allEmployees = readEmployeesFromCSV(writableEmployeeCsvPath); // Reads from file for update logic
        boolean found = false;

        for (int i = 0; i < allEmployees.size(); i++) {
            if (allEmployees.get(i).getEmployeeNumber().equals(updatedEmployee.getEmployeeNumber())) {
                allEmployees.set(i, updatedEmployee);
                found = true;
                break;
            }
        }

        if (found) {
            writeAllEmployeesToCSV(allEmployees, writableEmployeeCsvPath);
            System.out.println("✅ Employee updated successfully in CSV: " + updatedEmployee.getFullName());
            loadEmployeesToCache(); // Reload cache after modifying file
        } else {
            System.out.println("❌ Employee with ID " + updatedEmployee.getEmployeeNumber() + " not found for update.");
        }
        return found;
    }

    public static boolean deleteEmployeeFromCSV(String employeeNumber) {
        List<Employee> allEmployees = readEmployeesFromCSV(writableEmployeeCsvPath);
        boolean removed = allEmployees.removeIf(emp -> emp.getEmployeeNumber().equals(employeeNumber));

        if (removed) {
            writeAllEmployeesToCSV(allEmployees, writableEmployeeCsvPath);
            System.out.println("✅ Employee with ID " + employeeNumber + " deleted successfully from CSV.");
            loadEmployeesToCache(); // Reload cache after modifying file
        } else {
            System.out.println("❌ Employee with ID " + employeeNumber + " not found for deletion.");
          
        }
        return removed;
    }

    private static void writeAllEmployeesToCSV(List<Employee> employees, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, false);
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

    public static void addAttendanceToCSV(Attendance attendance) throws IOException {
        try (FileWriter writer = new FileWriter(writableAttendanceCsvPath, true);
             PrintWriter out = new PrintWriter(writer)) {

            String csvLine = String.join(",",
                attendance.getEmployeeNumber(),
                escapeCSV(attendance.getLastName()),
                escapeCSV(attendance.getFirstName()),
                escapeCSV(attendance.getDate()),
                escapeCSV(attendance.getLogInTime()),
                escapeCSV(attendance.getLogOutTime())
            );
            out.println(csvLine);
            System.out.println("✅ Attendance record added: " + attendance.getEmployeeNumber() + " on " + attendance.getDate());
            loadAttendanceToCache(); // Reload cache after modifying file
        } catch (IOException e) {
            System.err.println("❌ Error writing new attendance record to file: " + e.getMessage());
            throw e;
        }
    }

    public static boolean updateAttendanceInCSV(Attendance updatedAttendance) throws IOException {
        List<Attendance> allAttendance = readAttendanceFromCSV(writableAttendanceCsvPath); // Reads from file for update logic
        boolean found = false;

        for (int i = 0; i < allAttendance.size(); i++) {
            Attendance existingAttendance = allAttendance.get(i);
            if (existingAttendance.getEmployeeNumber().equals(updatedAttendance.getEmployeeNumber()) &&
                existingAttendance.getDate().equals(updatedAttendance.getDate())) {
                allAttendance.set(i, updatedAttendance);
                found = true;
                break;
            }
        }

        if (found) {
            writeAllAttendanceToCSV(allAttendance, writableAttendanceCsvPath);
            System.out.println("✅ Attendance record updated: " + updatedAttendance.getEmployeeNumber() + " on " + updatedAttendance.getDate());
            loadAttendanceToCache(); // Reload cache after modifying file
        } else {
            System.out.println("❌ Attendance record for employee " + updatedAttendance.getEmployeeNumber() + " on " + updatedAttendance.getDate() + " not found for update.");
        }
        return found;
    }

    private static void writeAllAttendanceToCSV(List<Attendance> attendanceList, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, false);
             PrintWriter out = new PrintWriter(writer)) {

            out.println("Employee #,Last Name,First Name,Date,Time In,Time Out");

            for (Attendance att : attendanceList) {
                String csvLine = String.join(",",
                    att.getEmployeeNumber(),
                    escapeCSV(att.getLastName()),
                    escapeCSV(att.getFirstName()),
                    escapeCSV(att.getDate()),
                    escapeCSV(att.getLogInTime()),
                    escapeCSV(att.getLogOutTime())
                );
                out.println(csvLine);
            }
            System.out.println("Attendance CSV file overwritten successfully at: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error writing all attendance records to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }
}