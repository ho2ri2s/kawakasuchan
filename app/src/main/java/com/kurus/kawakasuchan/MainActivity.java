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

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

    private TextView txtDPoint, txtLevel;
    private ImageView imgDryer, imgCharacter;
    private Button btnShopping, btnBath, btnDress;
    private ProgressBar experienceBar, statusBar;
    private SoundDetection soundDetection;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Character character;
    private FrameLayout frameLayout;
    private int experienceProgress, statusProgress;
    private int dryerWidth, dryerHeight;
    private int count;
    private float dryerX;
    private float dryerY;
    private boolean onImgCharacter;
    private boolean isDryer;
    private SharedPreferences sharedPreferences;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isDryer = false;
        dryerX = 0;
        dryerY = 0;
        experienceProgress = 0;
        statusProgress = 0;
        character = new Character();
        startTime = 0;
        onImgCharacter = false;

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
        btnBath.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBath:
                character.setWetStatus(100);
                character.setWetStage(3);
                statusProgress = character.getWetStatus();
                statusBar.setProgress(statusProgress);
                break;
        }
    }

    //他のアクティビティから帰ってきたときに呼ばれる
    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
        //ドライヤーの準備ができているときに録音処理を開始する
            soundDetection = new SoundDetection();
            soundDetection.setOnReachedVolumeListener(new SoundDetection.OnReachedVolumeListener() {
                //音を感知したら呼び出される
                public void onReachedVolume(short volume) {
                    //soundDetectionのスレッドからUIスレッドに描画更新処理を投げる
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(isDryer){
                                count++;
                                if(count % 20 == 0){
                                    character.drying();
                                    uiUpdate();
                                }
                            }
                        }
                    };
                    handler.post(runnable);
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
        //handlerとrunnableとの関係を切る
        handler.removeCallbacks(runnable);
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
            isDryer = true;
        }
    }


    //初期化
    public void dryerInit(){
        isDryer = false;
        ((FrameLayout)imgDryer.getParent()).removeView(imgDryer);
    }

    public void uiUpdate(){
        //フィールドの更新
        experienceProgress = character.getExperienceNow();
        statusProgress = character.getWetStatus();
        //UIの更新
        txtDPoint.setText(String.valueOf(character.getdPoint()));
        txtLevel.setText(String.valueOf(character.getLevel()));
        experienceBar.setProgress(experienceProgress);
        statusBar.setProgress(statusProgress);
    }

    public void readCharacterInformationFromSharedPreferences(){
        sharedPreferences = getSharedPreferences("characterInformation", MODE_PRIVATE);
        character.setdPoint(sharedPreferences.getInt("key_dPoint", 0));
        character.setExperienceNow(sharedPreferences.getInt("key_experienceNow", 0));
        character.setLevel(sharedPreferences.getInt("key_level", 1));
        character.setWetStage(sharedPreferences.getInt("key_wetStage", 3));
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

