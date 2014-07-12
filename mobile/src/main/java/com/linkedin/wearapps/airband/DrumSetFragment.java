package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class DrumSetFragment extends Fragment implements MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DrumSetFragment";

    MediaPlayer mKick;
    MediaPlayer mSnare;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        mKick = MediaPlayer.create(getActivity(), R.raw.kick);
        mKick.setVolume(1.0f, 1.0f);
        mSnare = MediaPlayer.create(getActivity(), R.raw.snare);
        mSnare.setVolume(1.0f, 1.0f);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        super.onStop();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "Received message, playing drum.");
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
        View drumSet = inflater.inflate(R.layout.fragment_drum_set, container, false);
        drumSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: do this when phone shakes instead of on click.
                // Play the sound.
                if (mKick.isPlaying()) {
                    mKick.reset();
                    mKick = MediaPlayer.create(getActivity(), R.raw.kick);
                    mKick.setVolume(1.0f, 1.0f);
                }
                mKick.start();
                // Show the pulse animation.
                final View pulseBackground = v.findViewById(R.id.pulse);
                pulseBackground.setVisibility(View.VISIBLE);
                pulseBackground.startAnimation(getPulseBackgroundAnimation(pulseBackground));

                v.findViewById(R.id.drum_set).startAnimation(getShakeDrumSetAnimation());
            }
        });

        // Pulsing yellow circle behind drum set.
        View yellowBackground = drumSet.findViewById(R.id.yellow_background);
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        yellowBackground.startAnimation(alpha);

        return drumSet;
    }

    private AnimationSet getPulseBackgroundAnimation(final View pulseBackground) {
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

    private AnimationSet getShakeDrumSetAnimation() {
        AnimationSet shake = new AnimationSet(false);
        RotateAnimation rotate1 = new RotateAnimation(15, 15, RotateAnimation.RELATIVE_TO_SELF,
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

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Failed to connect to Google Play Services");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
