package com.example.divided.mathrush;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;

public class ScoreDialog {


    public OnNameConfirmationListener listener;

    public ScoreDialog(){
        this.listener=null;
    }

    public void setOnNameConfirmationListener(OnNameConfirmationListener listener){
        this.listener = listener;
    }

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.scoredialog_layout);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

        final ImageView icon = dialog.findViewById(R.id.icon);
        icon.startAnimation(AnimationUtils.loadAnimation(activity,android.R.anim.slide_in_left));

        final EditText text = dialog.findViewById(R.id.text_dialog);


        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = text.getText().toString();
                if(name.length()==0){
                    if(listener!=null){
                        listener.onConfirm("Unknown");
                    }
                }
                else{
                    if(listener!=null){
                        listener.onConfirm(name);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public interface OnNameConfirmationListener{
        void onConfirm(String name);
    }
}
