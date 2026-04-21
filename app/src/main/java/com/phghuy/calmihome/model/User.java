package com.phghuy.calmihome.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private String emailId;
    private String password;
    private boolean active;
    private String createdTime;

    // Constructor mặc định (không đối số)
    public User() {
        this.createdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // Constructor với tất cả các trường
    public User(int id, String firstName, String lastName, String userName, String emailId, String password, boolean active, String createdTime) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.emailId = emailId;
        this.password = password;
        this.active = active;
        this.createdTime = createdTime;
    }

    // Getters và Setters cho từng trường
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    // Có thể override phương thức toString() để dễ dàng in thông tin đối tượng User
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", password='" + "[PROTECTED]" + '\'' + // Không nên in mật khẩu ra trực tiếp
                ", active=" + active +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}
