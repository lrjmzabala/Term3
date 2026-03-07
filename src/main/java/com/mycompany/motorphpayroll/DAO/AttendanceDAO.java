package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.Attendance;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.List;
import java.util.Optional;

public class AttendanceDAO {

    public List<Attendance> findAll() {
        return CSVReaderUtil.getAllAttendanceRecords();
    }

    public List<Attendance> findByEmployeeId(String employeeId) {
        // This utilizes the cache logic in Utility class
        return CSVReaderUtil.getAllAttendanceRecords().stream()
                .filter(a -> a.getEmployeeNumber().equals(employeeId))
                .toList();
    }

    public Optional<Attendance> findByDate(String employeeId, String date) {
        return Optional.ofNullable(CSVReaderUtil.getAttendanceRecord(employeeId, date));
    }

    public void save(Attendance attendance) throws Exception {
        CSVReaderUtil.addAttendanceToCSV(attendance);
    }

    public boolean update(Attendance attendance) throws Exception {
        return CSVReaderUtil.updateAttendanceInCSV(attendance);
    }
}