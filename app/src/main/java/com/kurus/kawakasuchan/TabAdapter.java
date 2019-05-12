package com.kurus.kawakasuchan;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter{

    Activity activity;

    public TabAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        if(activity instanceof ShopActivity){
            switch (position) {
                case 0:
                    return new ShopClothesFragment();
                case 1:
                    return new ShopShoesFragment();
                case 2:
                    return new ShopInteriorFragment();
                default:
                    return null;
            }
        }else if(activity instanceof CustomizeActivity){
            switch (position) {
                case 0:
                    return new CustomizeClothesFragment();
                case 1:
                    return new CustomizeShoesFragment();
                case 2:
                    return new CustomizeInteriorFragment();
                default:
                    return null;
            }
        }else {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "服";
            case 1:
                return "靴";
            case 2:
                return "レイアウト";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
