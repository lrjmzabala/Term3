package com.mycompany.motorphpayroll.model; // Must match the package folder

public class IT extends Employee implements IIT {
    public IT(String[] v) {
        super(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], 
              Double.parseDouble(v[13]), Double.parseDouble(v[14]), Double.parseDouble(v[15]), 
              Double.parseDouble(v[16]), Double.parseDouble(v[17]), Double.parseDouble(v[18]));
    }
    
    @Override
    public boolean canAccessModule(String m) {
        return m.equals("IT") || m.equals("Profile");
    }

    @Override
    public void resetUserPassword(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void viewSystemLogs() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}