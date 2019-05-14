package com.kurus.kawakasuchan;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Character extends RealmObject {


    private Clothes clothes;
    private Shoes shoes;
    private RealmList<Interior> interiors;

    //ドライヤーポイント
    private int dPoint;
    //経験値 0~100
    private int experienceNow;
    //レベル
    private int level;
    //濡れメーター0~100
    private int wetStatus;
    //濡れている段階0~3ステージまで
    private int wetStage;
    //キャラが存在するか
    private boolean isCharacter;


    public Character() {
        this.dPoint = 1000;
        this.experienceNow = 0;
        this.level = 1;
        this.wetStatus = 100;
        this.wetStage = 3;
        this.isCharacter = true;
    }


    public int getExperienceNow() {
        return experienceNow;
    }

    public int getdPoint() {
        return dPoint;
    }

    public int getLevel() {
        return level;
    }

    public int getWetStage() {
        return wetStage;
    }

    public int getWetStatus() {
        return wetStatus;
    }

    public boolean getIsCharacter() {
        return isCharacter;
    }

    public Shoes getShoes() {
        return shoes;
    }

    public Clothes getClothes() {
        return clothes;
    }

    public RealmList<Interior> getInteriors() {
        return interiors;
    }

    public void setdPoint(int dPoint) {
        this.dPoint = dPoint;
    }

    public void setExperienceNow(int experienceNow) {
        this.experienceNow = experienceNow;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setWetStatus(int wetStatus) {
        this.wetStatus = wetStatus;
    }

    public void setWetStage(int wetStage) {
        this.wetStage = wetStage;
    }

    public void setIsCharacter(boolean isCharacter) {
        this.isCharacter = isCharacter;
    }


    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }


    public void setShoes(Shoes shoes) {
        this.shoes = shoes;
    }

    public void setInteriors(RealmList<Interior> interiors) {
        this.interiors = interiors;
    }
}
