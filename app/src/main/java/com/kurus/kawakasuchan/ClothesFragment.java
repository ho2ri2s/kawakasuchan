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


/**
 * A simple {@link Fragment} subclass.
 */
public class ClothesFragment extends Fragment implements View.OnClickListener, BuyDialogFragment.DialogFragmentListener {

    private FloatingActionButton fab;
    private ImageView[] clothes = new ImageView[3];
    private TextView txtChose;

    public ClothesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clothes, container, false);

        fab = view.findViewById(R.id.floatingActionButton);
        clothes[0] = view.findViewById(R.id.imgClothes0);
        clothes[1] = view.findViewById(R.id.imgClothes1);
        clothes[2] = view.findViewById(R.id.imgClothes2);

        for (int i = 0; i < clothes.length; i++) {
            clothes[i].setOnClickListener(this);
        }
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgClothes0:
            case R.id.imgClothes1:
            case R.id.imgClothes2:
                ImageView imgClicked = (ImageView) view;
                LinearLayout linearLayout = (LinearLayout) imgClicked.getParent();
                txtChose = (TextView) linearLayout.getChildAt(0);
                break;
            case R.id.floatingActionButton:
                if (txtChose != null){
                    BuyDialogFragment buyDialogFragment = new BuyDialogFragment().newInstance(ClothesFragment.this, "購入", txtChose.getText() + "を購入しますか？");
                    buyDialogFragment.show(getFragmentManager(), "buy");
                }else{
                    Toast.makeText(getContext(), "アイテムを選択してください", Toast.LENGTH_LONG).show();
                }

                break;
        }

    }

    @Override
    public void onDialogPositiveButtonClicked() {
        Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
    }

}
