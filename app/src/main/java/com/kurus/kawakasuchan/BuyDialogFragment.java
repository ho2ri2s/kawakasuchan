package com.kurus.kawakasuchan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class BuyDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("購入")
                .setMessage("商品を購入しますか？")
                .setPositiveButton("購入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2019/05/04 対応するポイントを消費する
                        // TODO: 2019/05/04 imageをSoldOutに変更
                        // TODO: 2019/05/04 CustomizeActivityでも使えるようにする
                        // TODO: 2019/05/04 サーバーに保存
                    }
                })
                .setNegativeButton("やめる", null);

        return dialogBuilder.create();
    }
}
