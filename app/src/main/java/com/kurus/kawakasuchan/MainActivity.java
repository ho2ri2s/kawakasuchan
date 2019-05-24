package com.kurus.kawakasuchan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1000;
    private static final float LIVING__WIDTH = 1200.0f;
    private static final float LIVING__HEIGHT = 1920.0f;

    private TextView txtDPoint, txtLevel, txtExperience;
    private ImageButton btnDryerBack;
    private ImageView imgCharacter;
    private ImageView imgDryer;
    private Button btnShopping, btnBath, btnCustomize;
    private ProgressBar experienceBar, statusBar;
    private SoundDetection soundDetection;
    private Handler handler = new Handler();
    private Runnable runnable;
    private FrameLayout frameLayout;
    private ConstraintLayout constraintLayout;
    private int count;
    private int constraintWidth;
    private int constraintHeight;
    private float dryerX;
    private float dryerY;
    private boolean onImgCharacter;
    private boolean isDryer;
    private Realm realm;
    private GifImageView[] gifDrops = new GifImageView[4];
    private GifImageView gifWind;
    private GifDrawable[] dropDrawables = new GifDrawable[4];


    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isDryer = false;
        dryerX = 0;
        dryerY = 0;
        onImgCharacter = false;

        //UIとの関連付け
        txtDPoint = findViewById(R.id.txtDPoint);
        txtLevel = findViewById(R.id.txtLevel);
        imgDryer = findViewById(R.id.imgDryer);
        btnDryerBack = findViewById(R.id.btnDryerBack);
        btnShopping = findViewById(R.id.btnShopping);
        btnBath = findViewById(R.id.btnBath);
        btnCustomize = findViewById(R.id.btnCustomize);
        experienceBar = findViewById(R.id.experienceBar);
        statusBar = findViewById(R.id.statusBar);
        frameLayout = findViewById(R.id.frameLayout);
        constraintLayout = findViewById(R.id.constraintLayout);
        imgCharacter = findViewById(R.id.imgCharacter);
        txtExperience = findViewById(R.id.txtExperience);
        gifWind = findViewById(R.id.wind);
        for (int i = 0; i < 4; i++) {
            gifDrops[i] = findViewById(getResources().getIdentifier("drop" + i, "id", getPackageName()));
            dropDrawables[i] = (GifDrawable) gifDrops[i].getDrawable();
        }

        imgDryer.setOnTouchListener(this);
        btnDryerBack.setOnClickListener(this);
        frameLayout.setOnDragListener(this);
        btnBath.setOnClickListener(this);
        btnShopping.setOnClickListener(this);
        btnCustomize.setOnClickListener(this);
        //マイク使用許可を乞う
        requestPermission();

        realm = Realm.getDefaultInstance();
        //始めてアプリ起動時
        if (realm.where(Character.class).findFirst() == null) {
            //キャラ初期化
            init();
        }

    }

    //他のアクティビティから帰ってきたときに呼ばれる
    @Override
    protected void onResume() {
        super.onResume();

        //端末に保存されているキャラクター情報を読み込む
        showData();

        //録音OKなら音感センサー起動
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            soundDetection = new SoundDetection();
            soundDetection.setOnReachedVolumeListener(new SoundDetection.OnReachedVolumeListener() {
                //音を感知したら呼び出される
                public void onReachedVolume(short volume) {
                    //soundDetectionのスレッドからUIスレッドに描画更新処理を投げる
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (isDryer) {
                                for (int i = 0; i < dropDrawables.length; i++) {
                                    dropDrawables[i].start();
                                }
                                count++;
                                if (count % 15 == 0) {
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
    }

    //他のアクティビティに移ったときに呼ばれるメソッド
    @Override
    protected void onPause() {
        super.onPause();
        //録音停止
        if(soundDetection != null){
            soundDetection.stop();
        }
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
                isDryer = true;
                btnDryerBack.setImageResource(R.drawable.ic_close_blackdp);
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
                addDryerImage();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
//                imgDryer.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBath:
                Intent bathIntent = new Intent(MainActivity.this, BathActivity.class);
                startActivity(bathIntent);
                break;
            case R.id.btnShopping:
                Intent shopIntent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(shopIntent);
                break;
            case R.id.btnCustomize:
                Intent customizeIntent = new Intent(MainActivity.this, CustomizeActivity.class);
                startActivity(customizeIntent);
                break;
            case R.id.btnDryerBack:
                resetDryer();
                break;
        }
    }


    //    ドライヤー画像を生成
    public void addDryerImage() {
        if (onImgCharacter) {


            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgDryer.getWidth(), imgDryer.getHeight());
            ((FrameLayout) imgDryer.getParent()).removeView(imgDryer);
            imgDryer = new ImageView(getApplicationContext());
            imgDryer.setImageResource(R.drawable.dryer);
            frameLayout.addView(imgDryer, layoutParams);
            imgDryer.setTranslationX(dryerX - 100); //imgDryer.getHeight() / 2 は,ずれちゃう 検討課題
            imgDryer.setTranslationY(dryerY - 100); //imgDryer.getHeight() / 2 は,ずれちゃう 検討課題

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(gifWind.getWidth(), gifWind.getHeight());
            Log.d("MYTAG", gifWind.getWidth() + " wi, he " + gifWind.getHeight());
            ((FrameLayout) gifWind.getParent()).removeView(gifWind);
            gifWind = new GifImageView(this);
            gifWind.setImageResource(R.drawable.wind);
            frameLayout.addView(gifWind, params);
            gifWind.setTranslationX(imgDryer.getX() - 100);
            gifWind.setTranslationY(imgDryer.getY() + 25);


            imgDryer.setOnTouchListener(this);
        }

    }


    //初期化
    public void resetDryer() {
        isDryer = false;

        ((FrameLayout) imgDryer.getParent()).removeView(imgDryer);
        gifWind.setVisibility(View.INVISIBLE);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imgDryer.getWidth(), imgDryer.getHeight());
        layoutParams.gravity = Gravity.RIGHT;
        imgDryer = new ImageView(getApplicationContext());
        imgDryer.setImageResource(R.drawable.dryer);
        frameLayout.addView(imgDryer, layoutParams);

        this.imgDryer.setOnTouchListener(this);

        Character realmCharacter = realm.where(Character.class).findFirst();
        for (int i = 0; i < realmCharacter.getWetStage(); i++) {
            dropDrawables[i].stop();
        }

        btnDryerBack.setImageDrawable(null);

    }

    public void drying() {
        final Character realmCharacter = realm.where(Character.class).equalTo("isCharacter", true).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //髪が濡れている状態なら3秒に1メーター減らし、濡段階が1段階減るごとに経験値とDポイントをゲットする。
                if (realmCharacter.getWetStage() > 0) {
                    realmCharacter.setWetStatus(realmCharacter.getWetStatus() - 1);
                    if (realmCharacter.getWetStatus() % 25 == 0) {
                        Log.d("MYTAG", realmCharacter.getWetStatus() + "status  " + realmCharacter.getWetStage() + " stage");
                        //1段階下がるごとに
                        realmCharacter.setWetStage(realmCharacter.getWetStage() - 1);
                        //水滴が消え
                        fadeOutAndHideImage(gifDrops[realmCharacter.getWetStage()]);
                        //経験値と
                        realmCharacter.setExperienceNow(realmCharacter.getExperienceNow() + 25);
                        txtExperience.setAlpha(1.0f);
                        txtExperience.animate().alpha(0f).setDuration(3000);
                        if (realmCharacter.getExperienceNow() >= 100) {
                            realmCharacter.setLevel(realmCharacter.getLevel() + 1);
                            realmCharacter.setExperienceNow(realmCharacter.getExperienceNow() - 100);
                            Toast.makeText(MainActivity.this, "LevelUp!!", Toast.LENGTH_LONG).show();
                        }
                        //ポイントが得られる
                        realmCharacter.setdPoint(realmCharacter.getdPoint() + 100);
                    }
                }
            }
        });

    }

    public void uiUpdate() {

        final Character realmCharacter = realm.where(Character.class).equalTo("isCharacter", true).findFirst();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));
        experienceBar.setProgress(realmCharacter.getExperienceNow());
        statusBar.setProgress(realmCharacter.getWetStatus());

        if (realmCharacter.getWetStage() == 0) {
            if (realmCharacter.getClothes() != null) {
                imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
            } else {
                imgCharacter.setImageResource(R.drawable.naked_girl);
            }
        }

    }


    public void showData() {
        Character realmCharacter = realm.where(Character.class).equalTo("isCharacter", true).findFirst();

        //キャラ情報
        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));
        statusBar.setProgress(realmCharacter.getWetStatus());
        experienceBar.setProgress(realmCharacter.getExperienceNow());

        //濡れor乾いているキャラクターの画像をセット
        if (realmCharacter.getClothes() == null) {
            //服なし&乾いている
            if (realmCharacter.getWetStage() == 0)
                imgCharacter.setImageResource(R.drawable.naked_girl);
        } else {
            //服あり&乾いている
            if (realmCharacter.getWetStage() == 0) {
                imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
            } else {
                imgCharacter.setImageResource(realmCharacter.getClothes().getWetCharacterResourceId());
            }
        }

        //水滴を可視化
        for (int i = 0; i < realmCharacter.getWetStage(); i++) {
            gifDrops[i].setVisibility(View.VISIBLE);
            dropDrawables[i].stop();
        }
        Log.d("MYTAG", realmCharacter.getWetStage() + "drop数");

        // インテリアを配置する
        RealmList<Interior> realmInteriors = realmCharacter.getInteriors();
        if (realmInteriors.size() > 0) {
            for (int i = 0; i < realmInteriors.size(); i++) {
                addInteriorImage(realmInteriors.get(i));
            }
        }
        //ドライヤー・キャラクターを常に前面に持ってくる
        LinearLayout origin = findViewById(R.id.origin);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            origin.requestLayout();
            origin.invalidate();
        }
        origin.bringToFront();
        origin.invalidate();
    }


    public void addInteriorImage(final Interior interior) {
        //適当に配置
        final ImageView addImage = new ImageView(MainActivity.this);
        //配置転換
        ViewTreeObserver observer = constraintLayout.getViewTreeObserver();
//        if (globalLayoutListener != null) {
//            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
//        }

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //この中で幅と高さを取得
                constraintWidth = constraintLayout.getWidth();
                constraintHeight = constraintLayout.getHeight();

                //画像詳細を設定
                final ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        (int) (interior.getWidth() * constraintWidth / LIVING__WIDTH),
                        (int) (interior.getHeight() * constraintHeight / LIVING__HEIGHT));
                addImage.setImageResource(interior.getResourceId());
                addImage.setLayoutParams(params);
                addImage.setTranslationX(interior.getX() * constraintWidth / LIVING__WIDTH - addImage.getLayoutParams().width / 2);
                addImage.setTranslationY(interior.getY() * constraintHeight / LIVING__HEIGHT - addImage.getLayoutParams().height / 2);

                //一度呼んだら二度と呼ばない
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        };

        constraintLayout.addView(addImage);
        observer.addOnGlobalLayoutListener(globalLayoutListener);
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
                character.setExperienceNow(experienceBar.getProgress());
            }
        });
    }

    public void init() {
        //モデルのコンストラクタを呼びたいため、createObjectではなくこの方法
        final Character character = new Character();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealm(character);
                //服を初期化
                ItemGroup itemGroup = realm.createObject(ItemGroup.class);
                itemGroup.getClothes().add(new Clothes("バルーンワンピース", R.drawable.balloon_dress_only, R.drawable.balloon_dress, R.drawable.balloon_dress_wet, 1000, false));
                itemGroup.getClothes().add(new Clothes("シャツワンピース", R.drawable.shirt_dress_only, R.drawable.shirt_dress, R.drawable.shirt_dress_wet, 2000, false));
                itemGroup.getClothes().add(new Clothes("カジュアル服", R.drawable.casual_clothes_only, R.drawable.casual_clothes, R.drawable.casual_clothes_wet, 3000, false));
                //インテリアも初期化
                itemGroup.getInteriors().add(new Interior("ベッド", R.drawable.bed, 550, 250, 284, 1412, 700, false));
                itemGroup.getInteriors().add(new Interior("窓", R.drawable.window, 550, 400, 660, 530, 1500, false));
                itemGroup.getInteriors().add(new Interior("化粧台", R.drawable.mirror, 200, 350, 280, 1005, 3000, false));
                itemGroup.getInteriors().add(new Interior("本棚", R.drawable.bookshelf, 280, 400, 636, 970, 5000, false));
                itemGroup.getInteriors().add(new Interior("テレビ", R.drawable.tv, 364, 273, 1018, 1045, 10000, false));

            }
        });
        uiUpdate();
    }

    private void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        img.startAnimation(fadeOut);
    }


}

