package com.example.divided.mathrush;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView mEquationBox;
    TextView mScoreBox;
    TextView mRoundBox;
    TextView mTimeLeftTextView;

    MediaPlayer correctAnswerPlayer;
    MediaPlayer wrongAnswerPlayer;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 100, 100, 500};

    Button[] buttons = new Button[4];

    CountDownTimer timeLeftCountDownTimer;
    private NumberProgressBar mTimeLeftBar;

    private int roundNumber = 1;
    private int score = 0;
    private int timeLeft = 0;

    private char[] operationArray = new char[4];
    private int whichButtonIsCorrect = 0;


    private int randomSign(boolean isPositive) {
        if (isPositive) {
            return 1;
        } else return -1;
    }

    private void buttonsSetup(Button[] buttons, int whichButtonIsCorrect, int correctAnswer) {

        Deque<Integer> otherAnswers = new ArrayDeque<Integer>();


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

    private void roundInit(int roundNumber) {
        for (Button button : buttons) {
            button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));
        }
        mEquationBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_instantly));

        Random randomGenerator = new Random();
        randomGenerator.setSeed(System.currentTimeMillis());

        mRoundBox.setText("Round:\t" + Integer.toString(roundNumber));
        mScoreBox.setText("Score:\t" + score);

        int correctAnswer = 0;
        int firstElement;
        int secondElement;

        int whichOperation = randomGenerator.nextInt(4);

        if (operationArray[whichOperation] == '*') {

            firstElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(20) + 1);

            secondElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(20) + 1);
        } else {
            firstElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);

            secondElement = randomSign(randomGenerator.nextBoolean()) * (randomGenerator.nextInt(100) + 1);
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

        //array initialize
        operationArray[0] = '+';
        operationArray[1] = '-';
        operationArray[2] = '*';
        operationArray[3] = ':';

        mEquationBox = (TextView) findViewById(R.id.mEquationBox);
        mRoundBox = (TextView) findViewById(R.id.mRoundBox);
        mScoreBox = (TextView) findViewById(R.id.mScoreBox);
        mTimeLeftTextView = (TextView) findViewById(R.id.mTimeLeftTextView);
        mTimeLeftBar = (NumberProgressBar) findViewById(R.id.mTimeLeftBar);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        timeLeftCountDownTimer = new CountDownTimer(5000, 10) {

            public void onTick(long millisUntilFinished) {
                timeLeft = (int) millisUntilFinished;
                mTimeLeftTextView.setText("Time remaining: " + String.format("%.2f", (double) (millisUntilFinished / 1000.0)) + " s");
                mTimeLeftBar.setProgress(mTimeLeftBar.getMax() - (5000 - (int) millisUntilFinished));

            }

            public void onFinish() {
                mTimeLeftTextView.setText("Time remaining: " + String.format("%.2f", 0f) + " s");
                mTimeLeftBar.setProgress(0);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(vibrationPattern, -1);
                }
                Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                intent.putExtra("ROUND", Integer.toString(roundNumber));
                intent.putExtra("SCORE", Integer.toString(score));
                startActivity(intent);
            }
        };


        buttons[0] = (Button) findViewById(R.id.mAnswerButton1);
        buttons[1] = (Button) findViewById(R.id.mAnswerButton2);
        buttons[2] = (Button) findViewById(R.id.mAnswerButton3);
        buttons[3] = (Button) findViewById(R.id.mAnswerButton4);


        correctAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.correct_answer);
        wrongAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.incorrect_answer);


        for (int i = 0; i < buttons.length; i++) {
            final int indexOfButton = i + 1;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whichButtonIsCorrect == indexOfButton) {
                        correctAnswerPlayer.reset();
                        wrongAnswerPlayer.reset();
                        correctAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.correct_answer);
                        wrongAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.incorrect_answer);
                        correctAnswerPlayer.start();
                        roundNumber++;
                        score = score + (roundNumber * (timeLeft / 100));
                        timeLeftCountDownTimer.cancel();
                        roundInit(roundNumber);
                    } else {
                        correctAnswerPlayer.reset();
                        wrongAnswerPlayer.reset();
                        correctAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.correct_answer);
                        wrongAnswerPlayer = MediaPlayer.create(getApplicationContext(), R.raw.incorrect_answer);
                        wrongAnswerPlayer.start();
                        timeLeftCountDownTimer.cancel();
                        if (vibrator.hasVibrator()) {
                            vibrator.vibrate(1100);
                        }

                        Intent intent = new Intent(getBaseContext(), GameOverActivity.class);
                        intent.putExtra("ROUND", Integer.toString(roundNumber));
                        intent.putExtra("SCORE", Integer.toString(score));
                        startActivity(intent);
                    }

                }
            });
        }


        roundInit(roundNumber);
    }

    @Override
    public void onBackPressed() {

        timeLeftCountDownTimer.cancel();
        Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
        startActivity(intent);
    }
}