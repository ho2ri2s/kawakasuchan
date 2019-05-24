package com.kurus.kawakasuchan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopShoesFragment extends Fragment {

    private ImageView imgCharacter;
    private TextView txtDPoint;
    private TextView txtLevel;
    private Realm realm;


    public ShopShoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shoes, container, false);

        imgCharacter = view.findViewById(R.id.imgCharacter);
        txtDPoint = view.findViewById(R.id.txtDPoint);
        txtLevel = view.findViewById(R.id.txtLevel);
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

        txtDPoint.setText(String.valueOf(realmCharacter.getdPoint()));
        txtLevel.setText(String.valueOf(realmCharacter.getLevel()));

        if(realmCharacter.getClothes() != null){
            imgCharacter.setImageResource(realmCharacter.getClothes().getCharacterResourceId());
        }
    }
}
