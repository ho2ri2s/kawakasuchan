package com.kurus.kawakasuchan;

import io.realm.RealmObject;

public class Clothes extends RealmObject {

    private String name;
    private int clothesResourceId;
    private int characterResourceId;
    private int price;
    private boolean isHaving;

    public Clothes(){

    }

    // TODO: 2019/05/12 素材を全て準備するのではなく服を重ねる感じにしたい
    public Clothes(String name, int clothesResourceId, int characterResourceId, int price, boolean isHaving){
        this.name = name;
        this.clothesResourceId = clothesResourceId;
        this.characterResourceId = characterResourceId;
        this.price = price;
        this.isHaving = isHaving;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClothesResourceId() {
        return clothesResourceId;
    }

    public void setClothesResourceId(int clothesResourceId) {
        this.clothesResourceId = clothesResourceId;
    }

    public int getCharacterResourceId() {
        return characterResourceId;
    }

    public void setCharacterResourceId(int characterResourceId) {
        this.characterResourceId = characterResourceId;
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
