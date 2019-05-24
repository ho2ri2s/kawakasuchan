package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;

public class CustomizeShoesFragment extends Fragment {

    private ImageView imgCharacter;
    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;
    private FloatingActionButton fab;

    public CustomizeShoesFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shoes, container, false);

        imgCharacter = view.findViewById(R.id.imgCharacter);
        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);
        fab = view.findViewById(R.id.floatingActionButton);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void showData() {
        realm = Realm.getDefaultInstance();
        Character realmCharacter = realm.where(Character.class).findFirst();

        fab.setImageResource(R.drawable.ic_check);


        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));

        if(realmCharacter.getClothes() != null){
            imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
        }
    }
}
