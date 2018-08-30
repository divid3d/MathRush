package com.example.divided.mathrush;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

public class HighscoreActivity extends AppCompatActivity {

    private TickerView mScore;
    private Button mNext;
    private SoundPool mySoundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        setupText();
        soundEffectsSetup();

        mySoundPool.load(this, R.raw.highscore, 1);
        mySoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 1, 0, 1.0f);
            }
        });
        mScore = findViewById(R.id.mScoreValue);
        mScore.setCharacterLists(TickerUtils.provideNumberList());
        mScore.setText("0");
        mNext = findViewById(R.id.mNext);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                final Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("ROUND", extras.getInt("ROUND"));
                    intent.putExtra("SCORE", extras.getInt("SCORE"));
                }
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_instantly, R.anim.fade_out);
            }
        });
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int highScore = extras.getInt("HIGH_SCORE");
            mScore.setText(Integer.toString(highScore));
            mScore.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mScore.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse_animation));
                    mNext.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse_animation));
                }
            });
        }
        startConfetti(1000);
    }

    private void setupText() {
        TextView mHighScoreText = findViewById(R.id.mCongratulationsTitle);
        String firstWord = "Congratulations!\n";
        String secondWord = "New highscore";
        Spannable titleText = new SpannableString(firstWord + secondWord);
        titleText.setSpan(
                new RelativeSizeSpan(0.5f)
                , firstWord.length()
                , firstWord.length() + secondWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD)
                , 0
                , firstWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
                , 0
                , firstWord.length() + secondWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new ForegroundColorSpan(Color.WHITE)
                , 0
                , firstWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mHighScoreText.setText(titleText);
    }

    public void soundEffectsSetup() {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mySoundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attrs)
                .build();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, StartGameActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startConfetti(long delay) {
        CountDownTimer countDownToConfeti = new CountDownTimer(delay, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new ParticleSystem(HighscoreActivity.this, 100, R.drawable.confetti_red, 4000)
                        .setSpeedModuleAndAngleRange(0f, 0.1f, 180, 180)
                        .setRotationSpeedRange(30f, 144f)
                        .addModifier(new AlphaModifier(255, 0, 3500, 4000))
                        .setScaleRange(0.75f, 1.0f)
                        .setAcceleration(0.00008f, 90)
                        .emit(findViewById(R.id.emiter_top_right), 15);

                new ParticleSystem(HighscoreActivity.this, 100, R.drawable.confetti_green, 4500)
                        .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 0)
                        .setRotationSpeedRange(30f, 144f)
                        .setScaleRange(0.75f, 1.0f)
                        .addModifier(new AlphaModifier(255, 0, 3500, 4500))
                        .setAcceleration(0.00008f, 90)
                        .emit(findViewById(R.id.emiter_top_left), 15);

                new ParticleSystem(HighscoreActivity.this, 100, R.drawable.confetti_yellow, 4500)
                        .setSpeedModuleAndAngleRange(0f, 0.1f, 180, 180)
                        .setRotationSpeedRange(30f, 144f)
                        .setScaleRange(0.75f, 1.0f)
                        .addModifier(new AlphaModifier(255, 0, 3500, 4000))
                        .setAcceleration(0.00008f, 90)
                        .emit(findViewById(R.id.emiter_top_right), 15);

                new ParticleSystem(HighscoreActivity.this, 100, R.drawable.confetti_purple, 4500)
                        .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 0)
                        .setRotationSpeedRange(30f, 144f)
                        .setScaleRange(0.75f, 1.0f)
                        .addModifier(new AlphaModifier(255, 0, 3500, 4500))
                        .setAcceleration(0.00008f, 90)
                        .emit(findViewById(R.id.emiter_top_left), 15);
            }
        }.start();
    }
}
