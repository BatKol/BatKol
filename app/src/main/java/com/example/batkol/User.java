package com.example.batkol;

public class User {
    String uid,phoneNumber,nameDisplay,email;
    public User(){

    }
    public User(String uid,String phoneNumber,String nameDisplay,String email){
        this.nameDisplay = nameDisplay;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public String getEmail() {
        return email;
    }
}
