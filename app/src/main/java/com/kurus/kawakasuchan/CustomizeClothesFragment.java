package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class CustomizeClothesFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private FloatingActionButton fab;
    private ImageView imgCharacter;
    private ImageView[] imgClothes = new ImageView[3];
    private int[] ids = {R.id.imgBalloonDress, R.id.imgShirtDress, R.id.imgCasualClothes};
    private ImageView imgClicked;
    private TextView txtChose;
    private TextView txtClothesPoint;
    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;

    public CustomizeClothesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes, container, false);

        fab = view.findViewById(R.id.floatingActionButton);
        imgCharacter = view.findViewById(R.id.imgCharacter);
        for (int i = 0; i < imgClothes.length; i++) {
            imgClothes[i] = view.findViewById(ids[i]);
        }
        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);

        txtChose = new TextView(getContext());
        txtChose.setText("");

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
            case R.id.imgBalloonDress:
                getImageInfo(view);
                imgCharacter.setImageResource(R.drawable.balloon_dress);
                break;
            case R.id.imgShirtDress:
                getImageInfo(view);
                imgCharacter.setImageResource(R.drawable.shirt_dress);
                break;
            case R.id.imgCasualClothes:
                getImageInfo(view);
                imgCharacter.setImageResource(R.drawable.casual_clothes);
                break;
            case R.id.floatingActionButton:
                if (txtChose.getText().toString() != "") {
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(CustomizeClothesFragment.this, "着替え", txtChose.getText() + "に着替えますか？");
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
        LinearLayout childLinearLayout = (LinearLayout) parentLinearLayout.getChildAt(2);
        txtClothesPoint = (TextView) childLinearLayout.getChildAt(0);
    }

    private void showData() {
        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();
        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();

        fab.setImageResource(R.drawable.ic_check);

        //キャラクター情報をセット
        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));
        //裸でなければ服を読み込む
        if(realmCharacter.getClothes() != null){
            imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());

        }

        for (int i = 0; i < realmItemGroup.getClothes().size(); i++){
            //服を所持しているならSoldOutに、所持していないなら選択可
            if(realmItemGroup.getClothes().get(i).getIsHaving() == true){
                imgClothes[i].setOnClickListener(this);
                imgClothes[i].setImageResource(realmItemGroup.getClothes().get(i).getClothesResourceId());
            }else{
                imgClothes[i].setImageResource(R.drawable.question);
            }
        }


    }

    @Override
    public void onDialogPositiveButtonClicked() {
        final ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
        final Character realmCharacter = realm.where(Character.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // TODO: 2019/05/12 さっきまで選択していた服を脱ぐ
                switch (imgClicked.getId()) {
                    case R.id.imgBalloonDress:
                        realmCharacter.setClothes(realmItemGroup.getClothes().get(0));
                        break;
                    case R.id.imgShirtDress:
                        realmCharacter.setClothes(realmItemGroup.getClothes().get(1));
                        break;
                    case R.id.imgCasualClothes:
                        realmCharacter.setClothes(realmItemGroup.getClothes().get(2));
                        break;
                }

                Toast.makeText(getContext(), txtChose.getText() + "に着替えたよ！", Toast.LENGTH_SHORT).show();
                txtChose.setText("");



            }
        });

    }
}
