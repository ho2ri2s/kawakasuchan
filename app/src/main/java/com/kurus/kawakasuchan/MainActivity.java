package com.kurus.kawakasuchan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {

    private TextView txtDPoint, txtLevel;
    private ImageView imgDryer, imgCharacter;
    private Button btnShopping, btnBath, btnDress;
    private ProgressBar experienceBar, statusBar;
    private SoundDetection soundDetection;
    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask timerTask;
    private Character character;
    private FrameLayout frameLayout;
    private int experienceProgress, statusProgress;
    private int dryerWidth, dryerHeight;
    private int dryerNumber;
    private float dryerX;
    private float dryerY;
    private boolean onImgCharacter = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dryerNumber = 0;
        dryerX = 0;
        dryerY = 0;
        experienceProgress = 0;
        statusProgress = 0;
        character = new Character();

        //UIとの関連付け
        txtDPoint = (TextView)findViewById(R.id.txtDPoint);
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        imgDryer = (ImageView)findViewById(R.id.imgDryer);
        btnShopping = (Button)findViewById(R.id.btnShopping);
        btnBath = (Button)findViewById(R.id.btnBath);
        btnDress = (Button)findViewById(R.id.btnDress);
        experienceBar = (ProgressBar)findViewById(R.id.experienceBar);
        statusBar = (ProgressBar)findViewById(R.id.statusBar);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        imgCharacter = (ImageView)findViewById(R.id.imgCharacter);

        dryerWidth = imgDryer.getWidth();
        dryerHeight = imgDryer.getHeight();


        //ProgressBarの初期化
        experienceBar.setMax(100);
        experienceBar.setProgress(experienceProgress);
        statusBar.setMax(100);
        statusBar.setProgress(statusProgress);


        imgDryer.setOnTouchListener(this);
        frameLayout.setOnDragListener(this);
//        imgCharacter.setOnDragListener(this);

        //端末に保存されているキャラクター情報を読み込む
        readCharacterInformationFromSharedPreferences();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                view.startDrag(null, new View.DragShadowBuilder(view), (Object)view, 0);
                break;
        }
        return false;   //他のリスナイベントを発生させる(false)
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()){
            case DragEvent.ACTION_DRAG_STARTED:
                onImgCharacter = true;
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                onImgCharacter = false;
                break;
            case DragEvent.ACTION_DROP:
                dryerX = event.getX();
                dryerY = event.getY();
                dryerInit();
                addImage();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                imgDryer.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }

    //他のアクティビティから帰ってきたときに呼ばれる
    @Override
    protected void onResume() {
        super.onResume();
        soundDetection = new SoundDetection();
        soundDetection.setOnReachedVolumeListener(new SoundDetection.OnReachedVolumeListener(){
            //音を感知したら呼び出される
            public void onReachedVolume(short volume){
                //別スレッドからUIスレッドに処理を投げる
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 2019/04/01 ドライヤーやキャラクターのアニメーション実装
                        if (timer == null){
                            timer = new Timer();
                            //3秒おきに乾かす処理
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            character.drying();
                                            txtDPoint.setText(String.valueOf(character.getdPoint()));
                                            txtLevel.setText(String.valueOf(character.getLevel()));
                                            experienceProgress = character.getExperienceNow();
                                            experienceBar.setProgress(experienceProgress);
                                            statusProgress = character.getWetStatus();
                                            statusBar.setProgress(statusProgress);
                                        }
                                    });
                                }
                            };
                            timer.schedule(timerTask, 0, 3000);
                        }
                    }
                });
            }
        });
        //別のスレッドとして録音開始
        new Thread(soundDetection).start();
    }

    //他のアクティビティに移ったときに呼ばれるメソッド
    @Override
    protected void onPause() {
        super.onPause();
        //録音停止
        soundDetection.stop();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        //キャラクター情報を端末に保存する
        saveCharacterInformationFromSharedPreferences();

    }


//    ドライヤー画像を生成
    public void addImage(){
        if(onImgCharacter){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgDryer.getWidth(), imgDryer.getHeight());
            imgDryer = new ImageView(getApplicationContext());
            imgDryer.setImageResource(R.drawable.dryer);

            frameLayout.addView(imgDryer, layoutParams);
            imgDryer.setTranslationX(dryerX  - 100); //imgDryer.getHeight() / 2 は,ずれちゃう
            imgDryer.setTranslationY(dryerY  - 100); //imgDryer.getHeight() / 2 は,ずれちゃう

            imgDryer.setOnTouchListener(this);
            dryerNumber++;
        }
    }


    //初期化
    public void dryerInit(){
        dryerNumber = 0;
        ((FrameLayout)imgDryer.getParent()).removeView(imgDryer);

    }

    public void readCharacterInformationFromSharedPreferences(){
        sharedPreferences = getSharedPreferences("characterInformation", MODE_PRIVATE);
        character.setdPoint(sharedPreferences.getInt("key_dPoint", 0));
        character.setExperienceNow(sharedPreferences.getInt("key_experienceNow", 0));
        character.setLevel(sharedPreferences.getInt("key_level", 1));
        character.setWetStage(sharedPreferences.getInt("key_wetStage", 4));
        character.setWetStatus(sharedPreferences.getInt("key_wetAStatus", 100));
    }

    public void saveCharacterInformationFromSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key_dPoint", character.getdPoint());
        editor.putInt("key_experienceNow", character.getExperienceNow());
        editor.putInt("key_level", character.getLevel());
        editor.putInt("key_wetStage", character.getWetStage());
        editor.putInt("key_wetAStatus", character.getWetStatus());
    }



}
