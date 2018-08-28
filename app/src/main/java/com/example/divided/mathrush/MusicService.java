package com.example.divided.mathrush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mediaPlayer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            String filename = "android.resource://" + this.getPackageName() + "/raw/menu_theme_new_cut";
            mediaPlayer = new MediaPlayer();
            //sets the data source of audio file
            mediaPlayer.setDataSource(this, Uri.parse(filename));
            //prepares the player for playback synchronously
            mediaPlayer.prepare();
            //sets the player for looping
            mediaPlayer.setLooping(true);

            CountDownTimer delay = new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mediaPlayer.setVolume(0,0);
                    mediaPlayer.start();

                    ValueAnimator volumeFadeIn = ValueAnimator.ofFloat(0.0f,1.0f);
                    volumeFadeIn.setDuration(1500);
                    volumeFadeIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mediaPlayer.setVolume((float)animation.getAnimatedValue(),(float)animation.getAnimatedValue());
                        }

                    });
                    volumeFadeIn.start();
                }
            };
            delay.start();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    public void onDestroy(){
        ValueAnimator volumeFadeOut = ValueAnimator.ofFloat(1.0f,0.0f);
        volumeFadeOut.setDuration(1000);
        volumeFadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mediaPlayer.setVolume((float)animation.getAnimatedValue(),(float)animation.getAnimatedValue());
            }

        });
        volumeFadeOut.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mediaPlayer.stop();
                //releases any resource attached with MediaPlayer object
                mediaPlayer.release();
            }
        });
        volumeFadeOut.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
