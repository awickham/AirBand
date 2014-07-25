package com.linkedin.wearapps.airband.util;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Get animations used throughout application.
 */
public class AnimationUtils {
    public static final long LONG_FLOAT_DURATION = 10000;
    public static final long SHORT_FLOAT_DURATION = 1000;

    public static AlphaAnimation fadeBackgroundAnimation() {
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        alpha.setFillAfter(true);
        return alpha;
    }

    public static AnimationSet floatAndFadeAnimation(int x, int startY, int endY, int repeatCount,
                                                     long startOffset, long floatDuration) {
        AnimationSet floatAndFade = new AnimationSet(false);
        TranslateAnimation floatAnim = new TranslateAnimation(x, x, startY, endY);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.setFillAfter(true);
        AlphaAnimation fadeAnim = new AlphaAnimation(1f, 0f);
        fadeAnim.setFillAfter(true);
        floatAndFade.addAnimation(floatAnim);
        floatAndFade.addAnimation(fadeAnim);
        floatAndFade.setDuration(floatDuration);
        floatAndFade.setStartOffset(startOffset);
        floatAndFade.setRepeatCount(repeatCount);
        return floatAndFade;
    }

    public static AnimationSet pulseBackgroundAnimation(final View pulseBackground) {
        AnimationSet pulse = new AnimationSet(false);
        ScaleAnimation scale = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(200);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 0.6f);
        alpha.setRepeatCount(1);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(100);
        pulse.addAnimation(scale);
        pulse.addAnimation(alpha);
        pulse.setFillAfter(true);
        pulse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                pulseBackground.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return pulse;
    }

    public static AnimationSet shakeInstrumentAnimation() {
        AnimationSet shake = new AnimationSet(false);
        RotateAnimation rotate1 = new RotateAnimation(0, 15, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate1.setDuration(100);
        RotateAnimation rotate2 = new RotateAnimation(15, -15, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate2.setDuration(200);
        RotateAnimation rotate3 = new RotateAnimation(-15, 0, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate3.setDuration(100);
        shake.addAnimation(rotate1);
        shake.addAnimation(rotate2);
        shake.addAnimation(rotate3);
        shake.setFillAfter(true);
        return shake;
    }
}
