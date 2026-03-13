package com.mycompany.motorphpayroll.model;

public abstract class Employee {
    private String employeeNumber, lastName, firstName, birthday, address, phoneNumber, 
                   sssNumber, philhealthNumber, tinNumber, pagibigNumber, status, 
                   position, supervisor, immediateSupervisor;
    private double basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
                   grossSemiMonthlyRate, hourlyRate;

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
        setBasicSalary(basicSalary);
        this.riceSubsidy = riceSubsidy; 
        this.phoneAllowance = phoneAllowance; 
        this.clothingAllowance = clothingAllowance; 
        this.grossSemiMonthlyRate = grossSemiMonthlyRate; 
        this.hourlyRate = hourlyRate;
    }

    public abstract boolean canAccessModule(String moduleName);

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
    public String getImmediateSupervisor() { return immediateSupervisor; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public double getHourlyRate() { return hourlyRate; }

    // --- Setters (with validation in setBasicSalary) ---
    public void setLastName(String ln) { this.lastName = ln; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public void setBirthday(String b) { this.birthday = b; }
    public void setAddress(String a) { this.address = a; }
    public void setPhoneNumber(String p) { this.phoneNumber = p; }
    public void setSssNumber(String s) { this.sssNumber = s; }
    public void setPhilhealthNumber(String p) { this.philhealthNumber = p; }
    public void setTinNumber(String t) { this.tinNumber = t; }
    public void setPagibigNumber(String p) { this.pagibigNumber = p; }
    public void setStatus(String s) { this.status = s; }
    public void setPosition(String p) { this.position = p; }
    public void setSupervisor(String s) { this.supervisor = s; }
    public void setBasicSalary(double b) { 
        if (b < 0) throw new IllegalArgumentException("Basic Salary cannot be negative");
        this.basicSalary = b; 
    }
    public void setRiceSubsidy(double r) { this.riceSubsidy = r; }
    public void setPhoneAllowance(double p) { this.phoneAllowance = p; }
    public void setClothingAllowance(double c) { this.clothingAllowance = c; }
    public void setGrossSemiMonthlyRate(double g) { this.grossSemiMonthlyRate = g; }
    public void setHourlyRate(double h) { this.hourlyRate = h; }

    // --- Helpers ---
    public String getFullName() { return firstName + " " + lastName; }
    public double getDailyWage() { return hourlyRate * 8.0; }
    
    @Override
    public String toString() {
        return "--- Employee Details ---\n" +
               "ID: " + employeeNumber + "\n" +
               "Name: " + firstName + " " + lastName + "\n" +
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
               "Basic Salary: PHP " + String.format("%,.2f", basicSalary) + "\n" +
               "Rice Subsidy: PHP " + String.format("%,.2f", riceSubsidy) + "\n" +
               "Phone Allowance: PHP " + String.format("%,.2f", phoneAllowance) + "\n" +
               "Clothing Allowance: PHP " + String.format("%,.2f", clothingAllowance) + "\n" +
               "Gross Semi-Monthly: PHP " + String.format("%,.2f", grossSemiMonthlyRate) + "\n" +
               "Hourly Rate: PHP " + String.format("%,.2f", hourlyRate);
    }
}