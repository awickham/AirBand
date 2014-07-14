package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuitarFragment extends Fragment implements MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GuitarFragment";

    private Set<Integer> mButtonsPressed;
    private List<Integer> mRawGuitarSoundIds;
    private List<Integer> mRawElectricGuitarSoundIds;
    private static int mCurrentGuitarIndex = 0;
    private static final int NUM_NOTES = 8;

    // SoundPool stuff
    SoundPool mSoundPool;
    private static boolean mSoundsLoaded = false;
    private static final float VOLUME = 1.0f;
    private static final int MAX_STREAMS = 10;
    private static final int PRIORITY = 1;
    private static final int NUM_LOOPS = 1;
    private static final float RATE = 1f;

    private GoogleApiClient mGoogleApiClient;

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
        mRawGuitarSoundIds = new ArrayList<Integer>(NUM_NOTES);
        final Activity activity = getActivity();
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.g_third_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.a_third_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.b_second_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.c_second_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.d_second_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.e_first_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.f_first_string, PRIORITY));
        mRawGuitarSoundIds.add(mSoundPool.load(activity, R.raw.g_first_string, PRIORITY));
        mRawElectricGuitarSoundIds = new ArrayList<Integer>(NUM_NOTES);
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_g_low, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_a, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_b, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_c, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_d, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_e, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_f, PRIORITY));
        mRawElectricGuitarSoundIds.add(mSoundPool.load(activity, R.raw.electric_g_high, PRIORITY));

        mButtonsPressed = new HashSet<Integer>(4);

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
        if (Constants.PATH_PLAY_SOUND.equals(messageEvent.getPath()) && mSoundsLoaded) {
            Log.i(TAG, "Received message, playing guitar.");
            playSound();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View guitar = inflater.inflate(R.layout.fragment_guitar, container, false);

        // Pulsing red circle behind guitar.
        View redBackground = guitar.findViewById(R.id.red_background);
        AlphaAnimation alpha = new AlphaAnimation(0.5f, 0.2f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        redBackground.startAnimation(alpha);

        LinearLayout buttonsContainer = (LinearLayout) guitar.findViewById(R.id.buttons_container);
        for (int i = 0; i < buttonsContainer.getChildCount(); i++) {
            final Integer j = i;
            buttonsContainer.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        v.setAlpha(1.0f);
                        mButtonsPressed.add(j);
                        return true;
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        v.setAlpha(0.6f);
                        mButtonsPressed.remove(j);
                        return true;
                    }
                    return false;
                }
            });
        }

        return guitar;
    }

    private void playSound() {
        int rawGuitarSoundIndex =
                  (mButtonsPressed.contains(3) ? 1 : 0)
                | (mButtonsPressed.contains(2) ? 2 : 0)
                | (mButtonsPressed.contains(1) ? 4 : 0)
                | (mButtonsPressed.contains(0) ? 8 : 0);
        Integer soundId;
        if (rawGuitarSoundIndex < NUM_NOTES) {
            soundId = mRawGuitarSoundIds.get(rawGuitarSoundIndex);
        } else {
            // Electric guitar enabled - RELEASE THE BEAST!
            soundId = mRawElectricGuitarSoundIds.get(rawGuitarSoundIndex - NUM_NOTES);
        }
        if (soundId != null) {
            // Should never be null, but guess the check doesn't hurt.
            mSoundPool.play(soundId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
        }

        // If this is a different sound, set data item to tell watch what color to use.
        if (mCurrentGuitarIndex != rawGuitarSoundIndex) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.PATH_BACKGROUND);
            putDataMapRequest.getDataMap().putInt(Constants.CURRENT_BACKGROUND, rawGuitarSoundIndex);
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to set guitar data item.");
                            } else {
                                Log.i(TAG, "Setting guitar data item.");
                            }
                        }
                    });
        }

        mCurrentGuitarIndex = rawGuitarSoundIndex;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Failed to connect to Google Play Services");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        // Set data item to alert watch that guitar is active.
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.PATH_INSTRUMENT);
        putDataMapRequest.getDataMap().putByte(Constants.CURRENT_INSTRUMENT,
                Constants.INSTRUMENT_GUITAR);
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to set guitar data item.");
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
