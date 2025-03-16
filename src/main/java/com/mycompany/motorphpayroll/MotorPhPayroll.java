package com.mycompany.motorphpayroll;

import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import com.mycompany.motorphpayroll.util.PayrollCalculator;
import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.model.Employee;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MotorPhPayroll {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Login as:\n1. Admin\n2. Employee\n3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (choice == 1) {
                adminMenu(scanner);
            } else if (choice == 2) {
                employeeMenu(scanner);
            } else if (choice == 3) {
                System.out.println("Exiting... Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void adminMenu(Scanner scanner) {
        Admin adminPanel = new Admin(); // Initialize Admin instance

        while (true) {
            System.out.println("\nAdmin Panel:");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employee Details");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");
            int adminChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (adminChoice == 1) {
                System.out.print("Enter Employee Number: ");
                String empNum = scanner.nextLine();
                System.out.print("Enter First Name: ");
                String firstName = scanner.nextLine();
                System.out.print("Enter Last Name: ");
                String lastName = scanner.nextLine();
                System.out.print("Enter Date of Birth (MM-DD-YYYY): ");
                String dob = scanner.nextLine();
                System.out.print("Enter Address: ");
                String address = scanner.nextLine();
                System.out.print("Enter Phone Number: ");
                String phoneNumber = scanner.nextLine();
                System.out.print("Enter SSS Number: ");
                String sss = scanner.nextLine();
                System.out.print("Enter PhilHealth Number: ");
                String philhealth = scanner.nextLine();
                System.out.print("Enter TIN (Tax Identification Number): ");
                String tin = scanner.nextLine();
                System.out.print("Enter Pag-IBIG Number: ");
                String pagIbig = scanner.nextLine();
                System.out.print("Enter Employment Status: ");
                String status = scanner.nextLine();
                System.out.print("Enter Position: ");
                String position = scanner.nextLine();
                System.out.print("Enter Supervisor Name: ");
                String supervisor = scanner.nextLine();
                System.out.print("Enter Basic Salary: ");
                double basicSalary = scanner.nextDouble();
                System.out.print("Enter Rice Subsidy: ");
                double riceSubsidy = scanner.nextDouble();
                System.out.print("Enter Phone Allowance: ");
                double phoneAllowance = scanner.nextDouble();
                System.out.print("Enter Clothing Allowance: ");
                double clothingAllowance = scanner.nextDouble();
                System.out.print("Enter Gross Semi-Monthly Salary: ");
                double grossSemimonthly = scanner.nextDouble();
                System.out.print("Enter Hourly Rate: ");
                double hourlyRate = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                
                adminPanel.addEmployee(empNum, firstName, lastName, dob, address, phoneNumber, sss, philhealth, tin, pagIbig, status, position, supervisor, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemimonthly, hourlyRate);
                System.out.println("✅ Employee added successfully!");
                
            } else if (adminChoice == 2) {
                displayEmployees();
            } else if (adminChoice == 3) {
                return; // Go back to the main menu
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayEmployees() {
        String employeeFile = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee Data - Employee Details.csv";
        List<Employee> employees = CSVReaderUtil.readEmployeesFromCSV(employeeFile);

        System.out.println("\nEmployee Details:\n");
        for (Employee emp : employees) {
            System.out.println(emp);
            System.out.println("----------------------");
        }
    }

    private static void employeeMenu(Scanner scanner) {
        System.out.print("\nEnter your Employee Number: ");
        String empNum = scanner.nextLine();

        // ✅ Load employee details first
        Employee employee = CSVReaderUtil.getEmployeeById(empNum);
        if (employee == null) {
            System.out.println("⚠ Employee not found.");
            return;
        }

        while (true) {
            System.out.println("\nEmployee Menu:");
            System.out.println("1. View Employee Details");
            System.out.println("2. View Salary");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");
            int empChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (empChoice == 1) {
                // ✅ Display ONLY Employee details (without attendance records)
                System.out.println("\n👤 Employee Details:");
                System.out.println(employee);

            } else if (empChoice == 2) {
                // ✅ Load attendance records ONLY for this employee
                String attendanceFile = "C:\\Users\\Papa\\Downloads\\Copy of MotorPH Employee DataHoursWorked - Employee Details.csv";
                List<Attendance> allAttendanceRecords = CSVReaderUtil.readAttendanceFromCSV(attendanceFile);
                
                // ✅ Filter attendance **before** using it
                List<Attendance> employeeAttendance = allAttendanceRecords.stream()
                    .filter(a -> a.getEmployeeNumber().equals(empNum))
                    .collect(Collectors.toList());

                System.out.print("Enter Start Date (MM/DD/YYYY): ");
                String startDate = scanner.nextLine();
                System.out.print("Enter End Date (MM/DD/YYYY): ");
                String endDate = scanner.nextLine();

                // ✅ Fix: Ensure the filter function returns a boolean
                List<Attendance> filteredAttendance = employeeAttendance.stream()
                    .filter(a -> a.isWithinDateRange(startDate, endDate)) // ✅ This must return `true` or `false`
                    .collect(Collectors.toList());

                if (filteredAttendance.isEmpty()) {
                    System.out.println("⚠ No attendance records found for Employee ID: " + empNum + " in the selected period.");
                    return;
                }

                // ✅ Calculate total worked hours **only for the selected date range**
                double totalHoursWorked = PayrollCalculator.calculateTotalHoursWorked(empNum, filteredAttendance, startDate, endDate);

                // ✅ Compute salary using correct filtered attendance records
                PayrollCalculator calculator = new PayrollCalculator(List.of(employee), filteredAttendance);
                double salary = calculator.computeSalary(employee, totalHoursWorked);

                // ✅ Display correct payroll details
                System.out.println("\n💰 Net Salary for Employee " + empNum + " from " + startDate + " to " + endDate + ": PHP " + salary);
            } else if (empChoice == 3) {
                return;
            } else {
                System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
}
