package com.kurus.kawakasuchan;

import android.os.Bundle;
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

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;


public class ShopInteriorFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private static final float LIVING__WIDTH = 1200.0f;
    private static final float LIVING__HEIGHT = 1920.0f;

    private Realm realm;
    private TextView txtChose;
    private TextView txtDPoint;
    private TextView txtLevel;
    private TextView[] txtInteriorPrice = new TextView[5];
    private ImageView[] imgInterior = new ImageView[5];
    private int[] imageID = {R.id.imgBed, R.id.imgWindow, R.id.imgMirror, R.id.imgBookshelf, R.id.imgTelevision};
    private int[] priceID = {R.id.txtBedDP, R.id.txtWindowDP, R.id.txtMirrorDP, R.id.txtBookShelfDP, R.id.txtTelevisionDP};
    private FloatingActionButton fab;
    private FrameLayout frameLayout;

    private HashMap<String, ImageView> hashMap;
    private int frameWidth;
    private int frameHeight;
    private int choseNumber;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;


    public ShopInteriorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        choseNumber = -1;
        hashMap = new HashMap<String, ImageView>();
        txtChose = new TextView(getContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interior, container, false);

        for (int i = 0; i < imgInterior.length; i++) {
            imgInterior[i] = view.findViewById(imageID[i]);
            txtInteriorPrice[i] = view.findViewById(priceID[i]);
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
                    imgInterior[choseNumber].setBackground(null);
                    realmInterior = realmItemGroup.getInteriors().get(choseNumber);

                    frameLayout.removeView(hashMap.get(realmInterior.getName()));

                }
                choseNumber = Integer.parseInt(view.getTag().toString());
                imgInterior[choseNumber].setBackground(getResources().getDrawable(R.drawable.text_border));
                realmInterior = realmItemGroup.getInteriors().get(choseNumber);

                if (!realmCharacter.getInteriors().contains(realmInterior)) {
                    addImage(realmInterior);
                }

                txtChose.setText(realmInterior.getName());
                break;
            case R.id.floatingActionButton:
                if (txtChose.getText().toString() != "") {
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(ShopInteriorFragment.this, "購入", txtChose.getText() + "を購入しますか？");
                    buyDialogFragment.show(getFragmentManager(), "buy");
                } else {
                    Toast.makeText(getContext(), "アイテムを選択してください", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void showData() {
        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();
        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));

        // 持っているインテリアを配置する
        if (realmCharacter.getInteriors() != null) {
            RealmList<Interior> realmInteriorList = realmCharacter.getInteriors();
            for (int i = 0; i < realmInteriorList.size(); i++) {
                addImage(realmInteriorList.get(i));
            }
        }

        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
        RealmList<Interior> interiors = realmItemGroup.getInteriors();
        for (int i = 0; i < interiors.size(); i++) {
            txtInteriorPrice[i].setText(interiors.get(i).getPrice() + "    DP");
            //インテリアを所持しているならSoldOutに、所持していないなら選択可
            if (interiors.get(i).getIsHaving() == true) {
                imgInterior[i].setImageResource(R.drawable.sold_out);
            } else {
                imgInterior[i].setImageResource(interiors.get(i).getResourceId());
                imgInterior[i].setOnClickListener(this);
            }
        }
    }


    private void addImage(final Interior interior) {
        //適当に配置
        final ImageView addImage = new ImageView(getContext());
        addImage.setVisibility(View.GONE);

        //配置転換
        ViewTreeObserver observer = frameLayout.getViewTreeObserver();

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
                int afterPoint = 0;

                try {
                    afterPoint = realmCharacter.getdPoint() - realmInterior.getPrice();

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (afterPoint >= 0) {
                    //ポイント消費
                    realmCharacter.setdPoint(afterPoint);
                    //購入
                    realmInterior.setIsHaving(true);
                    //ポイント欄更新
                    txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
                    //購入したらSoldOutに
                    imgInterior[choseNumber].setImageResource(R.drawable.sold_out);
                    //クリックイベント削除
                    imgInterior[choseNumber].setOnClickListener(null);
                    //追加されたImageView削除
                    frameLayout.removeView(hashMap.get(realmInterior.getName()));
                    Toast.makeText(getContext(), txtChose.getText() + "を購入したよ！", Toast.LENGTH_SHORT).show();

                    txtChose.setText("");
                } else {
                    Toast.makeText(getContext(), "所持ポイントが足りないよ！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
