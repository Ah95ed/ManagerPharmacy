package com.Ahmed.PharmacistAssistant.model;

public class Model {
    private String id,name,code,cost,sell,date,quantity;


    public Model( String name,
                 String code, String cost,
                 String sell,String id, String date,
                 String quantity) {

        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
        this.id = id;
        this.date = date;
        this.quantity = quantity;

    }

    public Model(String name, String code, String cost, String sell, String date) {
        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
        this.date = date;
    }

    public Model(String name, String code, String cost, String sell,String id , String date) {
        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
        this.id = id;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }
}
