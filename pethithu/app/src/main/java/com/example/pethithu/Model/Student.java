package com.example.pethithu.Model;

public class Student {
    private String id;
    private String name;
    private String date;
    private String gender;
    private String Address;
    private String MajorId; // MajorId theo dữ liệu từ API

    // Biến để lưu tên chuyên ngành sau khi gộp dữ liệu
    private String nameMajor;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return Address;
    }

    public String getMajorId() {
        return MajorId;
    }

    public void setNameMajor(String nameMajor) {
        this.nameMajor = nameMajor;
    }

    public String getNameMajor() {
        return nameMajor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setMajorId(String majorId) {
        this.MajorId = majorId;
    }
}
