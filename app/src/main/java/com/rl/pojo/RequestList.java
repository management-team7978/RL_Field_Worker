package com.rl.pojo;

public class RequestList {
    private String id;
    private String user_id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String work_description;
    private String date;
    private String status;
    private String bill;

    public RequestList(String id, String user_id, String name, String email, String phone, String address, String work_description, String date, String status, String bill) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.work_description = work_description;
        this.date = date;
        this.status = status;
        this.bill = bill;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWork_description() {
        return work_description;
    }

    public void setWork_description(String work_description) {
        this.work_description = work_description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    @Override
    public String toString() {
        return "RequestList{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", work_description='" + work_description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
