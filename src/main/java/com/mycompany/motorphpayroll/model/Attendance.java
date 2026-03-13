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
    private String date;
    private String logInTime;
    private String logOutTime;

    // Existing constructor
    public Attendance(String employeeNumber, String lastName, String firstName, String date, String logInTime, String logOutTime) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logInTime = logInTime;
        this.logOutTime = logOutTime;
    }

    // NEW Constructor: Accepts String[] array from CSVReaderUtil
    public Attendance(String[] v) {
        if (v.length >= 6) {
            this.employeeNumber = v[0].trim();
            this.lastName = v[1].trim();
            this.firstName = v[2].trim();
            this.date = v[3].trim();
            this.logInTime = v[4].trim();
            this.logOutTime = v[5].trim();
        }
    }

    // Getters
    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getDate() { return date; }
    public String getLogInTime() { return logInTime; }
    public String getLogOutTime() { return logOutTime; }

    // Setters
    public void setLogInTime(String logInTime) { this.logInTime = logInTime; }
    public void setLogOutTime(String logOutTime) { this.logOutTime = logOutTime; }

    /**
     * Calculates the total hours worked for this attendance record.
     */
    public double getTotalWorkedHours() {
        if (logInTime == null || logInTime.isEmpty() || logOutTime == null || logOutTime.isEmpty()) {
            return 0.0;
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalTime inTime = LocalTime.parse(logInTime, timeFormatter);
            LocalTime outTime = LocalTime.parse(logOutTime, timeFormatter);

            Duration duration = Duration.between(inTime, outTime);
            double hours = duration.toMinutes() / 60.0;

            if (hours > 4.0) {
                hours -= 1.0;
            }

            return Math.max(0.0, hours);

        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time for employee " + employeeNumber + " on " + date + ": " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Checks if this attendance record's date falls within a given date range.
     * Updated formatter to "M/d/yyyy" to handle both "6/3/2024" and "06/03/2024".
     */
    public boolean isWithinDateRange(String startDateStr, String endDateStr) {
        try {
            // Using M/d/yyyy makes the parser flexible for single or double digits
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            
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