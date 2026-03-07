package com.mycompany.motorphpayroll.DAO;

import java.io.*;
import java.util.*;

public class LeaveDAO {
    private final String FILE_PATH = "leave_requests.csv";

    public List<String[]> getAllLeaves() {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) list.add(line.split(","));
        } catch (IOException e) { /* Log error */ }
        return list;
    }

    public void updateStatus(String empId, String status) {
    }
}