package com.kurus.kawakasuchan;

import io.realm.RealmObject;

public class Character extends RealmObject {
    
    //ドライヤーポイント
    private int dPoint;
    //経験値 0~100
    private int experienceNow;
    //レベル
    private  int level;
    //濡れメーター0~100
    private int wetStatus;
    //濡れている段階0~3ステージまで
    private int wetStage;
    //キャラが存在するか
    private boolean isCharacter;


    public int getExperienceNow(){
        return experienceNow;
    }
    public int getdPoint(){
        return dPoint;
    }
    public int getLevel(){
        return level;
    }
    public int getWetStage(){return wetStage;}
    public int getWetStatus() {
        return wetStatus;
    }
    public boolean getIsCharacter(){return isCharacter;}


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

    public void setIsCharacter(boolean isCharacter){ this.isCharacter = isCharacter;}
}
