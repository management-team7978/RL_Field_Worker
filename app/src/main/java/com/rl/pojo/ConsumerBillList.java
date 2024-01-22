package com.rl.pojo;

public class ConsumerBillList {
    String serial;
    String bill;
    String billing;

    public ConsumerBillList(String serial, String bill, String billing) {
        this.serial = serial;
        this.bill = bill;
        this.billing = billing;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public String getBilling() {
        return billing;
    }

    public void setBilling(String billing) {
        this.billing = billing;
    }
}
