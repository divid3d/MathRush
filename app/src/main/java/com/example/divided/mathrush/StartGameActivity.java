package com.example.divided.mathrush;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

public class StartGameActivity extends AppCompatActivity {

    Button mStartButton;
    Button mQuitButton;
    ImageButton mSettings;
    ImageButton mLeaderboard;
    TextView mTitle;
    AnimationDrawable anim;
    ConstraintLayout container;
    View view;
    SoundPool mySoundPool;

    int soundIds[] = new int[3];
    private boolean soundEnabled;
    private boolean vibrationEnabled;
    private boolean musicEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        getSettings();
        soundEffectsSetup();


        if (!isMyServiceRunning(MusicService.class) && musicEnabled) {
            startService(new Intent(this, MusicService.class));
        }


        container = findViewById(R.id.mStartGameLayout);
        mTitle = findViewById(R.id.mTitle);
        setupTitle();
        mStartButton = findViewById(R.id.mStartButton);
        mQuitButton = findViewById(R.id.mQuitButton2);
        mSettings = findViewById(R.id.mSettings);
        mLeaderboard = findViewById(R.id.mLeaderboard);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartButton.setEnabled(false);
                mQuitButton.setEnabled(false);

                ParticleSystem ps = new ParticleSystem(StartGameActivity.this, 400, R.drawable.square_particle, 1500);
                ps.setScaleRange(0.05f, 0.4f);
                ps.setSpeedRange(0.1f, 0.25f);
                ps.setAcceleration(0.0001f, 90);
                ps.setRotationSpeedRange(90, 180);
                ps.setFadeOut(200, new AccelerateInterpolator());
                ps.oneShot(findViewById(R.id.mStartButton), 400);

                if (soundEnabled == true) {
                    mySoundPool.play(soundIds[0], 1, 1, 1, 0, 1.0f);
                }

                final CountDownTimer gameStartTimer = new CountDownTimer(1000, 1000) {


                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        if (isMyServiceRunning(MusicService.class)) {
                            stopService(new Intent(getApplicationContext(), MusicService.class));
                        }
                        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                };
                gameStartTimer.start();
            }
        });

        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEnabled == true) {
                    mySoundPool.play(soundIds[1], 0.25f, 0.25f, 1, 0, 1.0f);
                }
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEnabled == true) {
                    mySoundPool.play(soundIds[1], 0.25f, 0.25f, 1, 0, 1.0f);
                }
                Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mStartButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.start_button_animation));
        mTitle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.title_text_animation));
        mQuitButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quit_button_animation));
        mSettings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.settings_animation));
        mLeaderboard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quit_button_animation));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        quitGame();
    }

    public void soundEffectsSetup() {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mySoundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(attrs)
                .build();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundIds[0] = mySoundPool.load(this, R.raw.start_click_new, 1);
        soundIds[1] = mySoundPool.load(this, R.raw.tick_effect, 1);
    }

    public void getSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        vibrationEnabled = sharedPreferences.getBoolean("ENABLE_VIBRATION", true);
        musicEnabled = sharedPreferences.getBoolean("ENABLE_MAIN_MENU_MUSIC",true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMyServiceRunning(MusicService.class)) {
            stopService(new Intent(getApplicationContext(), MusicService.class));
        }
    }

    private void setupTitle() {
        String firstWord = "Math\n";
        String secondWord = "Rush";
        Spannable titleText = new SpannableString(firstWord + secondWord);
        titleText.setSpan(
                new RelativeSizeSpan(0.5f)
                , 0
                , firstWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        titleText.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD)
                , firstWord.length()
                , firstWord.length() + secondWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
                , 0
                , firstWord.length() + secondWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new UnderlineSpan()
                , 0
                , firstWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new ForegroundColorSpan(Color.WHITE)
                , 0
                , firstWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        mTitle.setText(titleText);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
