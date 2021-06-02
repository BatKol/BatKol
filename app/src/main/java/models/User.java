package models;

public class User {
    String uid,phoneNumber,nameDisplay,email;
    boolean useAccessibility;
    public User(){

    }
    public User(String uid,String phoneNumber,String nameDisplay,String email, boolean useAccessibility){
        this.nameDisplay = nameDisplay;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.email = email;
        this.useAccessibility = useAccessibility;
    }

    public boolean isUseAccessibility() {return useAccessibility;}

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
