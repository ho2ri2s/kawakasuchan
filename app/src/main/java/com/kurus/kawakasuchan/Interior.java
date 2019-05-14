package com.kurus.kawakasuchan;

import io.realm.RealmObject;

public class Interior extends RealmObject {

    private String name;
    private int resourceId;
    //画像サイズ
    private int width;
    private int height;
    //設置するの中心座標
    private int x;
    private int y;
    private int price;
    private boolean isHaving;

    public Interior(){

    }

    public Interior(String name, int resourceId, int width, int height, int x, int y, int price, boolean having){
        this.name = name;
        this.resourceId = resourceId;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
