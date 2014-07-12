package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuitarFragment extends Fragment implements MessageApi.MessageListener {
    private Set<Integer> mButtonsPressed;
    private List<Integer> mRawGuitarSounds;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mRawGuitarSounds = new ArrayList<Integer>(16);
        mRawGuitarSounds.add(R.raw.g_third_string);
        mRawGuitarSounds.add(R.raw.a_third_string);
        mRawGuitarSounds.add(R.raw.b_second_string);
        mRawGuitarSounds.add(R.raw.c_second_string);
        mRawGuitarSounds.add(R.raw.d_second_string);
        mRawGuitarSounds.add(R.raw.e_first_string);
        mRawGuitarSounds.add(R.raw.f_first_string);
        mRawGuitarSounds.add(R.raw.g_first_string);

        mButtonsPressed = new HashSet<Integer>(4);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Constants.PATH_PLAY_SOUND.equals(messageEvent.getPath())) {
            playSound();
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
                        // TODO play when message is received instead of here.
                        playSound();
                        return true;
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        v.setAlpha(0.6f);
                        mButtonsPressed.remove(j);
                        // TODO play when message is received instead of here.
                        playSound();
                        return true;
                    }
                    return false;
                }
            });
        }

        return guitar;
    }

    private void playSound() {
        int rawGuitarSoundIndex = 0 | (mButtonsPressed.contains(3) ? 1 : 0)
                | (mButtonsPressed.contains(2) ? 2 : 0)
                | (mButtonsPressed.contains(1) ? 4 : 0)
                | (mButtonsPressed.contains(0) ? 8 : 0);
        if (rawGuitarSoundIndex >= mRawGuitarSounds.size()) {
            // This sound is not available. Get out of here silently!
            return;
        }
        Integer rawGuitarSound = mRawGuitarSounds.get(rawGuitarSoundIndex);
        if (rawGuitarSound != null) {
            MediaPlayer sound = MediaPlayer.create(getActivity(), rawGuitarSound);
            sound.setVolume(1.0f, 1.0f);
            sound.start();
        }
    }
}
