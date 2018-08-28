package com.example.divided.mathrush;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;


public class GameOverActivity extends AppCompatActivity {

    ScoresDatabaseHelper myDb;

    private void loadScoresFromDatabase(ScoresDatabaseHelper myDb,ArrayList<ScoreInformation>[] scoreLists){
        for(int i =1;i <=3;i++){
            Cursor res = myDb.getScores(i);
            if(res.getCount() == 0){
                return;
            }
            if(!scoreLists[i-1].isEmpty()){
                scoreLists[i-1].clear();
            }
            while(res.moveToNext()){
                final String name = res.getString(1);
                final int score = res.getInt(2);
                final int round = res.getInt(3);

                ScoreInformation newScore = new ScoreInformation(name,round,score);
                scoreLists[i-1].add(newScore);
            }
        }
    }

    Button mRetryButton;
    Button mQuitButton;
    Button mClearRanking;
    TextView mSummary;
    TextView mGameOver;
    ConstraintLayout container;
    RecyclerView mRecyclerView;
    AlertDialog.Builder builder;
    ImageButton mNextDifficultyArrow;
    ImageButton mPreviousDifficultyArrow;
    TextSwitcher mDifficultyText;

    private SoundPool mySoundPool;
    private int soundIds[] = new int[3];
    private int difficultyPosition;
    private boolean soundEnabled;
    private int gameDifficultyLevel;

    private ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];

    private ScoresAdapter easyScoresAdapter;
    private ScoresAdapter mediumScoresAdapter;
    private ScoresAdapter hardScoresAdapter;

    private ScoresAdapter adapterPicker(int difficultyPosition) {
        if (difficultyPosition == 1) {
            return easyScoresAdapter;
        } else if (difficultyPosition == 2) {
            return mediumScoresAdapter;
        } else {
            return hardScoresAdapter;
        }
    }

    public void getSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        gameDifficultyLevel = Integer.parseInt(sharedPreferences.getString("DIFFICULTY_LEVEL", "1"));
    }

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void rankingPickerSetup() {

        difficultyPosition = gameDifficultyLevel;
        if (difficultyPosition == 1) {
            mDifficultyText.setText("EASY");
        } else if (difficultyPosition == 2) {
            mDifficultyText.setText("MEDIUM");
        } else {
            mDifficultyText.setText("HARD");
        }

        mPreviousDifficultyArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mDifficultyText.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
                mDifficultyText.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
                mPreviousDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow_click_animation));
                if (difficultyPosition - 1 < 1) {
                    difficultyPosition = 1;
                } else {
                    mRecyclerView.scheduleLayoutAnimation();
                    difficultyPosition--;
                    if (scoreLists[difficultyPosition - 1].isEmpty() && mClearRanking.isEnabled()) {
                        mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                        mClearRanking.setEnabled(false);
                    } else if (!scoreLists[difficultyPosition - 1].isEmpty() && !mClearRanking.isEnabled()) {
                        mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                        mClearRanking.setEnabled(true);
                    }
                    mRecyclerView.setAdapter(adapterPicker(difficultyPosition));
                    adapterPicker(difficultyPosition).notifyDataSetChanged();
                }
                if (difficultyPosition == 1) {
                    mDifficultyText.setText("EASY");
                    mPreviousDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    mPreviousDifficultyArrow.setEnabled(false);

                } else if (difficultyPosition == 2) {

                    if (!mNextDifficultyArrow.isEnabled()) {
                        mNextDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                        mNextDifficultyArrow.setEnabled(true);
                    }
                    mDifficultyText.setText("MEDIUM");
                } else {

                    mDifficultyText.setText("HARD");
                }
            }
        });

        mNextDifficultyArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDifficultyText.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
                mDifficultyText.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left));
                mNextDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow_click_animation));
                if (difficultyPosition + 1 > 3) {
                    difficultyPosition = 3;
                } else {
                    mRecyclerView.scheduleLayoutAnimation();
                    difficultyPosition++;

                    if (scoreLists[difficultyPosition - 1].isEmpty() && mClearRanking.isEnabled()) {
                        mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                        mClearRanking.setEnabled(false);
                    } else if (!scoreLists[difficultyPosition - 1].isEmpty() && !mClearRanking.isEnabled()) {
                        mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                        mClearRanking.setEnabled(true);
                    }
                    mRecyclerView.setAdapter(adapterPicker(difficultyPosition));
                    adapterPicker(difficultyPosition).notifyDataSetChanged();
                    mRecyclerView.getLayoutAnimation().start();
                }
                if (difficultyPosition == 1) {
                    mDifficultyText.setText("EASY");
                } else if (difficultyPosition == 2) {
                    if (!mPreviousDifficultyArrow.isEnabled()) {
                        mPreviousDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                        mPreviousDifficultyArrow.setEnabled(true);
                    }
                    mDifficultyText.setText("MEDIUM");
                } else {
                    mNextDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    mNextDifficultyArrow.setEnabled(false);
                    mDifficultyText.setText("HARD");
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        getSettings();
        soundEffectsSetup();

        myDb = new ScoresDatabaseHelper(this);


        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
        }

        loadScoresFromDatabase(myDb,scoreLists);
        container = findViewById(R.id.mGameOverLayout);
        mPreviousDifficultyArrow = findViewById(R.id.mPreviousArrow);
        mNextDifficultyArrow = findViewById(R.id.mNextArrow);
        mRetryButton = findViewById(R.id.mRetryButton);
        mRetryButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation));
        mGameOver = findViewById(R.id.mGameOver);
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(
                mGameOver, // Target object
                "TextColor", // Property name
                0xFFFF0000, // Value
                0xFFFF6262 // Value
        );

        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();

        mDifficultyText = findViewById(R.id.mDiffucultyText);
        mQuitButton = findViewById(R.id.mQuitButton);
        mClearRanking = findViewById(R.id.mClearRanking);
        mSummary = findViewById(R.id.mSummary);
        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_thick));
        mRecyclerView.addItemDecoration(itemDecorator);

        easyScoresAdapter = new ScoresAdapter(scoreLists[0]);
        mediumScoresAdapter = new ScoresAdapter(scoreLists[1]);
        hardScoresAdapter = new ScoresAdapter(scoreLists[2]);

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
                    if (mNextDifficultyArrow.isEnabled()) {
                        mNextDifficultyArrow.callOnClick();
                    }
                } else {
                    if (mPreviousDifficultyArrow.isEnabled()) {
                        mPreviousDifficultyArrow.callOnClick();
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        rankingPickerSetup();

        mRecyclerView.setAdapter(adapterPicker(difficultyPosition));
        adapterPicker(difficultyPosition).notifyDataSetChanged();

        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.cleareRanking(difficultyPosition);
                scoreLists[difficultyPosition - 1].clear();
                mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
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

            if (myDb.getRankingPlace(new ScoreInformation(null,0,score),gameDifficultyLevel)==1) {

                Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
                intent.putExtra("HIGH_SCORE", score);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
            }
            mSummary.setText("Your score:\t" + score + "\n" + "Round:\t" + round);

            if (score > 0) {
                final EditText input = new EditText(GameOverActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);


                builder = new AlertDialog.Builder(GameOverActivity.this, R.style.HighScoreDialogTheme);

                builder.setTitle("Please enter your name")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String name = "Unknown";
                                if (input.getText().toString().length() != 0) {
                                    name = input.getText().toString();
                                }

                                final ScoreInformation newScore = new ScoreInformation(name, round, score);
                                myDb.insertScore(newScore,gameDifficultyLevel);
                                loadScoresFromDatabase(myDb,scoreLists);
                                mRecyclerView.setAdapter(adapterPicker(gameDifficultyLevel));
                                adapterPicker(gameDifficultyLevel).notifyDataSetChanged();
                                mRecyclerView.scheduleLayoutAnimation();
                                final int indexOfScore = myDb.getRankingPlace(newScore,gameDifficultyLevel);
                                mSummary.append("\nYou took " + indexOfScore + " place!");

                                RecyclerView.SmoothScroller smoothScroller = new
                                        LinearSmoothScroller(getApplicationContext()) {
                                            @Override
                                            protected int getVerticalSnapPreference() {
                                                return LinearSmoothScroller.SNAP_TO_START;
                                            }
                                        };

                                smoothScroller.setTargetPosition(indexOfScore);
                                adapterPicker(difficultyPosition).notifyDataSetChanged();
                                mRecyclerView
                                        .getLayoutManager()
                                        .startSmoothScroll(smoothScroller);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setView(input)
                        .setIcon(R.drawable.ic_round_fiber_new_24px);


                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.show();

            }
        }
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
    }
}