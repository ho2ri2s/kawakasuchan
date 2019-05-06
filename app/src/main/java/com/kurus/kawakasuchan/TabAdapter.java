package com.kurus.kawakasuchan;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ClothesFragment();
            case 1:
                return new ShoesFragment();
            case 2:
                return new InteriorFragment();
            default:
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
