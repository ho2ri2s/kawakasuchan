package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CustomizeInteriorFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private static final float LIVING__WIDTH = 1200.0f;
    private static final float LIVING__HEIGHT = 1920.0f;

    private Realm realm;
    private TextView txtChose;
    private TextView txtClothesPrice;
    private TextView txtDPoint;
    private TextView txtLevel;
    private ImageView[] imgInterior = new ImageView[5];
    private ImageView addImage;
    private int[] ids = {R.id.imgBed, R.id.imgWindow, R.id.imgMirror, R.id.imgBookshelf, R.id.imgTelevision};
    private FloatingActionButton fab;
    private FrameLayout frameLayout;

    private int frameWidth;
    private int frameHeight;
    private int choseNumber;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    public CustomizeInteriorFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interior, container, false);
        for (int i = 0; i < imgInterior.length; i++) {
            imgInterior[i] = view.findViewById(ids[i]);
        }

        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);
        fab = view.findViewById(R.id.floatingActionButton);
        frameLayout = view.findViewById(R.id.frameLayout);

        txtChose = new TextView(getContext());
        txtClothesPrice = new TextView(getContext());

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
        switch (view.getId()) {
            case R.id.imgBed:
            case R.id.imgWindow:
            case R.id.imgMirror:
            case R.id.imgBookshelf:
            case R.id.imgTelevision:
                choseNumber = Integer.parseInt(view.getTag().toString());

                ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
                Interior realmInterior = realmItemGroup.getInteriors().get(choseNumber);

                frameLayout.removeView(addImage);
                addImage(realmInterior);

                txtChose.setText(realmInterior.getName());
                txtClothesPrice.setText(String.valueOf(realmInterior.getPrice()));
                break;
            case R.id.floatingActionButton:
                if (txtChose.getText().toString() != "") {
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(CustomizeInteriorFragment.this, "変更", txtChose.getText() + "カスタマイズしますか？");
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
        if (realmCharacter.getInteriors() != null) {
            RealmResults<Interior> realmInterior = realmCharacter.getInteriors().where().equalTo("isHaving", true).findAll();
            for(int i = 0; i < realmInterior.size(); i++){
                addImage(realmInterior.get(i));
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
        addImage = new ImageView(getContext());
        addImage.setVisibility(View.GONE);

        //配置転換
        ViewTreeObserver observer = frameLayout.getViewTreeObserver();

        if(globalLayoutListener != null){
            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }

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
                if(!realmCharacter.getInteriors().contains(realmInterior)){
                    realmCharacter.getInteriors().add(realmInterior);
                    Toast.makeText(getContext(),  realmInterior.getName() + "を配置したよ！", Toast.LENGTH_SHORT).show();
                }else{
                    realmCharacter.getInteriors().remove(realmInterior);
                    frameLayout.removeView(addImage);
                    Toast.makeText(getContext(),  realmInterior.getName() + "を片付けたよ！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
