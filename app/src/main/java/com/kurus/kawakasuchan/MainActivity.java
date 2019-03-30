package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtDPoint, txtLevel;
    ImageView imgCharacter, imgDryer;
    Button btnShopping, btnBath, btnDress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDPoint = (TextView)findViewById(R.id.txtDPoint);
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        imgCharacter = (ImageView)findViewById(R.id.imgCharacter);
        imgDryer = (ImageView)findViewById(R.id.imgDryer);
        btnShopping = (Button)findViewById(R.id.btnShopping);
        btnBath = (Button)findViewById(R.id.btnBath);
        btnDress = (Button)findViewById(R.id.btnDress);
    }
}
