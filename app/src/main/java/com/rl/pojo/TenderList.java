package com.rl.pojo;

import java.util.ArrayList;

public class TenderList {
    private String serial;
    private String bdm_manager_id;
    private String name;
    private String mobile;
    private String email_id;
    private String tender_no;
    private String occupation;
    private String workplace_address;
    private String pincode;
    private String date;
    private String id;
    private ArrayList<String> tender_pdf;

    public TenderList(String serial, String bdm_manager_id, String name, String mobile, String email_id, String tender_no, String occupation, String workplace_address, String pincode, String date, String id, ArrayList<String> tender_pdf) {
        this.serial = serial;
        this.bdm_manager_id = bdm_manager_id;
        this.name = name;
        this.mobile = mobile;
        this.email_id = email_id;
        this.tender_no = tender_no;
        this.occupation = occupation;
        this.workplace_address = workplace_address;
        this.pincode = pincode;
        this.date = date;
        this.id = id;
        this.tender_pdf = tender_pdf;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getBdm_manager_id() {
        return bdm_manager_id;
    }

    public void setBdm_manager_id(String bdm_manager_id) {
        this.bdm_manager_id = bdm_manager_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getTender_no() {
        return tender_no;
    }

    public void setTender_no(String tender_no) {
        this.tender_no = tender_no;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getWorkplace_address() {
        return workplace_address;
    }

    public void setWorkplace_address(String workplace_address) {
        this.workplace_address = workplace_address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getTender_pdf() {
        return tender_pdf;
    }

    public void setTender_pdf(ArrayList<String> tender_pdf) {
        this.tender_pdf = tender_pdf;
    }
}
