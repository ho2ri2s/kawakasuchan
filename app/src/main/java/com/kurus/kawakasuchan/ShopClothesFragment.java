package com.kurus.kawakasuchan;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopClothesFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private FloatingActionButton fab;
    private TextView txtChose;
    private TextView[] txtClothesPrice = new TextView[3];
    private ImageView[] imgClothes = new ImageView[3];
    private int[] clothesID = {R.id.imgBalloonDress, R.id.imgShirtDress, R.id.imgCasualClothes};
    private int[] priceID = {R.id.txtBalloonDressDP, R.id.txtShirtDressDP, R.id.txtCasualClothesDP};
    private ImageView imgCharacter;

    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;

    private int choseNumber;

    public ShopClothesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clothes, container, false);

        choseNumber = -1;

        for (int i = 0; i < imgClothes.length; i++) {
            imgClothes[i] = view.findViewById(clothesID[i]);
            txtClothesPrice[i] = view.findViewById(priceID[i]);
        }
        fab = view.findViewById(R.id.floatingActionButton);
        imgCharacter = view.findViewById(R.id.imgCharacter);
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
            case R.id.imgShirtDress:
            case R.id.imgCasualClothes:
                if(choseNumber != -1){
                    imgClothes[choseNumber].setBackground(null);
                }
                choseNumber = Integer.parseInt(view.getTag().toString());
                imgClothes[choseNumber].setBackground(getResources().getDrawable(R.drawable.text_border));

                ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();
                Clothes realmClothes = realmItemGroup.getClothes().get(choseNumber);

                imgCharacter.setImageResource(realmClothes.getCharacterResourceId());

                txtChose.setText(realmClothes.getName());
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


    private void showData() {
        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();
        ItemGroup realmItemGroup = realm.where(ItemGroup.class).findFirst();

        RealmList<Clothes> clothes = realmItemGroup.getClothes();

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));

        if(realmCharacter.getClothes() != null){
            imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
        }

        for (int i = 0; i < clothes.size(); i++){
            txtClothesPrice[i].setText(clothes.get(i).getPrice() + "    DP");
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
        final Clothes realmClothes = realmItemGroup.getClothes().where().equalTo("name", txtChose.getText().toString()).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int afterPoint = 0;

                try {
                    afterPoint = realmCharacter.getdPoint() - realmClothes.getPrice();

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (afterPoint >= 0) {
                    //ポイント消費
                    realmCharacter.setdPoint(afterPoint);
                    //購入
                    realmClothes.setIsHaving(true);
                    //ポイント欄更新
                    txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
                    //購入したらSoldOutに
                    imgClothes[choseNumber].setImageResource(R.drawable.sold_out);
                    //クリックイベント削除
                    imgClothes[choseNumber].setOnClickListener(null);

                    Toast.makeText(getContext(), txtChose.getText() + "を購入したよ！", Toast.LENGTH_SHORT).show();

                    txtChose.setText("");
                } else {
                    Toast.makeText(getContext(), "所持ポイントが足りないよ！", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

}
