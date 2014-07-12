package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import java.util.HashSet;
import java.util.Set;

public class GuitarFragment extends Fragment implements MessageApi.MessageListener {
    private Set<Integer> mButtonsPressed;
    private MediaPlayer mKick;
    private MediaPlayer mSnare;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mKick = MediaPlayer.create(getActivity(), R.raw.kick);
        mKick.setVolume(1.0f, 1.0f);
        mSnare = MediaPlayer.create(getActivity(), R.raw.snare);
        mSnare.setVolume(1.0f, 1.0f);
        mButtonsPressed = new HashSet<Integer>(4);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Constants.PATH_PLAY_SOUND.equals(messageEvent.getPath())) {
            if (mSnare.isPlaying()) {
                mSnare.reset();
                mSnare = MediaPlayer.create(getActivity(), R.raw.snare);
                mSnare.setVolume(1.0f, 1.0f);
            }
            mSnare.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View guitar = inflater.inflate(R.layout.fragment_guitar, container, false);

        // Pulsing yellow circle behind drum set.
        View redBackground = guitar.findViewById(R.id.red_background);
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        redBackground.startAnimation(alpha);

        LinearLayout buttonsContainer = (LinearLayout) guitar.findViewById(R.id.buttons_container);
        for (int i = 0; i < buttonsContainer.getChildCount(); i++) {
            final Integer j = new Integer(i);
            buttonsContainer.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        v.setAlpha(1.0f);
                        mButtonsPressed.add(j);
                        Log.d("GuitarFragment", "Buttons pressed: " + mButtonsPressed);
                        return true;
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        v.setAlpha(0.6f);
                        mButtonsPressed.remove(j);
                        Log.d("GuitarFragment", "Buttons pressed: " + mButtonsPressed);
                        return true;
                    }
                    return false;
                }
            });
        }

        return guitar;
    }
}
