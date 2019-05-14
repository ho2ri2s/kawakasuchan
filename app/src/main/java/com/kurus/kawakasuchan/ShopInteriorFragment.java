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

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ShopInteriorFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private Realm realm;
    private TextView txtChose;
    private TextView txtClothesPrice;
    private TextView txtDPoint;
    private TextView txtLevel;
    private ImageView imgClicked;
    private ImageView[] imgInterior = new ImageView[5];
    private int[] ids = {R.id.imgBed, R.id.imgWindow, R.id.imgMirror, R.id.imgBookshelf, R.id.imgTelevision};
    private FloatingActionButton fab;
    private FrameLayout frameLayout;

    private int frameWidth;
    private int frameHeight;
    private static final float LIVING__WIDTH = 1208.0f;
    private static final float LIVING__HEIGHT = 1922.5f;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;


    public ShopInteriorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        switch (view.getId()) {
            case R.id.imgBed:
            case R.id.imgWindow:
            case R.id.imgMirror:
            case R.id.imgBookshelf:
            case R.id.imgTelevision:
                ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
                Interior realmInterior = realmItemGroup.getInteriors().get(Integer.parseInt(view.getTag().toString()));
                addImage(realmInterior);
                txtChose.setText(realmInterior.getName());
                txtClothesPrice.setText(realmInterior.getPrice());
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
        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();

        RealmList<Interior> interiors = realmItemGroup.getInteriors();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));
        // TODO: 2019/05/12 持っているインテリアを充てる
        if (realmCharacter.getInteriors() != null) {
            RealmResults<Interior> realmInterior = realmItemGroup.getInteriors().where().equalTo("isHaving", true).findAll();
            for(int i = 0; i < realmInterior.size(); i++){
                addImage(realmInterior.get(i));
            }
        }

        for (int i = 0; i < interiors.size(); i++) {
            //服を所持しているならSoldOutに、所持していないなら選択可
            if (interiors.get(i).getIsHaving() == true) {
                imgInterior[i].setImageResource(R.drawable.sold_out);
            } else {
                imgInterior[i].setImageResource(interiors.get(i).getResourceId());
                imgInterior[i].setOnClickListener(this);
            }
        }
    }

    private void addImage(final Interior interior) {
        //画像設置
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(interior.getWidth(), interior.getHeight());
        final ImageView image = new ImageView(getContext());
        image.setImageResource(interior.getResourceId());
        frameLayout.addView(image, params);

        //配置転換
        ViewTreeObserver observer = frameLayout.getViewTreeObserver();
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //この中で幅と高さを取得
                frameWidth = frameLayout.getWidth();
                frameHeight = frameLayout.getHeight();
                //指定の位置に画像を移動
                image.setTranslationX(interior.getX() * frameWidth / LIVING__WIDTH - interior.getWidth() / 2);
                image.setTranslationY(interior.getY() * frameHeight / LIVING__HEIGHT - interior.getHeight() / 2);
                //一回きり
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            }
        };
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
                    afterPoint = realmCharacter.getdPoint() - Integer.parseInt(txtClothesPrice.getText().toString());

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
                    imgClicked.setImageResource(R.drawable.sold_out);
                    //クリックイベント削除
                    imgClicked.setOnClickListener(null);

                    Toast.makeText(getContext(), txtChose.getText() + "を購入したよ！", Toast.LENGTH_SHORT).show();

                    txtChose.setText("");
                } else {
                    Toast.makeText(getContext(), "所持ポイントが足りないよ！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
