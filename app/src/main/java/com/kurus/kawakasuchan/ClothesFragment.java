package com.kurus.kawakasuchan;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClothesFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private FloatingActionButton fab;
    private ImageView imgGirl;
    private ImageView[] imgClothes = new ImageView[3];
    private ImageView imgClicked;
    private TextView txtChose;
    private TextView txtClothesPoint;
    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;

    public ClothesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clothes, container, false);

        fab = view.findViewById(R.id.floatingActionButton);
        imgGirl = view.findViewById(R.id.imgGirl);
        imgClothes[0] = view.findViewById(R.id.imgBalloonDress);
        imgClothes[1] = view.findViewById(R.id.imgShirtDress);
        imgClothes[2] = view.findViewById(R.id.imgCasualClothes);
        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);

        txtChose = new TextView(getContext());
        txtChose.setText("");

        for (int i = 0; i < imgClothes.length; i++) {
            imgClothes[i].setOnClickListener(this);
        }
        fab.setOnClickListener(this);

        //着用している服を判別し画像にセット
        realm = Realm.getDefaultInstance();
        Clothes realmClothes = realm.where(Clothes.class).findFirst();
        Character realmCharacter = realm.where(Character.class).findFirst();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));


        if (realmClothes.getBalloonDressStatus() == 2) {
            imgGirl.setImageResource(R.drawable.balloon_dress);
        } else if (realmClothes.getShirtDressStatus() == 2) {
            imgGirl.setImageResource(R.drawable.shirt_dress);
        } else if (realmClothes.getCasualClothesStatus() == 2) {
            imgGirl.setImageResource(R.drawable.casual_clothes);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBalloonDress:
                getImageInfo(view);
                imgGirl.setImageResource(R.drawable.balloon_dress);
                break;
            case R.id.imgShirtDress:
                getImageInfo(view);
                imgGirl.setImageResource(R.drawable.shirt_dress);
                break;
            case R.id.imgCasualClothes:
                getImageInfo(view);
                imgGirl.setImageResource(R.drawable.casual_clothes);
                break;
            case R.id.floatingActionButton:
                if (txtChose.getText().toString() != "") {
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(ClothesFragment.this, "購入", txtChose.getText() + "を購入しますか？");
                    buyDialogFragment.show(getFragmentManager(), "buy");
                } else {
                    Toast.makeText(getContext(), "アイテムを選択してください", Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    private void getImageInfo(View view) {
        //選択された画像のテキストを読み込む
        imgClicked = (ImageView) view;
        LinearLayout parentLinearLayout = (LinearLayout) imgClicked.getParent();
        txtChose = (TextView) parentLinearLayout.getChildAt(0);
        LinearLayout childLinearLayiout = (LinearLayout) parentLinearLayout.getChildAt(2);
        txtClothesPoint = (TextView) childLinearLayiout.getChildAt(0);
    }

    @Override
    public void onDialogPositiveButtonClicked() {
        final Character realmCharacter = realm.where(Character.class).findFirst();
        final Clothes realmClothes = realm.where(Clothes.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int afterPoint = 0;

                try {
                    afterPoint = realmCharacter.getdPoint() - Integer.parseInt(txtClothesPoint.getText().toString());

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (afterPoint >= 0) {
                    //ポイント消費
                    realmCharacter.setdPoint(afterPoint);
                    //購入
                    switch (imgClicked.getId()) {
                        case R.id.imgBalloonDress:
                            realmClothes.setBalloonDressStatus(1);
                            break;
                        case R.id.imgShirtDress:
                            realmClothes.setShirtDressStatus(1);
                            break;
                        case R.id.imgCasualClothes:
                            realmClothes.setCasualClothesStatus(1);
                    }
                    //ポイント欄更新
                    txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
                    //購入したらSoldOutに
                    imgClicked.setImageResource(R.drawable.dryer);
                    //クリックイベント削除
                    imgClicked.setOnClickListener(null);

                    txtChose.setText("");
                } else {
                    Toast.makeText(getContext(), "所持ポイントが足りないよ！", Toast.LENGTH_SHORT).show();
                }


            }
        });
        // TODO: 2019/05/04 imageをSoldOutに変更
        // TODO: 2019/05/04 CustomizeActivityでも使えるようにする

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
