package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /** Request code for launching the Intent to resolve Google Play services errors. */
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private String TAG = "MyActivity";

    private ImageView mImageView;
    private FrameLayout mFrameLayout;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Queue<Float> sensorQueue;
    private float vel;
    private boolean strum = true;
    private boolean mIsDrum = true;
    private int mColor = Color.BLACK;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private Node mPairedNode;
    private boolean mRetrievedPairedNode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorQueue = new ArrayDeque<Float>();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mImageView = (ImageView) stub.findViewById(R.id.musical_note_view);
                mFrameLayout = (FrameLayout) stub.findViewById(R.id.frame_layout);
                mFrameLayout.setBackgroundColor(Color.BLACK);
            }
        });

        byte instrument = getIntent().getByteExtra(Constants.CURRENT_INSTRUMENT,
                Constants.INSTRUMENT_DRUM);
        mIsDrum = instrument == Constants.INSTRUMENT_DRUM;
        mColor = getIntent().getIntExtra(Constants.CURRENT_BACKGROUND, Color.BLACK);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        mPairedNode = nodes.getNodes().get(0);
                        mRetrievedPairedNode = true;
                    }
                });

        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // empy message YAY!!!!
                if (mRetrievedPairedNode) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mPairedNode.getId(),
                            "", new byte[0]);
                    Log.i(TAG, "Sent message to phone.");
                } else {
                    Log.w(TAG, "Failed to send play sound message because haven't found paired node");
                }
            }
        }, 0, 300);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        //
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_FASTEST)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Successfully registered for the sensor updates");
            }
        }
    }

    private float lum;

    private float sign(float val) {
        return (val < 0.0f ? -1.0f : 1.0f);
    }

    private int eventThrottleTimer = 0;
    private final int THROTTLE_LIMIT = 30;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mFrameLayout != null) {
            if (mIsDrum) {
                if (event.values[1] <= -20.0f && eventThrottleTimer <= 0) {
                    lum = 1.0f;
                    sendPlaySoundMessage();
                    eventThrottleTimer = THROTTLE_LIMIT;
                }
                eventThrottleTimer--;
                lum -= 0.04f;
                lum = Math.max(0.0f, lum);
                mFrameLayout.setBackgroundColor(Color.rgb((int) (lum * 255), (int) (lum * 255), (int) (lum * 255)));
            } else {

                while (sensorQueue.size() > 5)
                    sensorQueue.remove();

                int count = 0;

                boolean ok = true;
                for (Float f : sensorQueue) {
                    if (Math.abs(f) <= 5.0f)
                        count++;
                }

                sensorQueue.add(event.values[1]);

                if (Math.abs(event.values[1]) <= 5.0f && count <= 0 && eventThrottleTimer <= 0) {
                    lum = 1.0f;
                    sendPlaySoundMessage();
                    eventThrottleTimer = THROTTLE_LIMIT;
                }
                eventThrottleTimer--;
                lum -= 0.8f;
                lum = Math.max(0.0f, lum);
                mFrameLayout.setBackgroundColor(Color.rgb(0,255,0));
            }
        }
    }

    private void sendPlaySoundMessage() {
        if (mRetrievedPairedNode) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPairedNode.getId(),
                    Constants.PATH_PLAY_SOUND, new byte[0]);
            Log.i(TAG, "Sent message to phone.");
        } else {
            Log.w(TAG, "Failed to send play sound message because haven't found paired node");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Disconnected from Google Api Service");
        }
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        mResolvingError = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}