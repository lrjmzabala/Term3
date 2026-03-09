package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class AttendanceDAO {

    /**
     * Normalizes date strings (e.g., "6/3/2024" -> "06/03/2024")
     * This ensures consistency without changing the model/calculator.
     */
    private String normalizeDate(String rawDate) {
        if (rawDate == null || !rawDate.contains("/")) return rawDate;
        try {
            String[] parts = rawDate.split("/");
            if (parts.length != 3) return rawDate;

            String month = String.format("%02d", Integer.parseInt(parts[0].trim()));
            String day = String.format("%02d", Integer.parseInt(parts[1].trim()));
            String year = parts[2].trim();

            return month + "/" + day + "/" + year;
        } catch (Exception e) {
            return rawDate; // Fallback to original if parsing fails
        }
    }

    public List<Attendance> findAll() {
        return CSVReaderUtil.getAllAttendanceRecords();
    }

    public List<Attendance> findByEmployeeId(String employeeId) {
        List<Attendance> all = CSVReaderUtil.getAllAttendanceRecords();
        List<Attendance> filtered = new ArrayList<>();
        
        for (Attendance a : all) {
            if (a.getEmployeeNumber().trim().equals(employeeId.trim())) {
                filtered.add(a);
            }
        }
        return filtered;
    }

    public Optional<Attendance> findByDate(String employeeId, String date) {
        // Normalize the search criteria date
        String normalizedDate = normalizeDate(date);
        return Optional.ofNullable(CSVReaderUtil.getAttendanceRecord(employeeId, normalizedDate));
    }

    public void save(Attendance attendance) throws Exception {
        // We call the utility directly. If your Utility handles the object, 
        // the normalization happens at the query/read level.
        CSVReaderUtil.addAttendanceToCSV(attendance);
    }

    public boolean update(Attendance attendance) throws Exception {
        return CSVReaderUtil.updateAttendanceInCSV(attendance);
    }
}