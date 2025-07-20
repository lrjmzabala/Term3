package com.mycompany.motorphpayroll.model;

public class Employee {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String birthday; // MM/DD/YYYY
    private String address;
    private String phoneNumber;
    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagibigNumber;
    private String status;
    private String position;
    private String supervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    // Constructor
    public Employee(String employeeNumber, String lastName, String firstName, String birthday, String address, String phoneNumber, String sssNumber, String philhealthNumber, String tinNumber, String pagibigNumber, String status, String position, String supervisor, double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {
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

    // --- Getters ---
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

    // --- NEW: Computed Getters for other classes ---

    /**
     * Returns the full name of the employee (first name + last name).
     * @return The employee's full name.
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Returns the employee ID, which is the employee number.
     * @return The employee's ID.
     */
    public String getId() {
        return this.employeeNumber;
    }

    /**
     * Calculates and returns the daily wage based on the hourly rate.
     * Assumes an 8-hour workday.
     * @return The employee's daily wage.
     */
    public double getDailyWage() {
        // Assuming an 8-hour workday for daily wage calculation
        return this.hourlyRate * 8.0;
    }

    /**
     * Returns the daily rate, which is currently the same as the daily wage.
     * @return The employee's daily rate.
     */
    public double getDailyRate() {
        return getDailyWage(); // Can be adjusted if daily rate is calculated differently
    }


    // --- Setters ---
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }
    public void setPhilhealthNumber(String philhealthNumber) { this.philhealthNumber = philhealthNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public void setPagibigNumber(String pagibigNumber) { this.pagibigNumber = pagibigNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setPosition(String position) { this.position = position; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    @Override
    public String toString() {
        return "Employee Number: " + employeeNumber + "\n" +
               "Name: " + firstName + " " + lastName + "\n" +
               "Birthday: " + birthday + "\n" +
               "Address: " + address + "\n" +
               "Phone Number: " + phoneNumber + "\n" +
               "SSS #: " + sssNumber + "\n" +
               "Philhealth #: " + philhealthNumber + "\n" +
               "TIN #: " + tinNumber + "\n" +
               "Pag-IBIG #: " + pagibigNumber + "\n" +
               "Status: " + status + "\n" +
               "Position: " + position + "\n" +
               "Supervisor: " + supervisor + "\n" +
               "Basic Salary: PHP " + String.format("%,.2f", basicSalary) + "\n" +
               "Rice Subsidy: PHP " + String.format("%,.2f", riceSubsidy) + "\n" +
               "Phone Allowance: PHP " + String.format("%,.2f", phoneAllowance) + "\n" +
               "Clothing Allowance: PHP " + String.format("%,.2f", clothingAllowance) + "\n" +
               "Gross Semi-monthly Rate: PHP " + String.format("%,.2f", grossSemiMonthlyRate) + "\n" +
               "Hourly Rate: PHP " + String.format("%,.2f", hourlyRate);
    }
}