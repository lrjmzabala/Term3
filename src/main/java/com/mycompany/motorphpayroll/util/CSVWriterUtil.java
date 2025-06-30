package com.mycompany.motorphpayroll.util;

import com.mycompany.motorphpayroll.model.Employee;
import java.io.*;

public class CSVWriterUtil {
    public static void appendEmployeeToCSV(String filename, Employee emp) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.printf("%s,%s,%s,%.2f%n", emp.getId(), emp.getFullName(), emp.getPosition(), emp.getDailyRate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
