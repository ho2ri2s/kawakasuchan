package com.kurus.kawakasuchan;

import io.realm.RealmObject;

public class Shoes extends RealmObject {

    private String name;
    private int resourceId;
    private int price;
    private boolean isHaving;

    public Shoes(){

    }

    public Shoes(String name, int resourceId, int price, boolean having){
        this.name = name;
        this.resourceId = resourceId;
        this.price = price;
        this.isHaving = having;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getIsHaving() {
        return isHaving;
    }

    public void setIsHaving(boolean having) {
        isHaving = having;
    }
}
