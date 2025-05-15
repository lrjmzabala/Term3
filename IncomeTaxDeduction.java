package com.mycompany.motorphpayroll.Deduction;

public class IncomeTaxDeduction implements Deduction {

    @Override
    public double compute(double grossSalary) {
        if (grossSalary <= 20832) {
            return 0.0;
        } else if (grossSalary <= 33333) {
            return 0.20 * (grossSalary - 20833);
        } else if (grossSalary <= 66667) {
            return 2500 + 0.25 * (grossSalary - 33333);
        } else if (grossSalary <= 166667) {
            return 10833 + 0.30 * (grossSalary - 66667);
        } else if (grossSalary <= 666667) {
            return 40833.33 + 0.32 * (grossSalary - 166667);
        } else {
            return 200833.33 + 0.35 * (grossSalary - 666667);
        }
    }

    @Override
    public String getName() {
        return "Income Tax";
    }
}
