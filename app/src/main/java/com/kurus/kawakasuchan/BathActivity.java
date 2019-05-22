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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class BathActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtMinute;
    private TextView txtMinute;
    private Button btnEnter;
    private Button btnBath;
    private ImageView imgCharacter;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture future;
    private Handler handler;
    private Runnable task;
    private Date startDate;
    private Date currentDate;
    private Realm realm;

    private boolean tookABath;
    private int setTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath);

        tookABath = false;
        handler = new Handler();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        edtMinute = findViewById(R.id.edtMinute);
        txtMinute = findViewById(R.id.txtMinute);
        btnEnter = findViewById(R.id.btnEnter);
        btnBath = findViewById(R.id.btnBath);
        imgCharacter = findViewById(R.id.imgCharacter);

        btnEnter.setOnClickListener(this);
        btnBath.setVisibility(View.INVISIBLE);

        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnter:
                try {
                    setTime = Integer.parseInt(edtMinute.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "数字を入力してね！", Toast.LENGTH_SHORT).show();
                }
                txtMinute.setText(String.valueOf(setTime));
                edtMinute.setKeyListener(null);
                btnEnter.setVisibility(View.GONE);
                btnBath.setVisibility(View.VISIBLE);
                btnBath.setOnClickListener(this);
                break;
            case R.id.btnBath:
                if (!tookABath) {
                    imgCharacter.setImageResource(R.drawable.ohuro);
                    btnBath.setVisibility(View.GONE);
                    final Calendar calendar1 = Calendar.getInstance();
                    startDate = calendar1.getTime();
                    Log.d("MYTAG", startDate + " startDate");

                    task = new MyTask();
                    future = scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);

                } else {
                    Intent intent = new Intent(BathActivity.this, MainActivity.class);
                    startActivity(intent);
                    final Character realmCharacter = realm.where(Character.class).findFirst();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realmCharacter.setWetStatus(100);
                            realmCharacter.setWetStage(3);
                        }
                    });
                }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (future != null) {
            imgCharacter.setImageResource(R.drawable.ohuro);

            Calendar calendar2 = Calendar.getInstance();
            Date currentDate = calendar2.getTime();

            long elapsedTime = (currentDate.getTime() - startDate.getTime()) / (1000 * 60);
            Log.d("MYTAG", elapsedTime + "resume");
            if (elapsedTime >= setTime) {
                tookABath = true;
                txtMinute.setText("0");
                btnBath.setText("あがる");
                btnBath.setVisibility(View.VISIBLE);
                //繰り返し終了
                scheduler.shutdown();
            } else {
                txtMinute.setText(String.valueOf(elapsedTime));
                future = scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
            }
        }
        showData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MYTAG", "destroy");
        if(scheduler != null){
            scheduler.shutdown();
        }
        realm.close();
    }

    private void showData() {
        Character realmCharacter = realm.where(Character.class).equalTo("isCharacter", true).findFirst();
        imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
    }

    class MyTask implements Runnable {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO: 2019/05/19 カウントダウンが終わる処理
                    Calendar calendar2 = Calendar.getInstance();
                    currentDate = calendar2.getTime();

                    long elapsedTime = ((currentDate.getTime() - startDate.getTime()) / (1000 * 60));

                    Log.d("MYTAG", currentDate + " currentDate");
                    Log.d("MYTAG", elapsedTime + " elapseTime");


                    txtMinute.setText(String.valueOf(setTime - elapsedTime));

                    if (elapsedTime >= setTime) {
                        tookABath = true;
                        btnBath.setText("あがる");
                        btnBath.setVisibility(View.VISIBLE);
                        //繰り返し終了
                        scheduler.shutdown();
                    }
                }
            });

        }
    }


}




