package com.example.divided.mathrush;

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

import java.util.ArrayList;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    ScoresDatabaseHelper myDb;

    private ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];
    private Button mClearRanking;
    private RecyclerView mRecyclerView;
    private SoundPool mySoundPool;
    private int soundIds[] = new int[3];

    private IndicatorView myIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        myDb = new ScoresDatabaseHelper(this);
        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
        }

        scoreLists = myDb.loadScoresFromDatabase();

        myIndicatorView = findViewById(R.id.mIndicatorView);
        String labels[] = new String[3];
        labels[0] = "Easy";
        labels[1] = "Medium";
        labels[2] = "Hard";
        myIndicatorView.setParameters(0, 2, labels);
        myIndicatorView.setCurrentPosition(0);
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

        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_thick)));
        mRecyclerView.addItemDecoration(itemDecorator);
        mClearRanking = findViewById(R.id.mClearRanking);
        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.clearRanking(myIndicatorView.getCurrentPosition());
                scoreLists[myIndicatorView.getCurrentPosition()].clear();
                mRecyclerView.getAdapter().notifyDataSetChanged();
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
                    myIndicatorView.setNext();
                } else {
                    myIndicatorView.setPrev();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(new ScoresAdapter(scoreLists[myIndicatorView.getCurrentPosition()]));
        mRecyclerView.getAdapter().notifyDataSetChanged();
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
