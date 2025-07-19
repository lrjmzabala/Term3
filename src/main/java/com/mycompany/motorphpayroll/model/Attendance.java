package com.mycompany.motorphpayroll.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    private final String employeeNumber; // Changed back to employeeNumber as per your current error
    private final String lastName;
    private final String firstName;
    private final String date;
    private final String logIn; // Stored original String
    private final String logOut; // Stored original String
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
        this.logIn = logIn;
        this.logOut = logOut;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        try {
            this.loginTime = LocalDateTime.parse(date + " " + logIn, formatter);
            this.logoutTime = LocalDateTime.parse(date + " " + logOut, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date or time format for employee " + employeeNumber + " (Date: " + date + ", Login: " + logIn + ", Logout: " + logOut + ")", e);
        }
    }

    // --- Essential Getters that were causing "cannot find symbol" errors ---

    /**
     * Getter method for employee number.
     * This method is named getEmployeeNumber() to match your current errors.
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * Getter method for date.
     * This was missing in the current error.
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter method for original login time string.
     * This was added in previous steps.
     */
    public String getLogIn() {
        return logIn;
    }

    /**
     * Getter method for original logout time string.
     * This was added in previous steps.
     */
    public String getLogOut() {
        return logOut;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    // --- Methods for calculations/logic that were causing errors ---

    /**
     * Method to check if attendance falls within the given date range.
     * This was missing in the current error.
     */
    public boolean isWithinDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        LocalDate attendanceDate = LocalDate.parse(this.date, formatter);

        // Returns true if attendance date is between start and end (inclusive)
        return !attendanceDate.isBefore(start) && !attendanceDate.isAfter(end);
    }

    /**
     * Calculates total hours worked.
     * This method expects no arguments, as in your previous correct version.
     */
    public double getTotalWorkedHours() {
        Duration duration = Duration.between(loginTime, logoutTime);
        return duration.toMinutes() / 60.0;
    }

    // You might also have other methods here, but these are the ones relevant to the current errors.
}