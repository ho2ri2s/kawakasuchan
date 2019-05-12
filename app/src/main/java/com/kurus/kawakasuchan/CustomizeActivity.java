package com.kurus.kawakasuchan;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class CustomizeActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), CustomizeActivity.this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(tabAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, false);

    }
}
