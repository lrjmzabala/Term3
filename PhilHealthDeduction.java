/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphpayroll.Deduction;

/**
 *
 * @author Papa
 */
public class PhilHealthDeduction implements Deduction {
    public double compute(double grossSalary) {
        return grossSalary * 0.0275;
    }

    public String getName() {
        return "PhilHealth";
    }
}