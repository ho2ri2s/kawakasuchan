package com.kurus.kawakasuchan;

public class Dryer {
    private static int instanceNumber = 0;
    Dryer(){
        instanceNumber++;
    }



    public static int getInstanceNumber(){
        return instanceNumber;
    }
}
