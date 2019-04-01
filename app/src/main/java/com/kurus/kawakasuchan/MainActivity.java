package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    TextView txtDPoint, txtLevel;
    ImageView imgCharacter, imgDryer, imgNew;
    Button btnShopping, btnBath, btnDress;
    FrameLayout frameLayout;
    int dryerNumber = 0;
    float imgCharacterX = 0;
    float imgCharacterY = 0;
    boolean onImgCharacter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UIとの関連付け
        txtDPoint = (TextView)findViewById(R.id.txtDPoint);
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        imgCharacter = (ImageView)findViewById(R.id.imgCharacter);
        imgDryer = (ImageView)findViewById(R.id.imgDryer);
        btnShopping = (Button)findViewById(R.id.btnShopping);
        btnBath = (Button)findViewById(R.id.btnBath);
        btnDress = (Button)findViewById(R.id.btnDress);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);

        imgDryer.setOnTouchListener(this);

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

    //ドライヤーがタッチされた際の処理
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        view.startDrag(null, new View.DragShadowBuilder(view), (Object) view, 0);
        return false;
    }

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



    public void dryerInit(){
        dryerNumber = 0;
        ((FrameLayout)imgNew.getParent()).removeView(imgNew);

    }

}
