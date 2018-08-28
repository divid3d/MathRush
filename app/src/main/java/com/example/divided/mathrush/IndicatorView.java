package com.example.divided.mathrush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;

import java.util.HashMap;


public class IndicatorView extends LinearLayout {
    ImageButton mPreviousArrow;
    ImageButton mNextArrow;
    TextSwitcher mIndicatorText;
    private int fromIndex;
    private int toIndex;
    private int currentPositionIndex;
    private String labels[];
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> textHashMap = new HashMap<>();
    private OnPositionChangeListener listener;
    private Animation animRightEnter = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
    private Animation animRightExit = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
    private Animation animLeftEnter = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
    private Animation animLeftExit = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
    private Animation arrowClicked = AnimationUtils.loadAnimation(getContext(), R.anim.arrow_click_animation);
    private Animation arrowFadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
    private Animation arrowFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        this.listener = null;
    }

    private int getNumberOfPositions() {
        return Math.abs(this.fromIndex - this.toIndex) + 1;
    }

    public void setOnPositionChangeListener(OnPositionChangeListener listener) {
        this.listener = listener;
    }

    public void setParameters(int from, int to, String labels[]) {
        this.fromIndex = from;
        this.toIndex = to;
        if ((getNumberOfPositions() == labels.length)) {
            textHashMap.clear();
            int labelsIterator = 0;
            for (int i = this.fromIndex; i <= this.toIndex; i++) {
                textHashMap.put(i, labels[labelsIterator]);
                labelsIterator++;
            }
        } else {
            Log.e("IndicatorView", "Labels count is wrong");
            for (int i = this.fromIndex; i <= this.toIndex; i++) {
                textHashMap.put(i, "");
            }
        }
    }

    public boolean isPositionedAtFirst() {
        return currentPositionIndex == this.fromIndex;
    }

    public boolean isPositionedAtLast() {
        return currentPositionIndex == this.toIndex;
    }

    public int getCurrentPosition() {
        return this.currentPositionIndex;
    }

    public void setCurrentPosition(int currentPosition) {
        if (currentPosition >= fromIndex && currentPosition <= toIndex) {
            this.currentPositionIndex = currentPosition;
            mIndicatorText.setText(textHashMap.get(currentPosition));
            if (isPositionedAtFirst()) {
                mPreviousArrow.startAnimation(arrowFadeOut);
                mPreviousArrow.setEnabled(false);
            }
            if (isPositionedAtLast()) {
                mNextArrow.startAnimation(arrowFadeOut);
                mNextArrow.setEnabled(false);
            }
        } else {
            Log.e("IndicatorView", "OUT OF RANGE. " + currentPosition + " is not in range of " + "< " + this.fromIndex + "," + this.toIndex + " >");
        }
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.indicator_view, this);
        initComponents();

    }

    public void setNext() {
        if (this.currentPositionIndex < this.toIndex) {
            mNextArrow.callOnClick();
        }
    }

    public void setPrev() {
        if (this.currentPositionIndex > this.fromIndex) {
            mPreviousArrow.callOnClick();
        }
    }

    public void setTextAnimations(Animation leftEnter, Animation leftExit, Animation rightEnter, Animation rightExit) {
        this.animLeftEnter = leftEnter;
        this.animLeftExit = leftExit;
        this.animRightEnter = rightEnter;
        this.animRightExit = rightExit;
    }

    public void setArrowsFadeOutAnimations(Animation arrowFadeIn, Animation arrowFadeOut) {
        this.arrowFadein = arrowFadeIn;
        this.arrowFadeOut = arrowFadeOut;
    }

    public void setArrowsClickAnimations(Animation arrowClicked) {
        this.arrowClicked = arrowClicked;
    }

    private void initComponents() {
        mPreviousArrow = findViewById(R.id.mPreviousArrow);
        mPreviousArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreviousArrow.startAnimation(arrowClicked);
                if (currentPositionIndex - 1 >= fromIndex) {
                    currentPositionIndex--;
                    if (listener != null) {
                        listener.onChange(getCurrentPosition());
                    }
                    mIndicatorText.setInAnimation(animLeftEnter);
                    mIndicatorText.setOutAnimation(animLeftExit);
                    mIndicatorText.setText(textHashMap.get(currentPositionIndex));
                    if (!mNextArrow.isEnabled()) {
                        mNextArrow.startAnimation(arrowFadein);
                        mNextArrow.setEnabled(true);
                    }
                    if (currentPositionIndex == fromIndex) {
                        mPreviousArrow.startAnimation(arrowFadeOut);
                        mPreviousArrow.setEnabled(false);
                    }
                }
            }
        });

        mNextArrow = findViewById(R.id.mNextArrow);
        mNextArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextArrow.startAnimation(arrowClicked);
                if (currentPositionIndex + 1 <= toIndex) {
                    currentPositionIndex++;
                    if (listener != null) {
                        listener.onChange(getCurrentPosition());
                    }
                    mIndicatorText.setInAnimation(animRightEnter);
                    mIndicatorText.setOutAnimation(animRightExit);
                    mIndicatorText.setText(textHashMap.get(currentPositionIndex));
                    if (!mPreviousArrow.isEnabled()) {
                        mPreviousArrow.startAnimation(arrowFadein);
                        mPreviousArrow.setEnabled(true);
                    }
                    if (currentPositionIndex == toIndex) {
                        mNextArrow.startAnimation(arrowFadeOut);
                        mNextArrow.setEnabled(false);
                    }
                }
            }
        });
        mIndicatorText = findViewById(R.id.mText);
    }

    public interface OnPositionChangeListener {
        void onChange(int position);
    }
}
