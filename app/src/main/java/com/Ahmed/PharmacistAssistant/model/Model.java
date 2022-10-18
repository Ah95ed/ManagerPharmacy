package com.Ahmed.PharmacistAssistant.model;


import java.util.Date;

public class Model {
    private String id,name,code,cost,sell,quantity;

    public Model(String name, String code, String cost, String sell, String id) {

        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
        this.id = id;

    }
    public Model(String name, String code, String cost, String sell) {

        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
    }
    public Model (String sell){
        this.sell = sell;
    }

    public Model( String name, String code, String cost, String sell,String id,String quantity) {

        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell= sell;
        this.id = id;
        this.quantity=quantity;
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
