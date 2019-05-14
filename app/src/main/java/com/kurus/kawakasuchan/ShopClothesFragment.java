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
import io.realm.RealmList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopClothesFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private FloatingActionButton fab;
    private ImageView imgCharacter;
    private ImageView[] imgClothes = new ImageView[3];
    private int[] ids = {R.id.imgBalloonDress, R.id.imgShirtDress, R.id.imgCasualClothes};
    private ImageView imgClicked;
    private TextView txtChose;
    private TextView txtClothesPrice;
    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;

    public ShopClothesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(ShopClothesFragment.this, "購入", txtChose.getText() + "を購入しますか？");
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
        txtClothesPrice = (TextView) childLinearLayout.getChildAt(0);
    }

    private void showData() {
        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();
        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();

        RealmList<Clothes> clothes = realmItemGroup.getClothes();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));
        // TODO: 2019/05/12 nullを作らないためnakedにも値を充てる
        if(realmCharacter.getClothes() != null){
            imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
        }

        for (int i = 0; i < clothes.size(); i++){
            //服を所持しているならSoldOutに、所持していないなら選択可
            if(clothes.get(i).getIsHaving() == true){
                imgClothes[i].setImageResource(R.drawable.sold_out);
            }else{
                imgClothes[i].setImageResource(clothes.get(i).getClothesResourceId());
                imgClothes[i].setOnClickListener(this);
            }
        }
        
    }

    @Override
    public void onDialogPositiveButtonClicked() {
        final Character realmCharacter = realm.where(Character.class).findFirst();
        final ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();

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
                    switch (imgClicked.getId()) {
                        case R.id.imgBalloonDress:
                            realmItemGroup.getClothes().get(0).setIsHaving(true);
                            break;
                        case R.id.imgShirtDress:
                            realmItemGroup.getClothes().get(1).setIsHaving(true);
                            break;
                        case R.id.imgCasualClothes:
                            realmItemGroup.getClothes().get(2).setIsHaving(true);
                    }
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
