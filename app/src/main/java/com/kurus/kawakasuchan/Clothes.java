package com.kurus.kawakasuchan;

import io.realm.RealmObject;

public class Clothes extends RealmObject {

    //ステータスが
    // 0:未所持
    // 1:所持
    // 2:着用
    private int balloonDressStatus;
    private int shirtDressStatus;
    private int casualClothesStatus;

    public Clothes(){
        this.balloonDressStatus = 0;
        this.shirtDressStatus = 0;
        this.casualClothesStatus = 0;
    }

    public int getBalloonDressStatus() {
        return balloonDressStatus;
    }

    public void setBalloonDressStatus(int balloonDressStatus) {
        this.balloonDressStatus = balloonDressStatus;
    }

    public int getShirtDressStatus() {
        return shirtDressStatus;
    }

    public void setShirtDressStatus(int shirtDressStatus) {
        this.shirtDressStatus = shirtDressStatus;
    }

    public int getCasualClothesStatus() {
        return casualClothesStatus;
    }

    public void setCasualClothesStatus(int casualClothesStatus) {
        this.casualClothesStatus = casualClothesStatus;
    }
}
