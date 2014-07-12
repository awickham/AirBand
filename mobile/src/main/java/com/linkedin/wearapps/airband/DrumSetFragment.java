package com.linkedin.wearapps.airband;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

public class DrumSetFragment extends Fragment implements MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {
    private static final String TAG = "DrumSetFragment";

    private GoogleApiClient mGoogleApiClient;

    // Sound stuff
    SoundPool mSoundPool;
    private static int mKickId;
    private static int mSnareId;
    private static boolean mSoundsLoaded = false;
    private static final float VOLUME = 1.0f;
    private static final int MAX_STREAMS = 10;
    private static final int PRIORITY = 1;
    private static final int NUM_LOOPS = 1;
    private static final float RATE = 1f;

    // Sensor stuff
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mEventThrottleTimer = 0;
    private static final float SENSOR_THRESHOLD = -10.0f;
    private static final int THROTTLE_LIMIT = 30;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        // Load the sounds
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundsLoaded = true;
            }
        });
        mKickId = mSoundPool.load(getActivity(), R.raw.kick, PRIORITY);
        mSnareId = mSoundPool.load(getActivity(), R.raw.snare, PRIORITY);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

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
        Wearable.DataApi.deleteDataItems(mGoogleApiClient,
                PutDataMapRequest.create(Constants.PATH_INSTRUMENT).getUri()).setResultCallback(
                new ResultCallback<DataApi.DeleteDataItemsResult>() {
                    @Override
                    public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
                        if (!deleteDataItemsResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to delete data items.");
                        }
                        mGoogleApiClient.disconnect();
                    }
                }
        );
        super.onStop();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "Received message, playing drum.");
        if (Constants.PATH_PLAY_SOUND.equals(messageEvent.getPath()) && mSoundsLoaded) {
            mSoundPool.play(mSnareId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View drumSet = inflater.inflate(R.layout.fragment_drum_set, container, false);

        // Pulsing yellow circle behind drum set.
        View yellowBackground = drumSet.findViewById(R.id.yellow_background);
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        yellowBackground.startAnimation(alpha);

        return drumSet;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Failed to connect to Google Play Services");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        // Set data item to alert watch that drum is active.
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.PATH_INSTRUMENT);
        putDataMapRequest.getDataMap().putByte(Constants.CURRENT_INSTRUMENT,
                Constants.INSTRUMENT_DRUM);
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to set drum data item.");
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Successfully registered for the sensor updates");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[1] <= SENSOR_THRESHOLD && mEventThrottleTimer <= 0) {
            if (mSoundsLoaded) {
                playKickSound();
            }
            mEventThrottleTimer = THROTTLE_LIMIT;
        }
        mEventThrottleTimer--;
    }

    private void playKickSound() {
        // Play the sound.
        mSoundPool.play(mKickId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
        // Show the pulse animation.
        final View pulseBackground = getActivity().findViewById(R.id.pulse);
        pulseBackground.setVisibility(View.VISIBLE);
        pulseBackground.startAnimation(getPulseBackgroundAnimation(pulseBackground));

        getActivity().findViewById(R.id.drum_set).startAnimation(getShakeDrumSetAnimation());
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
