package com.mycompany.motorphpayroll.model;

public class SalaryBreakdown {
    private double grossSalary;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double incomeTax;
    private double netSalary;

    public SalaryBreakdown(double grossSalary, double incomeTax) {
        this.grossSalary = grossSalary;
        this.incomeTax = incomeTax;
        this.sss = grossSalary * 0.045;
        this.philhealth = grossSalary * 0.0275;
        this.pagibig = Math.min(grossSalary * 0.02, 100);
        this.netSalary = grossSalary - (sss + philhealth + pagibig + incomeTax);
    }

    public String getBreakdownText(double totalHours) {
        return "💰 Salary Breakdown:\n"
            + "--------------------------------\n"
            + "Total Hours Worked: " + totalHours + "\n"
            + "Gross Salary: PHP " + String.format("%,.2f", grossSalary) + "\n"
            + "Deductions:\n"
            + "  - SSS: PHP " + String.format("%,.2f", sss) + "\n"
            + "  - PhilHealth: PHP " + String.format("%,.2f", philhealth) + "\n"
            + "  - Pag-IBIG: PHP " + String.format("%,.2f", pagibig) + "\n"
            + "  - Income Tax: PHP " + String.format("%,.2f", incomeTax) + "\n"
            + "--------------------------------\n"
            + "✅ Net Salary: PHP " + String.format("%,.2f", netSalary) + "\n";
    }

    public double getNetSalary() {
        return netSalary;
    }
}