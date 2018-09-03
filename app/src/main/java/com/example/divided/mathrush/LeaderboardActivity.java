package com.example.divided.mathrush;

import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
    private AppCompatDelegate mDelegate;
    private int soundIds[] = new int[3];

    private IndicatorView myIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24px);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    public AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
