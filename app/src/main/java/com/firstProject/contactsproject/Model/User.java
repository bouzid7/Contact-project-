package com.firstProject.contactsproject.Model;


public class User  {
     private String firstName ;
     private String lastName;
     private String birthDate;
     private String userName;
     private String password;


    public User(String firstName,String lastName,String birthDate,String userName,String password) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.birthDate=birthDate;
        this.userName=userName;
        this.password=password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
