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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    TextView mTimeLeftTextView;

    SoundPool mySoundPool;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 100, 100, 500};

    Button[] buttons = new Button[4];

    CountDownTimer timeLeftCountDownTimer;
    int soundIds[] = new int[2];
    private NumberProgressBar mTimeLeftBar;
    private int roundNumber = 1;
    private int score = 0;
    private int timeLeft = 0;
    private char[] operationArray = new char[4];
    private int whichButtonIsCorrect = 0;
    private boolean soundEnabled;
    private boolean vibrationEnabled;
    private int gameDifficultyLevel;

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
    }

    public void getSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        vibrationEnabled = sharedPreferences.getBoolean("ENABLE_VIBRATION", true);
        gameDifficultyLevel = Integer.parseInt(sharedPreferences.getString("DIFFICULTY_LEVEL", "1"));

    }

    private int randomSign(boolean isPositive) {
        if (isPositive) {
            return 1;
        } else return -1;
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
        for (Button button : buttons) {
            button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));
        }
        mEquationBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));


        Random randomGenerator = new Random();
        randomGenerator.setSeed(System.currentTimeMillis());

        mRoundBox.setText(Integer.toString(roundNumber));
        mScoreBox.setText(Integer.toString(score));

        int correctAnswer = 0;
        int firstElement;
        int secondElement;
        int whichOperation;

        if (gameDifficultyLevel == 1) {
            whichOperation = randomGenerator.nextInt(2);
        } else {
            whichOperation = randomGenerator.nextInt(4);
        }

        if (operationArray[whichOperation] == '*') {

            if (gameDifficultyLevel == 1) {
                do {
                    firstElement = (randomGenerator.nextInt(20) + 1);
                    secondElement = (randomGenerator.nextInt(20) + 1);
                }
                while (Math.abs(resultOfOperation(operationArray[whichOperation], firstElement, secondElement)) > 50);
            } else {
                firstElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(20) + 1);
                secondElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(20) + 1);
            }
        } else {
            if (gameDifficultyLevel == 1) {
                do {
                    firstElement = (randomGenerator.nextInt(20) + 1);
                    secondElement = (randomGenerator.nextInt(20) + 1);
                }
                while (Math.abs(resultOfOperation(operationArray[whichOperation], firstElement, secondElement)) > 50);
            } else {
                firstElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);
                secondElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);
            }
        }


        switch (operationArray[whichOperation]) {
            case '+':
                correctAnswer = firstElement + secondElement;
                break;
            case '-':
                correctAnswer = firstElement - secondElement;
                break;
            case '*':
                correctAnswer = firstElement * secondElement;
                break;
            case ':':

                if (firstElement % secondElement == 0 && Math.abs(secondElement) > 1 && Math.abs(firstElement) != Math.abs(secondElement)) {
                    correctAnswer = firstElement / secondElement;
                } else {
                    while (firstElement % secondElement != 0 || Math.abs(secondElement) == 1 || Math.abs(firstElement) == Math.abs(secondElement)) {
                        firstElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);
                        secondElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);
                    }
                    correctAnswer = firstElement / secondElement;

                }
        }


        if (secondElement < 0) {
            String toShow = firstElement + " " + operationArray[whichOperation] + " " + "(" + secondElement + ")" + " = ";
            mEquationBox.setText(toShow);
        } else {
            String toShow = firstElement + " " + operationArray[whichOperation] + " " + secondElement + " = ";
            mEquationBox.setText(toShow);
        }


        whichButtonIsCorrect = randomGenerator.nextInt(4) + 1;

        buttonsSetup(buttons, whichButtonIsCorrect, correctAnswer);
        mEquationBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_instantly));

        mTimeLeftTextView.setText("Time remaining: " + String.format("%.2f", 5f) + " s");
        mTimeLeftBar.setProgress(mTimeLeftBar.getMax());
        timeLeftCountDownTimer.start();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSettings();
        soundEffectsSetup();

        //array initialize
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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        timeLeftCountDownTimer = new CountDownTimer(5000, 10) {

            public void onTick(long millisUntilFinished) {
                float percentage = 0;
                timeLeft = (int) millisUntilFinished;
                String firstWord = "Time remaining: ";
                String secondWord = String.format("%.2f", (double) (millisUntilFinished / 1000.0)) + " s";
                Spannable timeLeftText = new SpannableString(firstWord + secondWord);
                timeLeftText.setSpan(new RelativeSizeSpan(1.2f), firstWord.length(), firstWord.length() + secondWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTimeLeftTextView.setText(timeLeftText);
                mTimeLeftBar.setProgress(mTimeLeftBar.getMax() - (5000 - (int) millisUntilFinished));

            }

            public void onFinish() {
                //mTimeLeftTextView.setText("Time remaining: " + String.format("%.2f", 0f) + " s");
                mTimeLeftBar.setProgress(0);
                if (vibrator.hasVibrator() && vibrationEnabled) {
                    vibrator.vibrate(vibrationPattern, -1);
                }
                Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                intent.putExtra("ROUND", roundNumber);
                intent.putExtra("SCORE", score);
                startActivity(intent);
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
                            mySoundPool.play(soundIds[0], 1, 1, 1, 0, 1.0f);
                        }
                        roundNumber++;
                        score = score + (roundNumber * (timeLeft / 100));
                        timeLeftCountDownTimer.cancel();
                        roundInit(roundNumber, gameDifficultyLevel);
                    } else {
                        if (soundEnabled) {
                            mySoundPool.play(soundIds[1], 1, 1, 1, 0, 1.0f);
                        }
                        timeLeftCountDownTimer.cancel();
                        if (vibrator.hasVibrator() && vibrationEnabled) {
                            vibrator.vibrate(1100);
                        }

                        Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                        intent.putExtra("ROUND", roundNumber);
                        intent.putExtra("SCORE", score);
                        startActivity(intent);
                    }

                }
            });
        }


        roundInit(roundNumber, gameDifficultyLevel);
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
        if (mySoundPool != null) {
            mySoundPool.release();
        }
    }
}
