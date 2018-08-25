package com.example.divided.mathrush;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {


    Vibrator vibrator;
    private AppCompatDelegate mDelegate;
    private Toolbar mToolbar;
    private Preference vibrationPreferance;
    private Preference soundEffectsPreferance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.mToolbar));
        addPreferencesFromResource(R.xml.app_preferences);

        mToolbar = findViewById(R.id.mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24px);
        mToolbar.setTitleTextColor(Color.WHITE);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        vibrationPreferance = findPreference("ENABLE_VIBRATION");
        vibrationPreferance.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final boolean vibrationEnabled = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .getBoolean("ENABLE_VIBRATION", true);
                if (vibrationEnabled && vibrator.hasVibrator()) {
                    vibrator.vibrate(1000);
                }
                return false;
            }
        });

        soundEffectsPreferance = findPreference("ENABLE_SOUND_EFFECTS");
        soundEffectsPreferance.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final boolean soundEffectsEnabled = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .getBoolean("ENABLE_SOUND_EFFECTS", true);
                if (soundEffectsEnabled) {

                    final MediaPlayer oneShootSound = MediaPlayer.create(getApplicationContext(), R.raw.correct_answer); //better approach
                    oneShootSound.start();
                    oneShootSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }

                return false;
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
    }
}