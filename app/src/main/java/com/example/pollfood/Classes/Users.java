package com.example.pollfood.Classes;

import com.google.firebase.firestore.auth.User;

public class Users {


    private Object uid;

    private String email;
    private String username;
    private String firstname;

    private String secondname;

    private Long familyid;





    private static Users user;


    public Long getFamilyid() {
        return familyid;
    }

    public void setFamilyid(Long familyid) {
        this.familyid = familyid;
    }

    private Users(Object uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
    }
    private Users() {
        this.username = "anonim";
    }

    public static synchronized Users getInstance(Object uid, String email, String username){
        if (user == null){
            user =  new Users( uid, email, username);
        }
        return user;
    }
    public static synchronized Users getInstance(){
        return user;
    }
    public Object getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public static void destroyInstance() {
        user = null;
    }
    public String getFirstname() {
        return firstname;
    }
    public static void clearInstance() {
        user = null;
    }
    public String getSecondname() {
        return secondname;
    }

    public void setUid(Object uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    @Override
    public String toString() {
        return "Users{" +
                "uid=" + uid +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", secondname='" + secondname + '\'' +
                ", familyid=" + familyid +
                '}';
    }
}
