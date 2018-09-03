package com.example.divided.mathrush;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ScoreDialog {


    public OnNameConfirmationListener listener;

    ScoreDialog() {
        this.listener = null;
    }

    public void setOnNameConfirmationListener(OnNameConfirmationListener listener) {
        this.listener = listener;
    }

    public void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.scoredialog_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.6f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        final ImageView icon = dialog.findViewById(R.id.icon);
        final Animation iconAnimation = AnimationUtils.loadAnimation(activity, R.anim.score_dialog_icon);
        final EditText text = dialog.findViewById(R.id.text_dialog);
        text.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_left));
        final Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = text.getText().toString();
                if (name.length() == 0) {
                    if (listener != null) {
                        listener.onConfirm("Unknown");
                    }
                } else {
                    if (listener != null) {
                        listener.onConfirm(name);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        text.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_left));
        dialogButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_right));
        icon.startAnimation(iconAnimation);
    }

    public interface OnNameConfirmationListener {
        void onConfirm(String name);
    }
}
