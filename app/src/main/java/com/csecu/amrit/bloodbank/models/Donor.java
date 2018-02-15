package com.csecu.amrit.bloodbank.models;

/**
 * Created by Amrit on 2/15/2018.
 */

public class Donor {
    String name, email, contact, password, age, address, gender, blood, picture, date;

    public Donor(String name, String email, String contact, String password, String age,
                 String address, String gender, String blood, String picture, String date) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.password = password;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.blood = blood;
        this.picture = picture;
        this.date = date;
    }

    public Donor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
