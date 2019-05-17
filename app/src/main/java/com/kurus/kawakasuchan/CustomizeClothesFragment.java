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

    private int choseNumber;

    public CustomizeClothesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes, container, false);

        choseNumber = -1;

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
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(CustomizeClothesFragment.this, "着替え", txtChose.getText() + "に着替えますか？");
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
        final Clothes realmClothes = realmItemGroup.getClothes().where().equalTo("name", txtChose.getText().toString()).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realmCharacter.setClothes(realmClothes);

                Toast.makeText(getContext(), txtChose.getText() + "に着替えたよ！", Toast.LENGTH_SHORT).show();
                txtChose.setText("");



            }
        });

    }
}
