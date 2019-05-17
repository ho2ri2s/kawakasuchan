package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;

public class CustomizeInteriorFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private static final float LIVING__WIDTH = 1200.0f;
    private static final float LIVING__HEIGHT = 1920.0f;

    private Realm realm;
    private TextView txtChose;
    private TextView txtDPoint;
    private TextView txtLevel;
    private ImageView[] imgInterior = new ImageView[5];
    private int[] ids = {R.id.imgBed, R.id.imgWindow, R.id.imgMirror, R.id.imgBookshelf, R.id.imgTelevision};
    private FloatingActionButton fab;
    private FrameLayout frameLayout;

    private HashMap<String, ImageView> hashMap;
    private int frameWidth;
    private int frameHeight;
    private int choseNumber;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    public CustomizeInteriorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        choseNumber = -1;
        txtChose = new TextView(getContext());
        hashMap = new HashMap<String, ImageView>();

        View view = inflater.inflate(R.layout.fragment_interior, container, false);
        for (int i = 0; i < imgInterior.length; i++) {
            imgInterior[i] = view.findViewById(ids[i]);
        }

        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);
        fab = view.findViewById(R.id.floatingActionButton);
        frameLayout = view.findViewById(R.id.frameLayout);


        fab.setOnClickListener(this);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onClick(View view) {
        ItemGroup realmItemGroup;
        Interior realmInterior;
        Character realmCharacter;
        switch (view.getId()) {
            case R.id.imgBed:
            case R.id.imgWindow:
            case R.id.imgMirror:
            case R.id.imgBookshelf:
            case R.id.imgTelevision:
                realmItemGroup = realm.where(ItemGroup.class).findFirst();
                realmCharacter = realm.where(Character.class).findFirst();
                if (choseNumber != -1) {
                    //1つ前に選択されていたインテリアに関しての処理
                    imgInterior[choseNumber].setBackground(null);
                    realmInterior = realmItemGroup.getInteriors().get(choseNumber);
                    if(!realmCharacter.getInteriors().contains(realmInterior)){
                        frameLayout.removeView(hashMap.get(realmInterior.getName()));
                    }
                }

                choseNumber = Integer.parseInt(view.getTag().toString());
                imgInterior[choseNumber].setBackground(getResources().getDrawable(R.drawable.text_border));
                realmInterior = realmItemGroup.getInteriors().get(choseNumber);

                if(!realmCharacter.getInteriors().contains(realmInterior)){
                    addImage(realmInterior);
                }

                txtChose.setText(realmInterior.getName());
                break;
            case R.id.floatingActionButton:
                realmCharacter = realm.where(Character.class).findFirst();
                realmItemGroup = realm.where(ItemGroup.class).findFirst();
                realmInterior = realmItemGroup.getInteriors().get(choseNumber);
                if (txtChose.getText().toString() != "") {
                    BuyDialogFragment buyDialogFragment;
                    if (!realmCharacter.getInteriors().contains(realmInterior)) {
                        buyDialogFragment = new BuyDialogFragment().newInstance(
                                CustomizeInteriorFragment.this,
                                "設置",
                                txtChose.getText() + "設置しますか？");
                    } else {
                        buyDialogFragment = new BuyDialogFragment().newInstance(
                                CustomizeInteriorFragment.this,
                                "片付け",
                                txtChose.getText() + "片付けますか？");
                    }
                    buyDialogFragment.show(getFragmentManager(), "buy");
                } else {
                    Toast.makeText(getContext(), "アイテムを選択してください", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void showData() {
        fab.setImageResource(R.drawable.ic_check);

        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));

        // 持っているインテリアを配置する
        RealmList<Interior> realmInteriors = realmCharacter.getInteriors();
        if (realmInteriors.size() > 0) {
            for (int i = 0; i < realmInteriors.size(); i++) {
                addImage(realmInteriors.get(i));
            }
        }

        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
        RealmList<Interior> interiors = realmItemGroup.getInteriors();
        for (int i = 0; i < interiors.size(); i++) {
            //インテリアを所持しているならSoldOutに、所持していないなら選択可
            if (interiors.get(i).getIsHaving() == true) {
                imgInterior[i].setImageResource(interiors.get(i).getResourceId());
                imgInterior[i].setOnClickListener(this);
            } else {
                imgInterior[i].setImageResource(R.drawable.question);
            }
        }
    }


    private void addImage(final Interior interior) {
        //適当に配置
        final ImageView addImage = new ImageView(getContext());
        addImage.setVisibility(View.GONE);

        //配置転換
        ViewTreeObserver observer = frameLayout.getViewTreeObserver();

//        if(globalLayoutListener != null){
//            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
//        }

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //この中で幅と高さを取得
                frameWidth = frameLayout.getWidth();
                frameHeight = frameLayout.getHeight();

                //画像詳細を設定
                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        (int) (interior.getWidth() * frameWidth / LIVING__WIDTH),
                        (int) (interior.getHeight() * frameHeight / LIVING__HEIGHT));
                addImage.setImageResource(interior.getResourceId());
                addImage.setLayoutParams(params);
                addImage.setTranslationX(interior.getX() * frameWidth / LIVING__WIDTH - addImage.getLayoutParams().width / 2);
                addImage.setTranslationY(interior.getY() * frameHeight / LIVING__HEIGHT - addImage.getLayoutParams().height / 2);

                //一度呼んだら二度と呼ばない
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                addImage.setVisibility(View.VISIBLE);
            }
        };
        frameLayout.addView(addImage);
        hashMap.put(interior.getName(), addImage);
        Log.d("MYTAG", addImage + "");

        observer.addOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public void onDialogPositiveButtonClicked() {
        final Character realmCharacter = realm.where(Character.class).findFirst();
        final ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
        final Interior realmInterior = realmItemGroup.getInteriors().where().equalTo("name", txtChose.getText().toString()).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!realmCharacter.getInteriors().contains(realmInterior)) {
                    realmCharacter.getInteriors().add(realmInterior);
                    Toast.makeText(getContext(), realmInterior.getName() + "を配置したよ！", Toast.LENGTH_SHORT).show();
                } else {
                    realmCharacter.getInteriors().remove(realmInterior);
                    frameLayout.removeView(hashMap.get(realmInterior.getName()));
                    Log.d("MYTAG", hashMap.get(realmInterior.getName()) + "");
                    hashMap.remove(realmInterior.getName());
                    Toast.makeText(getContext(), realmInterior.getName() + "を片付けたよ！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
