package com.mycompany.motorphpayroll.model;

public class RegularEmployee extends Employee {
    public RegularEmployee(String[] v) {
        super(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], 
              Double.parseDouble(v[13]), Double.parseDouble(v[14]), Double.parseDouble(v[15]), 
              Double.parseDouble(v[16]), Double.parseDouble(v[17]), Double.parseDouble(v[18]));
    }
    @Override
    public boolean canAccessModule(String m) { return true; }
}