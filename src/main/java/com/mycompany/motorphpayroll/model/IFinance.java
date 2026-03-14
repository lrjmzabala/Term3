package com.mycompany.motorphpayroll.model;

public interface IFinance {
    double calculateNetPay(String employeeId);
    void generatePayrollReport();
}