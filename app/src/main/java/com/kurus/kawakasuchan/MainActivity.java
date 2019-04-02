package com.kurus.kawakasuchan;

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

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView txtDPoint, txtLevel;
    private ImageView imgCharacter, imgDryer, imgNew;
    private Button btnShopping, btnBath, btnDress;
    private FrameLayout frameLayout;
    private ProgressBar experienceBar, statusBar;
    private SoundDetection soundDetection;
    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask timerTask;
    private Character character;
    private int experienceProgress, statusProgress;
    private int dryerNumber;
    private float imgCharacterX;
    private float imgCharacterY;
    private boolean onImgCharacter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dryerNumber = 0;
        imgCharacterX = 0;
        imgCharacterY = 0;
        experienceProgress = 0;
        statusProgress = 0;
        character = new Character();

        //UIとの関連付け
        txtDPoint = (TextView)findViewById(R.id.txtDPoint);
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        imgCharacter = (ImageView)findViewById(R.id.imgCharacter);
        imgDryer = (ImageView)findViewById(R.id.imgDryer);
        btnShopping = (Button)findViewById(R.id.btnShopping);
        btnBath = (Button)findViewById(R.id.btnBath);
        btnDress = (Button)findViewById(R.id.btnDress);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        experienceBar = (ProgressBar)findViewById(R.id.experienceBar);
        statusBar = (ProgressBar)findViewById(R.id.statusBar);

        //ProgressBarの初期化
        experienceBar.setMax(100);
        experienceBar.setProgress(experienceProgress);
        statusBar.setMax(100);
        statusBar.setProgress(statusProgress);

        imgDryer.setOnTouchListener(this);
        //大元ドライヤーのイベント受信処理
        imgDryer.setOnDragListener(new View.OnDragListener(){
            @Override
            public boolean onDrag(View view, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        imgDryer.setAlpha(0.5f);
                        return true;
                        case DragEvent.ACTION_DROP:
                            if(dryerNumber != 0){
                                dryerInit();
                            }
                    case DragEvent.ACTION_DRAG_ENDED:
                        imgDryer.setAlpha(1.0f);
                }
                return true;
            }
        });

        //キャラクターのイベント受信処理
        imgCharacter.setOnDragListener(new View.OnDragListener(){
            @Override
            public boolean onDrag(View view, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        if(imgNew != null) {
                            imgNew.setAlpha(0.5f);
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        onImgCharacter = false;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        onImgCharacter = true;
                        break;
                    case DragEvent.ACTION_DROP:
                        imgCharacterX = event.getX();
                        imgCharacterY = event.getY();
                        if(dryerNumber != 0){
                            ((FrameLayout)imgNew.getParent()).removeView(imgNew);
                        }
                        addImage();
                        break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            if(imgNew != null) {
                                imgNew.setAlpha(1.0f);
                            }
                            break;

                }
                return true;
            }
        });
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
    }

    //ドライヤーがタッチされた際の処理
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        view.startDrag(null, new View.DragShadowBuilder(view), (Object) view, 0);
        return false;
    }

    //ドライヤー画像を生成
    public void addImage(){
        if(onImgCharacter){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgDryer.getWidth(), imgDryer.getHeight());
            imgNew = new ImageView(getApplicationContext());
            imgNew.setImageResource(R.drawable.dryer);

            frameLayout.addView(imgNew, layoutParams);
            imgNew.setTranslationX(imgCharacterX - imgDryer.getWidth() / 2);
            imgNew.setTranslationY(imgCharacterY - imgDryer.getHeight() / 2);

            imgNew.setOnTouchListener(this);
            dryerNumber++;
        }
    }
    //初期化
    public void dryerInit(){
        dryerNumber = 0;
        ((FrameLayout)imgNew.getParent()).removeView(imgNew);

    }

}
