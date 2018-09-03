package com.example.divided.mathrush;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LivesView extends LinearLayout {

    private ImageView[] hearts = new ImageView[3];
    private int lifesCount;

    public LivesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.livesview_layout, this);
        initComponents();
        lifesCount = 3;
    }

    private void initComponents() {
        hearts[0] = findViewById(R.id.mHeart1);
        hearts[1] = findViewById(R.id.mHeart2);
        hearts[2] = findViewById(R.id.mHeart3);
    }

    public void takeAwayOneLife() {
        if (lifesCount > 0) {
            animateHeartOut(hearts[lifesCount - 1]);
            this.lifesCount--;
        }
    }

    public void addOneLife() {
        if (lifesCount < 3) {
            animateHeartIn(hearts[lifesCount]);
            this.lifesCount++;
        }
    }

    private void animateHeartIn(final ImageView heart) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.life_get);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                heart.setVisibility(VISIBLE);
                heart.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        heart.startAnimation(fadeInAnimation);
    }

    private void animateHeartOut(final ImageView heart) {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.life_loss);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                heart.setVisibility(INVISIBLE);
                heart.clearAnimation();
                heart.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_heart_icon_border));
                heart.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shadow_heart_appear));
                heart.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        heart.startAnimation(fadeOutAnimation);
    }

    public int getLifesCount() {
        return this.lifesCount;
    }
}