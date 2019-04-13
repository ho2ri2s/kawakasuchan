package com.kurus.kawakasuchan;

public class Character {
    
    //ドライヤーポイント
    private int dPoint;
    //経験値 0~100
    private int experienceNow;
    //レベル
    private  int level;
    //濡れメーター0~100
    private int wetStatus;
    //濡ている段階0~3ステージまで
    private int wetStage;
    
    public Character(){
        dPoint = 0;
        experienceNow = 0;
        level = 1;
        //初めは乾いている状態
        wetStatus = 0;
        wetStage = 0;
    }
    
    public void drying(){

        //髪が濡れている状態なら3秒に1メーター減らし、濡段階が1段階減るごとに経験値とDポイントをゲットする。
        if(wetStage != 0){
            wetStatus--;
            if(wetStatus % 25 == 0){
                wetStage--;
                expGenerate();
                dpGenerate();
            }
        }
    }

    public void expGenerate(){
        experienceNow += 25;
        if(experienceNow >= 100){
            level++;
            experienceNow =0;
        }
    }
    public void dpGenerate(){
        dPoint += 100;
    }

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

}
