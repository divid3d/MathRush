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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GameOverActivity extends AppCompatActivity {


    final String FILE_NAME = "Scores";
    Button mRetryButton;
    Button mQuitButton;
    Button mClearRanking;
    TextView mSummary;
    TextView mGameOver;
    // AnimationDrawable anim;
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
    private boolean vibrationEnabled;
    private int gameDifficultyLevel;
    private List<ScoreInformation> scoreList = new ArrayList<>();
    private ScoresAdapter mScoresAdapter;

    public void getSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        vibrationEnabled = sharedPreferences.getBoolean("ENABLE_VIBRATION", true);
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
                mRecyclerView.scheduleLayoutAnimation();
                mDifficultyText.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
                mDifficultyText.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
                mPreviousDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow_click_animation));
                if (difficultyPosition - 1 < 1) {
                    difficultyPosition = 1;
                } else {
                    difficultyPosition--;
                    mScoresAdapter.notifyDataSetChanged();
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
                Toast.makeText(getApplicationContext(), "Position " + difficultyPosition, Toast.LENGTH_SHORT).show();
            }
        });

        mNextDifficultyArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scheduleLayoutAnimation();
                mDifficultyText.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
                mDifficultyText.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left));
                mNextDifficultyArrow.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow_click_animation));
                if (difficultyPosition + 1 > 3) {
                    difficultyPosition = 3;
                } else {
                    difficultyPosition++;
                    scoreList.add(new ScoreInformation("testuje", 1234, 1234));
                    mScoresAdapter.notifyDataSetChanged();
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
                Toast.makeText(getApplicationContext(), "Position: " + difficultyPosition, Toast.LENGTH_SHORT).show();
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

        container = findViewById(R.id.mGameOverLayout);
        mPreviousDifficultyArrow = findViewById(R.id.mPreviousArrow);
        mNextDifficultyArrow = findViewById(R.id.mNextArrow);
        mRetryButton = findViewById(R.id.mRetryButton);
        mGameOver = findViewById(R.id.mGameOver);
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(
                mGameOver, // Target object
                "TextColor", // Property name
                0xFFFF0000, // Value
                0xFFFF6262 // Value
        );

        // Set value animator evaluator
        valueAnimator.setEvaluator(new ArgbEvaluator());
        // Set animation duration in milliseconds
        valueAnimator.setDuration(3000);
        // Animation repeat count and mode
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);

        // Finally, start the animation
        valueAnimator.start();
        mDifficultyText = findViewById(R.id.mDiffucultyText);
        mQuitButton = findViewById(R.id.mQuitButton);
        mClearRanking = findViewById(R.id.mClearRanking);
        mSummary = findViewById(R.id.mSummary);
        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mScoresAdapter = new ScoresAdapter(scoreList);
        mRecyclerView.setAdapter(mScoresAdapter);

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
                    if (difficultyPosition < 3) {
                        mNextDifficultyArrow.callOnClick();
                    }
                } else {
                    if (difficultyPosition > 1) {
                        mPreviousDifficultyArrow.callOnClick();
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        rankingPickerSetup();


        for (String scoreInformation : readScoresFromFile(FILE_NAME).split("\n")) {
            String[] values;
            values = scoreInformation.split("_");
            if (values.length == 3) {
                scoreList.add(new ScoreInformation(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2])));
            }

        }

        Collections.sort(scoreList, myScoreComparator);
        mScoresAdapter.notifyDataSetChanged();

        mClearRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRanking(FILE_NAME);
                scoreList.clear();

                mClearRanking.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                mClearRanking.setEnabled(false);
                mRecyclerView.setEnabled(false);
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

                                writeScoresToFile(FILE_NAME, name + "_" + round + "_" + score, true);

                                final ScoreInformation newScore = new ScoreInformation(name, round, score);
                                scoreList.add(newScore);
                                Collections.sort(scoreList, myScoreComparator);
                                mScoresAdapter.notifyDataSetChanged();
                                final int indexOfScore = scoreList.indexOf(newScore);
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
                                mScoresAdapter.notifyDataSetChanged();
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