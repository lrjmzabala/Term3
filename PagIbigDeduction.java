/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphpayroll.Deduction;

/**
 *
 * @author Papa
 */
public class PagIbigDeduction implements Deduction {
    public double compute(double grossSalary) {
        return Math.min(grossSalary * 0.02, 100);
    }

    public String getName() {
        return "Pag-IBIG";
    }
}