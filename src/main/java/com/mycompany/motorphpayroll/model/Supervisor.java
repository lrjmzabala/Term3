package com.mycompany.motorphpayroll.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Supervisor extends Employee implements ISupervisor {

    public Supervisor(String[] v) {
        super(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], 
              Double.parseDouble(v[13]), Double.parseDouble(v[14]), Double.parseDouble(v[15]), 
              Double.parseDouble(v[16]), Double.parseDouble(v[17]), Double.parseDouble(v[18]));
    }

    @Override
    public List<String[]> getSubordinateLeaveRequests(String supervisorName, List<Employee> allEmployees) {
        List<String[]> requests = new ArrayList<>();
        File file = new File("leave_requests.csv");
        
        List<String> subIds = new ArrayList<>();
        for (Employee emp : allEmployees) {
            String empSup = emp.getSupervisor() != null ? emp.getSupervisor().replace("\"", "").replace(" ", "").toLowerCase() : "";
            if (empSup.equals(supervisorName)) subIds.add(emp.getEmployeeNumber());
        }

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (data.length >= 5 && subIds.contains(data[0].replace("\"", "").trim())) {
                        requests.add(data);
                    }
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
        return requests;
    }

    @Override
    public void approveLeave(String employeeId, String date) { updateCSVStatus(employeeId, date, "APPROVED"); }

    @Override
    public void denyLeave(String employeeId, String date) { updateCSVStatus(employeeId, date, "DENIED"); }

    private void updateCSVStatus(String empId, String date, String newStatus) {
        List<String> lines = new ArrayList<>();
        File file = new File("leave_requests.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 5 && data[0].replace("\"", "").trim().equals(empId) && data[1].trim().equals(date)) {
                    data[4] = newStatus;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) { e.printStackTrace(); }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @Override
    public boolean canAccessModule(String m) {
        // Supervisors can access the Supervisor portal, plus standard modules
        return m.equals("Supervisor") || m.equals("Attendance") || m.equals("Leave");
    }
}