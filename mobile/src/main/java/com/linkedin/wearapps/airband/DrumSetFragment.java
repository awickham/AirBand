package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class DrumSetFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View drumSet = inflater.inflate(R.layout.fragment_drum_set, container, false);
        drumSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: do this when we receive message from wearable instead of on click.
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
                final View pulseBackground = v.findViewById(R.id.pulse);
                pulseBackground.setVisibility(View.VISIBLE);
                pulseBackground.startAnimation(pulse);
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
            }
        });

        View yellowBackground = drumSet.findViewById(R.id.yellow_background);
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        yellowBackground.setAnimation(alpha);

        return drumSet;
    }
}
