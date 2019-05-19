package com.kurus.kawakasuchan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class BathActivity extends AppCompatActivity implements View.OnClickListener, CountDown.OnTickListener, CountDown.OnFinishListener {

    private EditText edtMinute;
    private TextView txtMinute;
    private Button btnEnter;
    private Button btnBath;
    private ImageView imgCharacter;

    private CountDown countDown;
    private Realm realm;
    private boolean tookABath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath);

        tookABath = false;

        edtMinute = findViewById(R.id.edtMinute);
        txtMinute = findViewById(R.id.txtMinute);
        btnEnter = findViewById(R.id.btnEnter);
        btnBath = findViewById(R.id.btnBath);
        imgCharacter = findViewById(R.id.imgCharacter);

        btnEnter.setOnClickListener(this);
        btnBath.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEnter:
                try{
                    txtMinute.setText(edtMinute.getText().toString());
                }catch (NumberFormatException e){
                    Toast.makeText(this, "数字を入力してね！", Toast.LENGTH_SHORT).show();
                }
                edtMinute.setKeyListener(null);
                btnBath.setVisibility(View.VISIBLE);
                btnBath.setOnClickListener(this);
                break;
            case R.id.btnBath:
                if(!tookABath){
                    //// TODO: 2019/05/18 お風呂画像用意
                    imgCharacter.setImageResource(R.drawable.question);
                    countDown = new CountDown(Long.parseLong(edtMinute.getText().toString()) * 60 * 1000, 60 * 1000);
                    countDown.setOnTickListener(this);
                    countDown.setOnFinishListener(this);
                    countDown.start();
                }else{
                    Intent intent = new Intent(BathActivity.this, MainActivity.class);
                    startActivity(intent);
                    realm = Realm.getDefaultInstance();
                    final Character realmCharacter = realm.where(Character.class).findFirst();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realmCharacter.setWetStatus(100);
                            realmCharacter.setWetStage(3);
                        }
                    });
                    realm.close();
                }

        }

    }

    @Override
    public void onFinish() {
        tookABath = true;
        btnBath.setText("あがる");
    }

    @Override
    public void onTick() {
        int remainingMinute;
        remainingMinute = Integer.parseInt(edtMinute.getText().toString());
        txtMinute.setText(String.valueOf(remainingMinute - 1));
    }
}
