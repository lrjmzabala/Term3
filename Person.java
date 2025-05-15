package com.mycompany.motorphpayroll.model;

/**
 * Base class for shared attributes between Employee and Admin.
 */
public abstract class Person {
    protected String firstName;
    protected String lastName;
    protected String phoneNumber;

    public Person(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public abstract String getRoleDescription();
}