package com.kurus.kawakasuchan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class BathActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtMinute;
    private TextView txtMinute;
    private Button btnEnter;
    private Button btnBath;
    private ImageView imgCharacter;

    private Timer timer;
    private Handler handler;
    private TimerTask timerTask;
    private CountDown countDown;
    private Realm realm;
    private boolean tookABath;
    private int remainingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath);

        tookABath = false;
        handler = new Handler();

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
        switch (view.getId()) {
            case R.id.btnEnter:
                try {
                    remainingTime = Integer.parseInt(edtMinute.getText().toString());
                    Log.d("MYTAG", remainingTime + "");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "数字を入力してね！", Toast.LENGTH_SHORT).show();
                }
                txtMinute.setText(String.valueOf(remainingTime));
                edtMinute.setKeyListener(null);
                btnBath.setVisibility(View.VISIBLE);
                btnBath.setOnClickListener(this);
                break;
            case R.id.btnBath:
                final Calendar calendar1 = Calendar.getInstance();
                final Date startDate = calendar1.getTime();
                Log.d("MYTAG", startDate + "");
                if (!tookABath) {
                    //// TODO: 2019/05/18 お風呂画像用意
                    imgCharacter.setImageResource(R.drawable.question);
//                    countDown = new CountDown(Long.parseLong(edtMinute.getText().toString()) * 60 * 1000, 60 * 1000);
//                    countDown.setOnTickListener(this);
//                    countDown.setOnFinishListener(this);
//                    countDown.start();
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO: 2019/05/19 カウントダウンが終わる処理 
                                    Calendar calendar2 = Calendar.getInstance();
                                    calendar2.add(Calendar.MINUTE, -Integer.parseInt(edtMinute.getText().toString()));
                                    Date judgeTime = calendar2.getTime();
                                    Log.d("MYTAG", judgeTime + "");

                                    remainingTime--;
                                    Log.d("MYTAG", remainingTime + "");

                                    txtMinute.setText(String.valueOf(remainingTime));

                                    if (judgeTime.after(startDate)) {
                                        tookABath = true;
                                        btnBath.setText("あがる");
                                    }
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask, 0, 60 * 1000);
                } else {
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
    protected void onResume() {
        super.onResume();
        if (timer != null && !tookABath) {
            timer.schedule(timerTask, 0, 60 * 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    //    @Override
//    public void onFinish() {
//
//    }
//
//    @Override
//    public void onTick() {
//        int remainingMinute;
//        remainingMinute = Integer.parseInt(edtMinute.getText().toString());
//        txtMinute.setText(String.valueOf(remainingMinute - 1));
//    }
}
