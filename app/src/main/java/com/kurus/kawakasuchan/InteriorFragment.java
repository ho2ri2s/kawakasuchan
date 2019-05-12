package com.kurus.kawakasuchan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class InteriorFragment extends Fragment implements View.OnClickListener {

    ImageView[] imgInterior = new ImageView[5];
    int[] ids = {R.id.imgBed, R.id.imgMirror, R.id.imgBookshelf, R.id.imgSofa, R.id.imgTelevision};
    public InteriorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interior, container, false);

        for (int i = 0; i < imgInterior.length; i++){
            imgInterior[i] = view.findViewById(ids[i]);
            imgInterior[i].setOnClickListener(this);
        }


        return view;
    }


    @Override
    public void onClick(View view) {
        addView(view);
    }

    private void addView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(180, 180); //数値はpixel

        ImageView addImage = (ImageView)view;
        FrameLayout frameLayout = view.findViewById(R.id.frameLayout);
        frameLayout.addView(addImage, params);


    }
}
