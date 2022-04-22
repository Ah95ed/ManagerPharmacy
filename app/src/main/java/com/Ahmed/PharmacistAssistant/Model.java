package com.Ahmed.PharmacistAssistant;

public class Model {
    String id,name,code,cost,sell,quantity,dose,drugName,mostSideEffect,mechanismOfAction,pregnancy;
    public Model(String name, String code, String cost, String sell,String id,String dose,String drugName,
    String mostSideEffect,String mechanismOfAction ,String pregnancy) {

        this.name = name;
        this.code = code;
        this.cost = cost;
        this.sell = sell;
        this.id = id;
        this.dose =dose;
        this.drugName = drugName;
        this.mostSideEffect = mostSideEffect;
        this.mechanismOfAction = mechanismOfAction;
        this.pregnancy= pregnancy;
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

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getMostSideEffect() {
        return mostSideEffect;
    }

    public void setMostSideEffect(String mostSideEffect) {
        this.mostSideEffect = mostSideEffect;
    }

    public String getMechanismOfAction() {
        return mechanismOfAction;
    }

    public void setMechanismOfAction(String mechanismOfAction) {
        this.mechanismOfAction = mechanismOfAction;
    }

    public String getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(String pregnancy) {
        this.pregnancy = pregnancy;
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
