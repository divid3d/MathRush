package com.example.divided.mathrush;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardActivity extends AppCompatActivity {

    final String EASY_SCORES_FILENAME = "EASY_SCORES";
    final String MEDIUM_SCORES_FILENAME = "MEDIUM_SCORES";
    final String HARD_SCORES_FILENAME = "HARD_SCORES";

    Comparator<ScoreInformation> myScoreComparator = new Comparator<ScoreInformation>() {
        @Override
        public int compare(ScoreInformation o1, ScoreInformation o2) {

            if (o1.getScore() == o2.getScore()) {
                return Integer.compare(o2.getRound(), o1.getRound());
            } else {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        }
    };
    private ArrayList<ScoreInformation>[] scoreLists = (ArrayList<ScoreInformation>[]) new ArrayList[3];
    private ScoresAdapter easyScoresAdapter;
    private ScoresAdapter mediumScoresAdapter;
    private ScoresAdapter hardScoresAdapter;
    private Button mClearRanking;
    private RecyclerView mRecyclerView;
    private ImageButton mNextDifficultyArrow;
    private ImageButton mPreviousDifficultyArrow;
    private TextSwitcher mDifficultyText;
    private int difficultyPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
        }

        easyScoresAdapter = new ScoresAdapter(scoreLists[0]);
        mediumScoresAdapter = new ScoresAdapter(scoreLists[1]);
        hardScoresAdapter = new ScoresAdapter(scoreLists[2]);

        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.score_divider));
        mRecyclerView.addItemDecoration(itemDecorator);

        mNextDifficultyArrow = findViewById(R.id.mNextArrow);
        mPreviousDifficultyArrow = findViewById(R.id.mPreviousArrow);
        mDifficultyText = findViewById(R.id.mDiffucultyText);
        mClearRanking = findViewById(R.id.mClearRanking);
        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRanking(filenamePicker(difficultyPosition));

                scoreLists[difficultyPosition - 1].clear();

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
        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        rankingPickerSetup();


        loadScoresToLists(scoreLists);
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
        File testFile = new File(this.getExternalFilesDir(null), filename);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    private String readScoresFromFile(String filename) {
        String outputString = "";

        File testFile = new File(this.getExternalFilesDir(null), filename);
        if (testFile != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    outputString += line;
                    outputString += "\n";
                }

                reader.close();

            } catch (Exception e) {
                Log.e("READ_FILE", "Unable to read " + filename + " file.");
            }
        } else {
            return "";
        }
        return outputString;

    }

    public String filenamePicker(int difficultyPosition) {
        if (difficultyPosition == 1) {
            return EASY_SCORES_FILENAME;
        } else if (difficultyPosition == 2) {
            return MEDIUM_SCORES_FILENAME;
        } else {
            return HARD_SCORES_FILENAME;
        }
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

    public void loadScoresToLists(ArrayList<ScoreInformation>[] scoreLists) {
        for (int i = 0; i < scoreLists.length; i++) {
            if (scoreLists[i] != null) {
                scoreLists[i].clear();
            }
            for (String scoreInformation : readScoresFromFile(filenamePicker(i + 1)).split("\n")) {
                String[] values;
                values = scoreInformation.split("_");
                if (values.length == 3) {
                    scoreLists[i].add(new ScoreInformation(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                }

            }
            if (scoreLists[i] != null) {
                Collections.sort(scoreLists[i], myScoreComparator);
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
