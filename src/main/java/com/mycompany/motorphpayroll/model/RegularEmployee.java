package com.mycompany.motorphpayroll.model;

public class RegularEmployee extends Employee {

    public RegularEmployee(String[] v) {
        super(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], 
              parseMoney(v[13]), // Basic Salary
              parseMoney(v[14]), // Rice Subsidy
              parseMoney(v[15]), // Phone Allowance
              parseMoney(v[16]), // Clothing Allowance
              parseMoney(v[17]), // Gross Semi-monthly Rate
              parseMoney(v[18])  // Hourly Rate
        );
    }

    // This helper cleans the string before passing it to Double.parseDouble
    private static double parseMoney(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        // Removes commas and trims whitespace
        return Double.parseDouble(value.replace(",", "").trim());
    }

    @Override
    public boolean canAccessModule(String m) { 
        return m.equals("Attendance") || m.equals("Profile");
    }
}