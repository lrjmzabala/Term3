package com.mycompany.motorphpayroll.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    private final String employeeNumber;
    private final String lastName;
    private final String firstName;
    private final String date;  
    private final LocalDateTime loginTime;
    private final LocalDateTime logoutTime;

    /**
     * Constructor to initialize attendance details.
     */
    public Attendance(String employeeNumber, String lastName, String firstName, String date, String logIn, String logOut) {
        if (employeeNumber == null || lastName == null || firstName == null || date == null || logIn == null || logOut == null ||
            employeeNumber.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || date.isEmpty() || logIn.isEmpty() || logOut.isEmpty()) {
            throw new IllegalArgumentException("Invalid row: Missing required fields for employee " + employeeNumber);
        }

        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;  

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        try {
            this.loginTime = LocalDateTime.parse(date + " " + logIn, formatter);
            this.logoutTime = LocalDateTime.parse(date + " " + logOut, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date or time format for employee " + employeeNumber);
        }
    }

    /**
     * ✅ Getter method for date.
     */
    public String getDate() {
        return date;
    }

    /**
     * ✅ Method to check if attendance falls within the given date range.
     */
    public boolean isWithinDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        LocalDate attendanceDate = LocalDate.parse(this.date, formatter);

        // ✅ Returns true if attendance date is between start and end (inclusive)
        return !attendanceDate.isBefore(start) && !attendanceDate.isAfter(end);
    }

    /**
     * ✅ Calculates total hours worked.
     */
    public double getTotalWorkedHours() {
        Duration duration = Duration.between(loginTime, logoutTime);
        return duration.toMinutes() / 60.0;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}