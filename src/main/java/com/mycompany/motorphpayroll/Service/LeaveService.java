package com.mycompany.motorphpayroll.service;

import com.mycompany.motorphpayroll.DAO.LeaveDAO;
import java.util.List;

public class LeaveService {
    private final LeaveDAO leaveDAO = new LeaveDAO();

    public List<String[]> getPendingLeaves() {
        return leaveDAO.getAllLeaves(); // Fetch all, then filter
    }

    public void approveLeave(String empId) {
        leaveDAO.updateStatus(empId, "APPROVED");
    }
}