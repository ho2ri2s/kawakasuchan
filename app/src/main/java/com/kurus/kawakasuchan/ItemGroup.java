package com.kurus.kawakasuchan;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ItemGroup extends RealmObject {

    private RealmList<Clothes> clothes;
    private RealmList<Interior> interiors;

    public ItemGroup(){
    }

    public RealmList<Clothes> getClothes() {
        return clothes;
    }

    public void setClothes(RealmList<Clothes> clothes) {
        this.clothes = clothes;
    }

    public RealmList<Interior> getInteriors() {
        return interiors;
    }

    public void setInteriors(RealmList<Interior> interiors) {
        this.interiors = interiors;
    }
}
