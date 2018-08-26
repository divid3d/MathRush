package com.example.divided.mathrush;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.MediaPlayer;
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
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class GameOverActivity extends AppCompatActivity {


    final String EASY_SCORES_FILENAME = "EASY_SCORES";
    final String MEDIUM_SCORES_FILENAME = "MEDIUM_SCORES";
    final String HARD_SCORES_FILENAME = "HARD_SCORES";

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

    public void getSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        gameDifficultyLevel = Integer.parseInt(sharedPreferences.getString("DIFFICULTY_LEVEL", "1"));


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

    private void writeScoresToFile(String filename, String data, boolean append) {
        try {

            File file = new File(this.getExternalFilesDir(null), filename);
            if (!file.exists())
                file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(data + "\n");
            writer.close();

        } catch (IOException e) {
            Log.e("WRITE_FILE", "Unable to write " + filename + " file.");
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

    private void clearRanking(String filename) {
        File testFile = new File(this.getExternalFilesDir(null), filename);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        getSettings();

        for (int i = 0; i < 3; i++) {
            scoreLists[i] = new ArrayList<>();
        }

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
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.score_divider));
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
        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        rankingPickerSetup();


        loadScoresToLists(scoreLists);
        mRecyclerView.setAdapter(adapterPicker(difficultyPosition));
        adapterPicker(difficultyPosition).notifyDataSetChanged();

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

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRetryButton.setEnabled(false);
                mQuitButton.setEnabled(false);
                if (soundEnabled) {
                    MediaPlayer startSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start_click_new);
                    startSoundPlayer.start();
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

                                writeScoresToFile(filenamePicker(difficultyPosition), name + "_" + round + "_" + score, true);

                                final ScoreInformation newScore = new ScoreInformation(name, round, score);
                                scoreLists[difficultyPosition - 1].add(newScore);
                                if (scoreLists[difficultyPosition - 1] != null) {
                                    Collections.sort(scoreLists[difficultyPosition - 1], myScoreComparator);
                                }
                                mRecyclerView.setAdapter(adapterPicker(difficultyPosition));
                                adapterPicker(difficultyPosition).notifyDataSetChanged();
                                mRecyclerView.scheduleLayoutAnimation();
                                final int indexOfScore = scoreLists[difficultyPosition - 1].indexOf(newScore);
                                //Toast.makeText(getApplicationContext(),Integer.toString(indexOfScore),Toast.LENGTH_LONG).show();
                                mSummary.append("\nYou took " + (indexOfScore + 1) + " place!");

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

    @Override
    protected void onResume() {
        super.onResume();
        //if (anim != null && !anim.isRunning())
        //    anim.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // if (anim != null && anim.isRunning())
        //    anim.stop();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
    }
}