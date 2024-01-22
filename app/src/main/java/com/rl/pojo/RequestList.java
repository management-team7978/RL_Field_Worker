package com.rl.pojo;

public class RequestList {
    private String serial;
    private String service_request_id;
    private String consumer_user_id;
    private String name;
    private String phone;
    private String quotation;
    private String quotations;

    public RequestList(String serial, String service_request_id, String consumer_user_id, String name, String phone, String quotation, String quotations) {
        this.serial = serial;
        this.service_request_id = service_request_id;
        this.consumer_user_id = consumer_user_id;
        this.name = name;
        this.phone = phone;
        this.quotation = quotation;
        this.quotations = quotations;
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
}
