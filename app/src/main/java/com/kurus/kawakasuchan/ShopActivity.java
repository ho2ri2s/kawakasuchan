package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ShopActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

    }

    public void buy(View view){
        BuyDialogFragment dialog = new BuyDialogFragment();
        dialog.show(getSupportFragmentManager(), "buy");
    }
}
