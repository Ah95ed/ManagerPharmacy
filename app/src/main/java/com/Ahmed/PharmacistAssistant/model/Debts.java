package com.Ahmed.PharmacistAssistant.model;

public class Debts {

    private String id,name,amount,time,description;


    public Debts() {
    }

    public Debts(String name, String amount, String time, String description) {
        this.name = name;
        this.amount = amount;
        this.time = time;
        this.description = description;
    }

    public Debts(String id, String name, String amount, String time, String description) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.time = time;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    ///////////////////////////////////////////////////////////////


}
