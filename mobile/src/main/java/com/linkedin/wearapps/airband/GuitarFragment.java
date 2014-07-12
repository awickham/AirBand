package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

public class GuitarFragment extends Fragment implements MessageApi.MessageListener {
    MediaPlayer mKick;
    MediaPlayer mSnare;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mKick = MediaPlayer.create(getActivity(), R.raw.kick);
        mKick.setVolume(1.0f, 1.0f);
        mSnare = MediaPlayer.create(getActivity(), R.raw.snare);
        mSnare.setVolume(1.0f, 1.0f);
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

        return guitar;
    }
}
