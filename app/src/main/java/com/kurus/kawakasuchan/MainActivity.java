package com.kurus.kawakasuchan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1000;

    private TextView txtDPoint, txtLevel, txtExperience;
    private ImageView imgDryer, imgCharacter;
    private Button btnShopping, btnBath, btnCustomize;
    private ProgressBar experienceBar, statusBar;
    private SoundDetection soundDetection;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Character character;
    private FrameLayout frameLayout;
    private int count;
    private float dryerX;
    private float dryerY;
    private boolean onImgCharacter;
    private boolean isDryer;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        isDryer = false;
        dryerX = 0;
        dryerY = 0;
        character = new Character();
        onImgCharacter = false;

        //UIとの関連付け
        txtDPoint = findViewById(R.id.txtDPoint);
        txtLevel = findViewById(R.id.txtLevel);
        imgDryer = findViewById(R.id.imgDryer);
        btnShopping = findViewById(R.id.btnShopping);
        btnBath = findViewById(R.id.btnBath);
        btnCustomize = findViewById(R.id.btnCustomize);
        experienceBar = findViewById(R.id.experienceBar);
        statusBar = findViewById(R.id.statusBar);
        frameLayout = findViewById(R.id.frameLayout);
        imgCharacter = findViewById(R.id.imgCharacter);
        txtExperience = findViewById(R.id.txtExperience);

        imgDryer.setOnTouchListener(this);
        frameLayout.setOnDragListener(this);
        btnBath.setOnClickListener(this);
        btnShopping.setOnClickListener(this);
        btnCustomize.setOnClickListener(this);
        //マイク使用許可を乞う
        requestPermission();

        //始めてアプリ起動時
        if (realm.where(Character.class).findFirst() == null) {
            //キャラ初期化
            initCharacter();
        }

    }

    //他のアクティビティから帰ってきたときに呼ばれる
    @Override
    protected void onResume() {
        super.onResume();

        //端末に保存されているキャラクター情報を読み込む
        showData();

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
                        if (isDryer) {
                            count++;
                            if (count % 20 == 0) {
                                drying();
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

        saveValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ありがとう！！", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.startDrag(null, new View.DragShadowBuilder(view), (Object) view, 0);
                break;
        }
        return false;   //他のリスナイベントを発生させる(false)
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
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
        switch (view.getId()) {
            case R.id.btnBath:
                final Character character = realm
                        .where(Character.class)
                        .equalTo("isCharacter", true)
                        .findFirst();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        character.setWetStatus(100);
                        character.setWetStage(3);
                    }
                });

                uiUpdate();

                break;
            case R.id.btnShopping:
                Intent shopIntent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(shopIntent);
                break;
            case R.id.btnCustomize:
                Intent customizeIntent = new Intent(MainActivity.this, CustomizeActivity.class);
                startActivity(customizeIntent);
                break;
        }
    }


    //    ドライヤー画像を生成
    public void addImage() {
        if (onImgCharacter) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgDryer.getWidth(), imgDryer.getHeight());
            imgDryer = new ImageView(getApplicationContext());
            imgDryer.setImageResource(R.drawable.dryer);

            frameLayout.addView(imgDryer, layoutParams);
            imgDryer.setTranslationX(dryerX - 100); //imgDryer.getHeight() / 2 は,ずれちゃう
            imgDryer.setTranslationY(dryerY - 100); //imgDryer.getHeight() / 2 は,ずれちゃう

            imgDryer.setOnTouchListener(this);
            isDryer = true;
        }
    }


    //初期化
    public void dryerInit() {
        isDryer = false;
        ((FrameLayout) imgDryer.getParent()).removeView(imgDryer);
    }

    public void drying() {

        final Character character = realm
                .where(Character.class)
                .equalTo("isCharacter", true)
                .findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //髪が濡れている状態なら3秒に1メーター減らし、濡段階が1段階減るごとに経験値とDポイントをゲットする。
                if (character.getWetStage() > 0) {
                    character.setWetStatus(character.getWetStatus() - 1);
                    if (character.getWetStatus() % 25 == 0) {
                        //1段階下がるごとに
                        character.setWetStage(character.getWetStatus() - 1);
                        //経験値と
                        character.setExperienceNow(character.getExperienceNow() + 25);
                        txtExperience.setAlpha(1.0f);
                        txtExperience.animate().alpha(0f).setDuration(3000);
                        if (character.getExperienceNow() >= 100) {
                            character.setLevel(character.getLevel() + 1);
                            character.setExperienceNow(character.getExperienceNow() - 100);
                            Toast.makeText(MainActivity.this, "LevelUp!!", Toast.LENGTH_LONG).show();
                        }
                        //ポイントが得られる
                        character.setdPoint(character.getdPoint() + 100);
                    }
                }
            }
        });

    }

    public void uiUpdate() {

        final Character character = realm
                .where(Character.class)
                .equalTo("isCharacter", true)
                .findFirst();

        txtDPoint.setText(String.valueOf(character.getdPoint()));
        txtLevel.setText(String.valueOf(character.getLevel()));
        experienceBar.setProgress(character.getExperienceNow());
        statusBar.setProgress(character.getWetStatus());

    }


    public void showData() {
        Character character = realm.where(Character.class).equalTo("isCharacter", true).findFirst();
        Clothes clothes = realm.where(Clothes.class).findFirst();

        //キャラ情報
        txtDPoint.setText(String.valueOf(character.getdPoint()));
        txtLevel.setText(String.valueOf(character.getLevel()));
        statusBar.setProgress(character.getWetStatus());
        experienceBar.setProgress(character.getExperienceNow());

        //服情報
        if(clothes.getBalloonDressStatus() == 2){
            imgCharacter.setImageResource(R.drawable.balloon_dress);
        }else if(clothes.getShirtDressStatus() == 2){
            imgCharacter.setImageResource(R.drawable.shirt_dress);
        }else if(clothes.getCasualClothesStatus() == 2){
            imgCharacter.setImageResource(R.drawable.shirt_dress);
        }
    }

    public void saveValue() {
        final Character character = realm
                .where(Character.class)
                .equalTo("isCharacter", true)
                .findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                character.setdPoint(Integer.parseInt(txtDPoint.getText().toString()));
                character.setLevel(Integer.parseInt(txtLevel.getText().toString()));
                character.setWetStatus(statusBar.getProgress());
                character.setExperienceNow(experienceBar.getProgress());
                character.setWetStage(statusBar.getProgress() / 25);
            }
        });
    }

    public void initCharacter() {
        //モデルのコンストラクタを呼びたいため、createObjectではなくこの方法
        final Character character = new Character();
        final Clothes clothes = new Clothes();
        final Shoes shoes = new Shoes();
        final Interior interior = new Interior();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealm(character);
                realm.copyToRealm(clothes);
                realm.copyToRealm(shoes);
                realm.copyToRealm(interior);

            }
        });
        uiUpdate();
    }

}

