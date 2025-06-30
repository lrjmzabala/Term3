package com.mycompany.motorphpayroll.model;

public class Employee extends Person {
    private final String employeeNumber;
    private final String lastName;
    private final String firstName;
    private final String birthday;
    private final String address;
    private final String phoneNumber;
    private final String sssNumber;
    private final String philhealthNumber;
    private final String tinNumber;
    private final String pagibigNumber;
    private final String status;
    private final String position;
    private final String supervisor;
    private final double basicSalary;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;
    private final double grossSemiMonthlyRate;
    private final double hourlyRate;
    
    
    public Employee(String employeeNumber, String lastName, String firstName, String birthday,
                    String address, String phoneNumber, String sssNumber, String philhealthNumber,
                    String tinNumber, String pagibigNumber, String status, String position,
                    String supervisor, double basicSalary, double riceSubsidy,
                    double phoneAllowance, double clothingAllowance,
                    double grossSemiMonthlyRate, double hourlyRate) {
        // Calling superclass constructor (Person)
        super(firstName, lastName, phoneNumber);

        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.sssNumber = sssNumber;
        this.philhealthNumber = philhealthNumber;
        this.tinNumber = tinNumber;
        this.pagibigNumber = pagibigNumber;
        this.status = status;
        this.position = position;
        this.supervisor = supervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    // Method to return full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Overriding toString() method to properly format Employee details
    @Override
    public String toString() {
        return getRoleDescription() + "\n" +
               "Employee Number: " + employeeNumber + "\n" +
               "Birthday: " + birthday + "\n" +
               "Address: " + address + "\n" +
               "Phone: " + phoneNumber + "\n" +
               "SSS #: " + sssNumber + "\n" +
               "PhilHealth #: " + philhealthNumber + "\n" +
               "TIN #: " + tinNumber + "\n" +
               "Pag-IBIG #: " + pagibigNumber + "\n" +
               "Status: " + status + "\n" +
               "Position: " + position + "\n" +
               "Supervisor: " + supervisor + "\n" +
               "Basic Salary: " + basicSalary + "\n" +
               "Rice Subsidy: " + riceSubsidy + "\n" +
               "Phone Allowance: " + phoneAllowance + "\n" +
               "Clothing Allowance: " + clothingAllowance + "\n" +
               "Gross Semi-Monthly Rate: " + grossSemiMonthlyRate + "\n" +
               "Hourly Rate: " + hourlyRate + "\n" +
               "Daily Rate (Calculated): " + getDailyRate() + "\n" + // Display calculated daily rate
               "--------------------------------------";
    }

    // Getters for all final fields
    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getBirthday() { return birthday; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagibigNumber() { return pagibigNumber; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getSupervisor() { return supervisor; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public double getHourlyRate() { return hourlyRate; }


   
    public double getDailyRate() {
        return this.hourlyRate * 8; 
    }

    
    public double getDailyWage() {
        return this.hourlyRate * 8;
    }

    @Override
    public String getRoleDescription() {
        return "Employee: " + position;
    }

    
    public String getId() {
        return employeeNumber;
    }
}