package com.mycompany.motorphpayroll.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Attendance {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String date; // Stored as MM/DD/YYYY string
    private String logInTime; // Stored as h:mm:ss a string
    private String logOutTime; // Stored as h:mm:ss a string

    public Attendance(String employeeNumber, String lastName, String firstName, String date, String logInTime, String logOutTime) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logInTime = logInTime;
        this.logOutTime = logOutTime;
    }

    // Getters
    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getDate() { return date; }
    public String getLogInTime() { return logInTime; }
    public String getLogOutTime() { return logOutTime; }

    // Setters (important for updating records)
    public void setLogInTime(String logInTime) { this.logInTime = logInTime; }
    public void setLogOutTime(String logOutTime) { this.logOutTime = logOutTime; }

    /**
     * Calculates the total hours worked for this attendance record.
     * Assumes logInTime and logOutTime are in "h:mm:ss a" format.
     * Returns 0.0 if either time is missing or cannot be parsed.
     * Accounts for lunch break (1 hour) if both times are present and duration > 4 hours.
     *
     * @return Total hours worked (double), or 0.0 if calculation is not possible.
     */
    public double getTotalWorkedHours() {
        if (logInTime == null || logInTime.isEmpty() || logOutTime == null || logOutTime.isEmpty()) {
            return 0.0; // Cannot calculate if times are missing
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalTime inTime = LocalTime.parse(logInTime, timeFormatter);
            LocalTime outTime = LocalTime.parse(logOutTime, timeFormatter);

            // Calculate duration
            Duration duration = Duration.between(inTime, outTime);
            double hours = duration.toMinutes() / 60.0;

            // Assuming a standard 1-hour lunch break if working more than 4 hours
            if (hours > 4.0) {
                hours -= 1.0;
            }

            // Ensure hours are not negative
            return Math.max(0.0, hours);

        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time for employee " + employeeNumber + " on " + date + ": " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Checks if this attendance record's date falls within a given date range.
     * @param startDateStr The start date string (MM/DD/YYYY).
     * @param endDateStr The end date string (MM/DD/YYYY).
     * @return true if the attendance date is within the range (inclusive), false otherwise.
     */
    public boolean isWithinDateRange(String startDateStr, String endDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate attendanceDate = LocalDate.parse(this.date, formatter);
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            return !attendanceDate.isBefore(startDate) && !attendanceDate.isAfter(endDate);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date in Attendance.isWithinDateRange: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "Attendance{" +
               "employeeNumber='" + employeeNumber + '\'' +
               ", lastName='" + lastName + '\'' +
               ", firstName='" + firstName + '\'' +
               ", date='" + date + '\'' +
               ", logInTime='" + logInTime + '\'' +
               ", logOutTime='" + logOutTime + '\'' +
               '}';
    }
}