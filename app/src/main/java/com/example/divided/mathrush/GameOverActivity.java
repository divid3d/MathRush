package com.example.divided.mathrush;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;


public class GameOverActivity extends AppCompatActivity {

    ScoresDatabaseHelper myDb;

    Button mRetryButton;
    Button mQuitButton;
    Button mClearRanking;
    TextView mSummary;
    TextView mGameOver;
    ConstraintLayout container;
    RecyclerView mRecyclerView;
    IndicatorView myIndicatorView;

    private SoundPool mySoundPool;
    private ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];
    private int soundIds[] = new int[3];
    private boolean soundEnabled;
    private int gameDifficultyLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        getSettings();
        soundEffectsSetup();
        myDb = new ScoresDatabaseHelper(this);
        scoreLists = myDb.loadScoresFromDatabase();
        container = findViewById(R.id.mGameOverLayout);
        mRetryButton = findViewById(R.id.mRetryButton);
        mGameOver = findViewById(R.id.mGameOver);
        mQuitButton = findViewById(R.id.mQuitButton);
        mClearRanking = findViewById(R.id.mClearRanking);
        mSummary = findViewById(R.id.mSummary);
        myIndicatorView = findViewById(R.id.mIndicatorView);
        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_thick)));
        mRecyclerView.addItemDecoration(itemDecorator);

        ValueAnimator valueAnimator = ObjectAnimator.ofInt(mGameOver, "TextColor", 0xFFFF0000, 0xFFFF6262);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();

        String labels[] = new String[3];
        labels[0] = "Easy";
        labels[1] = "Medium";
        labels[2] = "Hard";

        myIndicatorView.setParameters(0, 2, labels);
        myIndicatorView.setCurrentPosition(gameDifficultyLevel);
        myIndicatorView.setOnPositionChangeListener(new IndicatorView.OnPositionChangeListener() {
            @Override
            public void onChange(int position) {
                mRecyclerView.setAdapter(new ScoresAdapter(scoreLists[position]));
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mRecyclerView.scheduleLayoutAnimation();
                if (scoreLists[myIndicatorView.getCurrentPosition()].isEmpty() && mClearRanking.isEnabled()) {
                    mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    mClearRanking.setEnabled(false);
                } else if (!scoreLists[myIndicatorView.getCurrentPosition()].isEmpty() && !mClearRanking.isEnabled()) {
                    mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    mClearRanking.setEnabled(true);
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    myIndicatorView.setNext();
                } else {
                    myIndicatorView.setPrev();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(new ScoresAdapter(scoreLists[gameDifficultyLevel]));
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();

        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.clearRanking(myIndicatorView.getCurrentPosition());
                scoreLists[myIndicatorView.getCurrentPosition()].clear();
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mClearRanking.setEnabled(false);
            }
        });

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetryButton.setEnabled(false);
                mQuitButton.setEnabled(false);
                if (soundEnabled) {
                    mySoundPool.play(soundIds[2], 1, 1, 1, 0, 1.0f);
                }
                CountDownTimer gameStartTimer = new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
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
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int score = extras.getInt("SCORE");
            final int round = extras.getInt("ROUND");

            mSummary.setText("Your score:\t" + score + "\n" + "Round:\t" + round);
            if (score > 0) {
                ScoreDialog scoreDialog = new ScoreDialog();
                scoreDialog.setOnNameConfirmationListener(new ScoreDialog.OnNameConfirmationListener() {
                    @Override
                    public void onConfirm(String name) {
                        mRetryButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse_animation));
                        final ScoreInformation newScore = new ScoreInformation(name, round, score);
                        myDb.insertScore(newScore, gameDifficultyLevel);
                        scoreLists = myDb.loadScoresFromDatabase();
                        ScoresAdapter scoreAdapter = new ScoresAdapter(scoreLists[gameDifficultyLevel]);
                        mRecyclerView.setAdapter(scoreAdapter);
                        final int indexOfScore = myDb.getRankingPlace(newScore, gameDifficultyLevel);
                        mSummary.append("\nYou took " + indexOfScore + " place!");
                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(getApplicationContext()) {
                                    @Override
                                    protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };
                        smoothScroller.setTargetPosition(indexOfScore);
                        scoreAdapter.notifyDataSetChanged();
                        mRecyclerView
                                .getLayoutManager()
                                .startSmoothScroll(smoothScroller);
                    }
                });
                scoreDialog.showDialog(this);
            }
            mRetryButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse_animation));
        }
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
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
        finish();
    }

    public void getSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        gameDifficultyLevel = Integer.parseInt(sharedPreferences.getString("DIFFICULTY_LEVEL", "0"));
    }

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
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

        soundIds[0] = mySoundPool.load(this, R.raw.correct_answer, 1);
        soundIds[1] = mySoundPool.load(this, R.raw.incorrect_answer, 1);
        soundIds[2] = mySoundPool.load(this, R.raw.start_click_new, 1);
    }
}