package com.example.divided.mathrush;

import android.database.Cursor;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    ScoresDatabaseHelper myDb;

    private ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];
    private ScoresAdapter easyScoresAdapter;
    private ScoresAdapter mediumScoresAdapter;
    private ScoresAdapter hardScoresAdapter;
    private Button mClearRanking;
    private RecyclerView mRecyclerView;
    private ImageButton mNextDifficultyArrow;
    private ImageButton mPreviousDifficultyArrow;
    private TextSwitcher mDifficultyText;
    private SoundPool mySoundPool;
    private int soundIds[] = new int[3];
    private int difficultyPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        myDb = new ScoresDatabaseHelper(this);
        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
        }

        loadScoresFromDatabase(myDb, scoreLists);

        easyScoresAdapter = new ScoresAdapter(scoreLists[0]);
        mediumScoresAdapter = new ScoresAdapter(scoreLists[1]);
        hardScoresAdapter = new ScoresAdapter(scoreLists[2]);

        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_thick));
        mRecyclerView.addItemDecoration(itemDecorator);

        mNextDifficultyArrow = findViewById(R.id.mNextArrow);
        mPreviousDifficultyArrow = findViewById(R.id.mPreviousArrow);
        mDifficultyText = findViewById(R.id.mDiffucultyText);
        mClearRanking = findViewById(R.id.mClearRanking);
        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.cleareRanking(difficultyPosition);
                scoreLists[difficultyPosition - 1].clear();
                adapterPicker(difficultyPosition).notifyDataSetChanged();
                mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mClearRanking.setEnabled(false);
            }
        });
        final Button mBackButton = findViewById(R.id.mBackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
    }

    public void rankingPickerSetup() {


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


    private void clearRanking(String filename) {

    }


    private ScoresAdapter adapterPicker(int difficultyPosition) {
        if (difficultyPosition == 1) {
            return easyScoresAdapter;
        } else if (difficultyPosition == 2) {
            return mediumScoresAdapter;
        } else {
            return hardScoresAdapter;
        }
    }

    private void loadScoresFromDatabase(ScoresDatabaseHelper myDb, ArrayList<ScoreInformation>[] scoreLists) {
        for (int i = 1; i <= 3; i++) {
            Cursor res = myDb.getScores(i);
            if (res.getCount() == 0) {
                return;
            }
            if (!scoreLists[i - 1].isEmpty()) {
                scoreLists[i - 1].clear();
            }
            while (res.moveToNext()) {
                final String name = res.getString(1);
                final int score = res.getInt(2);
                final int round = res.getInt(3);

                ScoreInformation newScore = new ScoreInformation(name, round, score);
                scoreLists[i - 1].add(newScore);
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
