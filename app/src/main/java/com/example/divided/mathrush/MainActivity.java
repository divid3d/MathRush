package com.example.divided.mathrush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView mEquationBox;
    TickerView mScoreBox;
    TickerView mRoundBox;
    TextSwitcher mTimeLeftTextView;
    TextView mRoundScore;
    LivesView mLivesView;
    SoundPool mySoundPool;
    Vibrator vibrator;
    Button[] buttons = new Button[4];
    CountDownTimer timeLeftCountDownTimer;
    long[] vibrationPattern = {0, 100, 100, 500};

    int soundIds[] = new int[3];
    private NumberProgressBar mTimeLeftBar;
    private int roundNumber = 1;
    private int score = 0;
    private int timeLeft = 0;
    private char[] operationArray = new char[4];
    private int whichButtonIsCorrect = 0;
    private boolean soundEnabled;
    private boolean vibrationEnabled;
    private int gameDifficultyLevel;

    private static int getRandomIntegerInRange(int min, int max, boolean zeroInclusive, int seed) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        r.setSeed(System.currentTimeMillis() * seed);
        int result;
        if (zeroInclusive) {
            return r.nextInt((max - min) + 1) + min;
        } else {
            do {
                result = r.nextInt((max - min) + 1) + min;
            } while (result == 0);
            return result;
        }
    }

    @Override
    public void onBackPressed() {
        timeLeftCountDownTimer.cancel();
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySoundPool.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSettings();
        soundEffectsSetup();

        operationArray[0] = '+';
        operationArray[1] = '-';
        operationArray[2] = '*';
        operationArray[3] = ':';

        mEquationBox = findViewById(R.id.mEquationBox);
        mRoundBox = findViewById(R.id.mRoundBox);
        mRoundBox.setCharacterLists(TickerUtils.provideNumberList());
        mScoreBox = findViewById(R.id.mScoreBox);
        mScoreBox.setCharacterLists(TickerUtils.provideNumberList());
        mTimeLeftTextView = findViewById(R.id.mTimeLeftTextView);
        mTimeLeftBar = findViewById(R.id.mTimeLeftBar);
        mRoundScore = findViewById(R.id.mRoundScore);
        mLivesView = findViewById(R.id.mLivesView);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        timeLeftCountDownTimer = new CountDownTimer(5000, 1) {

            public void onTick(long millisUntilFinished) {
                TextView currentTextView = (TextView) mTimeLeftTextView.getCurrentView();
                final String currentText = currentTextView.getText().toString();
                if (!currentText.equals(String.valueOf(((millisUntilFinished / 1000)) + 1))) {
                    if(soundEnabled) {
                        mySoundPool.play(soundIds[2], 1, 1, 1, 0, 1f);
                    }
                    mTimeLeftTextView.setText(String.valueOf(((millisUntilFinished / 1000)) + 1));
                }
                timeLeft = (int) millisUntilFinished;
                mTimeLeftBar.setProgress(mTimeLeftBar.getMax() - (5000 - (int) millisUntilFinished));
            }

            public void onFinish() {

                if (mLivesView.getLifesCount() > 1) {
                    if (vibrator.hasVibrator() && vibrationEnabled) {
                        vibrator.vibrate(vibrationPattern, -1);
                    }
                    mLivesView.takeAwayOneLife();
                    roundInit(roundNumber, gameDifficultyLevel);
                } else {
                    if (vibrator.hasVibrator() && vibrationEnabled) {
                        vibrator.vibrate(1100);
                    }
                    mTimeLeftBar.setProgress(0);
                    if (new ScoresDatabaseHelper(getApplicationContext()).getRankingPlace(new ScoreInformation(null, 0, score), gameDifficultyLevel) == 1) {
                        Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
                        intent.putExtra("HIGH_SCORE", score);
                        intent.putExtra("ROUND", roundNumber);
                        intent.putExtra("SCORE", score);
                        startActivity(intent);
                        overridePendingTransition(R.anim.transition_in_highscore_activity, R.anim.fade_out);
                        finish();
                    } else {
                        Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                        intent.putExtra("ROUND", roundNumber);
                        intent.putExtra("SCORE", score);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
            }
        };

        buttons[0] = findViewById(R.id.mAnswerButton1);
        buttons[1] = findViewById(R.id.mAnswerButton2);
        buttons[2] = findViewById(R.id.mAnswerButton3);
        buttons[3] = findViewById(R.id.mAnswerButton4);

        for (int i = 0; i < buttons.length; i++) {
            final int indexOfButton = i + 1;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whichButtonIsCorrect == indexOfButton) {
                        if (soundEnabled) {
                            mySoundPool.play(soundIds[0], 1, 1, 2, 0, 1.0f);
                        }
                        roundNumber++;
                        score = score + (roundNumber * (timeLeft / 100));
                        mRoundScore.setText("+" + (roundNumber * (timeLeft / 100)));
                        mRoundScore.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                        timeLeftCountDownTimer.cancel();
                        roundInit(roundNumber, gameDifficultyLevel);
                    } else {
                        if (mLivesView.getLifesCount() > 1) {
                            if (vibrator.hasVibrator() && vibrationEnabled) {
                                vibrator.vibrate(vibrationPattern, -1);
                            }
                            if (soundEnabled) {
                                mySoundPool.play(soundIds[1], 1, 1, 2, 0, 1.0f);
                            }
                            mLivesView.takeAwayOneLife();
                            roundInit(roundNumber, gameDifficultyLevel);
                        } else {
                            if (soundEnabled) {
                                mySoundPool.play(soundIds[1], 1, 1, 2, 0, 1.0f);
                            }
                            timeLeftCountDownTimer.cancel();
                            if (vibrator.hasVibrator() && vibrationEnabled) {
                                vibrator.vibrate(1100);
                            }
                            if (new ScoresDatabaseHelper(getApplicationContext()).getRankingPlace(new ScoreInformation(null, 0, score), gameDifficultyLevel) == 1) {
                                Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
                                intent.putExtra("ROUND", roundNumber);
                                intent.putExtra("SCORE", score);
                                startActivity(intent);
                                overridePendingTransition(R.anim.transition_in_highscore_activity, R.anim.fade_out);
                                finish();
                            } else {
                                Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                                intent.putExtra("ROUND", roundNumber);
                                intent.putExtra("SCORE", score);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }
                    }
                }
            });
        }
        roundInit(roundNumber, gameDifficultyLevel);
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

        soundIds[0] = mySoundPool.load(this, R.raw.correct_answer_newest, 2);
        soundIds[1] = mySoundPool.load(this, R.raw.incorrect_answer, 2);
        soundIds[2] = mySoundPool.load(this,R.raw.clock_tick,1);
    }

    public void getSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        vibrationEnabled = sharedPreferences.getBoolean("ENABLE_VIBRATION", true);
        gameDifficultyLevel = Integer.parseInt(sharedPreferences.getString("DIFFICULTY_LEVEL", "1"));
    }

    private void buttonsSetup(Button[] buttons, int whichButtonIsCorrect, int correctAnswer) {
        Deque<Integer> otherAnswers = new ArrayDeque<>();

        while (otherAnswers.size() != 3) {
            Random randomGenerator = new Random();
            randomGenerator.setSeed(System.currentTimeMillis());
            if (randomGenerator.nextInt(1) % 2 == 0) {
                int generated = correctAnswer + (randomGenerator.nextInt(10) + 1);
                if (!otherAnswers.contains(generated)) {
                    otherAnswers.add(generated);
                }
            } else {
                int generated = correctAnswer - (randomGenerator.nextInt(10) + 1);
                if (!otherAnswers.contains(generated)) {
                    otherAnswers.add(generated);
                }
            }
            for (Button button : buttons) {
                button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_instantly));
            }
        }
        for (int i = 0; i < buttons.length; i++) {
            if (i + 1 == whichButtonIsCorrect) {
                buttons[i].setText(Integer.toString(correctAnswer));
            } else {
                if (otherAnswers.size() > 0) {
                    buttons[i].setText(Integer.toString(otherAnswers.pop()));
                }
            }
        }
    }

    private int resultOfOperation(char operation, int firstElement, int secondElement) {
        int result;

        switch (operation) {
            case '+':
                result = firstElement + secondElement;
                break;
            case '-':
                result = firstElement - secondElement;
                break;
            case ':':
                result = firstElement / secondElement;
                break;
            case '*':
                result = firstElement * secondElement;
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

    private void roundInit(int roundNumber, int gameDifficultyLevel) {
        int firstElement = 0;
        int secondElement = 0;
        int result = 0;
        char operation;

        for (Button button : buttons) {
            button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));
        }
        mEquationBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));
        mRoundBox.setText(Integer.toString(roundNumber));
        mScoreBox.setText(Integer.toString(score));

        if (gameDifficultyLevel == 0) {
            operation = operationArray[getRandomIntegerInRange(0, 1, true, roundNumber)];
        } else {
            operation = operationArray[getRandomIntegerInRange(0, 3, true, roundNumber)];
        }


        int minNumberBound = -50 * (gameDifficultyLevel + 1);
        final int maxNumberBound = 50 * (gameDifficultyLevel + 1);

        if (gameDifficultyLevel == 0) {
            minNumberBound = 1;
        }

        switch (operation) {
            case '+':
                do {
                    firstElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    secondElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    result = resultOfOperation(operation, firstElement, secondElement);
                }
                while (Math.abs(firstElement) == Math.abs(secondElement)
                        || Math.abs(result) > 100 * (gameDifficultyLevel + 1));
                break;

            case '-':
                do {
                    firstElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    secondElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    result = resultOfOperation(operation, firstElement, secondElement);
                }
                while (Math.abs(firstElement) == Math.abs(secondElement) || Math.abs(result) > 100 * (gameDifficultyLevel + 1));
                break;

            case '*':
                do {
                    firstElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    secondElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    result = resultOfOperation(operation, firstElement, secondElement);
                }
                while (Math.abs(firstElement) == Math.abs(secondElement)
                        || Math.abs(result) > 100 * (gameDifficultyLevel + 1)
                        || Math.abs(firstElement) == 1
                        || Math.abs(secondElement) == 1);
                break;

            case ':':
                do {
                    firstElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    secondElement = getRandomIntegerInRange(minNumberBound, maxNumberBound, false, roundNumber);
                    result = resultOfOperation(operation, firstElement, secondElement);
                }
                while (Math.abs(firstElement) == Math.abs(secondElement)
                        || Math.abs(result) > 100 * (gameDifficultyLevel + 1)
                        || Math.abs(firstElement) % Math.abs(secondElement) != 0
                        || Math.abs(firstElement) == 1
                        || Math.abs(secondElement) == 1);
                break;
        }

        if (secondElement < 0) {
            mEquationBox.setText(firstElement + " " + operation + " " + "(" + secondElement + ")" + " = ");
        } else {
            mEquationBox.setText(firstElement + " " + operation + " " + secondElement + " = ");
        }

        whichButtonIsCorrect = getRandomIntegerInRange(1, 4, false, roundNumber);
        buttonsSetup(buttons, whichButtonIsCorrect, result);
        mEquationBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_instantly));
        mTimeLeftBar.setProgress(mTimeLeftBar.getMax());
        timeLeftCountDownTimer.start();
    }
}
