package com.example.divided.mathrush;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.List;


public class GameOverActivity extends AppCompatActivity {


    final String FILE_NAME = "Scores";
    Button mRetryButton;
    Button mQuitButton;
    Button mClearRanking;
    TextView mSummary;
    TextView mGameOver;
    AnimationDrawable anim;
    ConstraintLayout container;
    RecyclerView mRecyclerView;
    AlertDialog.Builder builder;
    Comparator<ScoreInformation> myScoreComparator = new Comparator<ScoreInformation>() {
        @Override
        public int compare(ScoreInformation o1, ScoreInformation o2) {

            if (Integer.parseInt(o1.getScore()) < Integer.parseInt(o2.getScore()))
                return 1;
            else if (Integer.parseInt(o1.getScore()) > Integer.parseInt(o2.getScore()))
                return -1;
            else {
                return Integer.compare(Integer.parseInt(o2.getRound()), Integer.parseInt(o1.getRound()));
            }
        }
    };
    private List<ScoreInformation> scoreList = new ArrayList<>();
    private ScoresAdapter mScoresAdapter;

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
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


        container = findViewById(R.id.mGameOverLayout);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);
        mRetryButton = findViewById(R.id.mRetryButton);
        mGameOver = findViewById(R.id.mGameOver);
        mQuitButton = findViewById(R.id.mQuitButton);
        mClearRanking = findViewById(R.id.mClearRanking);
        mSummary = findViewById(R.id.mSummary);
        mRecyclerView = findViewById(R.id.mScoreView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mScoresAdapter = new ScoresAdapter(scoreList);
        mRecyclerView.setAdapter(mScoresAdapter);

        mRetryButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation));


        for (String scoreInformation : readScoresFromFile(FILE_NAME).split("\n")) {
            String[] values;
            values = scoreInformation.split("_");
            if (values.length == 3) {
                scoreList.add(new ScoreInformation(values[0], values[1], values[2]));
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
                MediaPlayer startSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start_click_new);
                startSoundPlayer.start();

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
            final String score = extras.getString("SCORE");
            final String round = extras.getString("ROUND");

            mSummary.setText("Your score:\t" + score + "\n" + "Round:\t" + round);

            if (Integer.parseInt(score) > 0) {

                final EditText input = new EditText(GameOverActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);

                builder = new AlertDialog.Builder(GameOverActivity.this);

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
                                mRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(input);

                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();

            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
    }
}