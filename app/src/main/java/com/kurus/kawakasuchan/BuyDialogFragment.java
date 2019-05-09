package com.kurus.kawakasuchan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class BuyDialogFragment extends DialogFragment {

    private String title;
    private String message;
    private DialogFragmentListener dialogFragmentListener;

    public interface DialogFragmentListener{
       void onDialogPositiveButtonClicked();
    }

    //画面回転等のFragment再生成時に呼ばれるため、引数なしのコンストラクタが必要
    public BuyDialogFragment(){
    }
    //再生成されても失いたくない値をBundleにput
    public BuyDialogFragment newInstance(DialogFragmentListener fragment, String title, String message){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        BuyDialogFragment instance = new BuyDialogFragment();
        instance.setArguments(bundle);
        instance.setTargetFragment((Fragment) fragment, 0);
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment targetFragment = this.getTargetFragment();
        try{
            if(targetFragment != null){
                dialogFragmentListener = (DialogFragmentListener)targetFragment;
            }
        }catch (ClassCastException e){
            throw new ClassCastException("DialogFragmentListenerをimplementしていません");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"))
                .setMessage(getArguments().getString("message"))
                .setPositiveButton("する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialogFragmentListener.onDialogPositiveButtonClicked();

                    }
                })
                .setNegativeButton("やめる", null);

        return dialogBuilder.create();
    }
}
