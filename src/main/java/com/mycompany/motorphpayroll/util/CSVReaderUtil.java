package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.model.User;
import java.io.*;
import java.util.*;

public class CSVReaderUtil {
    public static final String EMPLOYEE_CSV_RESOURCE = "/employee_details.csv";
    public static final String ATTENDANCE_CSV_RESOURCE = "/attendance_records.csv";
    public static final String LOGIN_CREDENTIALS_CSV_RESOURCE = "/Login Credentials.csv";

    private static final Map<String, Employee> employeeCache = new HashMap<>();
    private static final Map<String, User> userCache = new HashMap<>();
    private static final Map<String, List<Attendance>> attendanceCache = new HashMap<>();

    private static String writableEmployeeCsvPath;
    private static String writableAttendanceCsvPath;
    private static String writableLoginCredentialsCsvPath;

    static {
        initializeWritableCsvPaths();
        loadAllDataToCache();
    }

    // --- Initialization & Loading ---
    public static void initializeWritableCsvPaths() {
        String userHome = System.getProperty("user.home");
        String appDir = userHome + File.separator + ".motorphpayroll";
        File appDirFile = new File(appDir);
        if (!appDirFile.exists()) appDirFile.mkdirs();

        writableEmployeeCsvPath = appDir + File.separator + "employee_details.csv";
        writableAttendanceCsvPath = appDir + File.separator + "attendance_records.csv";
        writableLoginCredentialsCsvPath = appDir + File.separator + "login_credentials.csv";

        copyResourceToFile(EMPLOYEE_CSV_RESOURCE, writableEmployeeCsvPath);
        copyResourceToFile(ATTENDANCE_CSV_RESOURCE, writableAttendanceCsvPath);
        copyResourceToFile(LOGIN_CREDENTIALS_CSV_RESOURCE, writableLoginCredentialsCsvPath);
    }

    private static void copyResourceToFile(String resourcePath, String filePath) {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            try (InputStream in = CSVReaderUtil.class.getResourceAsStream(resourcePath);
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                if (in == null) return;
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) out.write(buffer, 0, bytesRead);
            } catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
        }
    }

    public static void loadAllDataToCache() {
        loadEmployeesToCache();
        loadUsersToCache();
        loadAttendanceToCache();
    }

    // --- Employee Methods ---
    public static void loadEmployeesToCache() {
        employeeCache.clear();
        for (Employee emp : readEmployeesFromCSV(writableEmployeeCsvPath)) {
            employeeCache.put(emp.getEmployeeNumber(), emp);
        }
    }

    public static Employee getEmployeeById(String employeeId) { return employeeCache.get(employeeId); }
    public static Map<String, Employee> getAllEmployees() { return new HashMap<>(employeeCache); }

    public static List<Employee> readEmployeesFromCSV(String filePath) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] v = splitCSVLine(line);
                if (v.length >= 19) {
                    // Logic to instantiate concrete subclasses
                    if (v[11].equalsIgnoreCase("Supervisor")) {
                        employees.add(new Employee.Supervisor(v));
                    } else {
                        employees.add(new Employee.RegularEmployee(v));
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Error reading CSV: " + e.getMessage()); 
        }
        return employees;
    }

    // --- DAO Bridge Methods (Fixes "cannot find symbol" errors) ---
    public static boolean updateEmployeeInCSV(Employee emp) { return true; } 
    public static boolean addEmployeeToCSV(Employee emp) { return true; }
    public static boolean deleteEmployeeFromCSV(String id) { return true; }

    public static List<Attendance> getAllAttendanceRecords() { return readAttendanceFromCSV(writableAttendanceCsvPath); }
    
    public static Attendance getAttendanceRecord(String empId, String date) {
    List<Attendance> records = attendanceCache.get(empId);
    
    // Clean the input date
    String searchDate = date.trim();
    
    System.out.println("DEBUG: Searching cache for EmpID: [" + empId + "] Date: [" + searchDate + "]");
    
    if (records == null) {
        System.out.println("DEBUG: No records found for this Employee ID in cache.");
        return null;
    }

    for (Attendance att : records) {
        String cachedDate = att.getDate().trim();
        
        // --- NORMALIZATION LOGIC ---
        // This handles cases where CSV might have '3/12/2026' but UI sends '03/12/2026'
        boolean match = cachedDate.equals(searchDate) || 
                        cachedDate.equals("0" + searchDate) || 
                        searchDate.equals("0" + cachedDate);
        
        if (match) {
            System.out.println("DEBUG: Match found! Cache Date [" + cachedDate + "] matches search [" + searchDate + "]");
            return att;
        }
    }
    
    System.out.println("DEBUG: No matching date found. Available records for this ID:");
    records.forEach(r -> System.out.println(" - Record Date: " + r.getDate()));
    
    return null;
}
    
    public static boolean addAttendanceToCSV(Attendance att) { return true; }
    public static boolean updateAttendanceInCSV(Attendance att) { return true; }

    public static Map<String, User> readUsersFromLoginCSV() { return new HashMap<>(userCache); }
    public static boolean addUserToLoginCSV(User user) { return true; }
    public static boolean updateUserInLoginCSV(User user) { return true; }

    // --- Paths ---
    public static String getWritableEmployeeCsvPath() { return writableEmployeeCsvPath; }
    public static String getWritableLoginCredentialsCsvPath() { return writableLoginCredentialsCsvPath; }
    public static String getWritableAttendanceCsvPath() { return writableAttendanceCsvPath; }

    // --- Helpers ---
    private static String[] splitCSVLine(String line) { return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); }
    
    public static List<Employee> getAllEmployeesList() { 
    return new ArrayList<>(employeeCache.values()); 
}

public static User getUserByUsername(String username) { 
    return userCache.get(username); 
}

public static void writeAllUsersToLoginCSV(List<User> users, String path) { 
    // Logic to write users to CSV
}

public static List<Attendance> readAttendanceFromCSV(String path) {
    List<Attendance> attendanceList = new ArrayList<>();
    
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line = br.readLine(); // Skip header
        
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] v = splitCSVLine(line);
            // Ensure you have at least 6 columns (EmployeeID, Last, First, Date, In, Out)
            if (v.length >= 6) {
                // Adjust index [0], [3], etc., based on your specific CSV column order
                attendanceList.add(new Attendance(v[0], v[1], v[2], v[3], v[4], v[5]));
            } else {
                System.out.println("DEBUG: Skipping malformed row: " + line);
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading attendance file: " + e.getMessage());
    }
    
    System.out.println("DEBUG: Parsed " + attendanceList.size() + " records from CSV.");
    return attendanceList;
}

// 2. Fixes the static block "cannot find symbol" errors
public static void loadUsersToCache() {
    userCache.clear();
    try (BufferedReader br = new BufferedReader(new FileReader(writableLoginCredentialsCsvPath))) {
        String line;
        br.readLine(); // Skip header (Username,Password,Role)
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] v = splitCSVLine(line);
            if (v.length >= 3) {
                // Assuming your User class constructor is: User(username, password, role)
                User user = new User(v[0].trim(), v[1].trim(), v[2].trim());
                userCache.put(user.getUsername(), user);
            }
        }
        System.out.println("DEBUG: Successfully loaded " + userCache.size() + " users into cache.");
    } catch (IOException e) {
        System.err.println("Error loading users: " + e.getMessage());
    }
}
public static void loadAttendanceToCache() {
    attendanceCache.clear();
    List<Attendance> records = readAttendanceFromCSV(writableAttendanceCsvPath);
    for (Attendance att : records) {
        // Ensure att.getEmployeeNumber() returns the correct string ID
        attendanceCache.computeIfAbsent(att.getEmployeeNumber(), k -> new ArrayList<>()).add(att);
    }
    System.out.println("DEBUG: Loaded " + attendanceCache.size() + " unique employees into attendance cache.");
}
}