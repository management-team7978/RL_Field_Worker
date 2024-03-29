package com.rl.pojo;

public class RequestList {
    private String serial;
    private String service_request_id;
    private String consumer_user_id;
    private String name;
    private String phone;
    private String quotation;
    private String quotations;
    private String occupation;
    private String id;
    private String work_status;
    private String address;
    private String status_code;
    private String status_message;

    public RequestList(String serial, String service_request_id, String consumer_user_id, String name, String phone, String quotation, String quotations, String occupation, String id, String work_status, String address, String status_code, String status_message) {
        this.serial = serial;
        this.service_request_id = service_request_id;
        this.consumer_user_id = consumer_user_id;
        this.name = name;
        this.phone = phone;
        this.quotation = quotation;
        this.quotations = quotations;
        this.occupation = occupation;
        this.id = id;
        this.work_status = work_status;
        this.address = address;
        this.status_code = status_code;
        this.status_message = status_message;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getService_request_id() {
        return service_request_id;
    }

    public void setService_request_id(String service_request_id) {
        this.service_request_id = service_request_id;
    }

    public String getConsumer_user_id() {
        return consumer_user_id;
    }

    public void setConsumer_user_id(String consumer_user_id) {
        this.consumer_user_id = consumer_user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getQuotations() {
        return quotations;
    }

    public void setQuotations(String quotations) {
        this.quotations = quotations;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }
}
