package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

public class MyActivity  extends Activity implements SensorEventListener {

    private String TAG = "MyActivity";

    private ImageView mImageView;
    private FrameLayout mFrameLayout;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView mTextView;
    private Queue<Float> sensorQueue;
    private float vel;
    private boolean strum = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"OHLSIEFLSIEJFLISJEFLISJFLISEJFLISEJFLISJEF !!!!!!!!! !!!!!!!!!!!!!!");

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
                mTextView = (TextView) stub.findViewById(R.id.textView);
                mFrameLayout.setBackgroundColor(Color.RED);
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        });
    }

    @Override
    protected void onResume() {
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mTextView != null) {

            while (sensorQueue.size() > 5)
                sensorQueue.remove();

            int count = 0;

            boolean ok = true;
            for (Float f : sensorQueue) {
                if (Math.abs(f) <= 5.0f)
                    count++;
            }

            sensorQueue.add(event.values[1]);

            if (Math.abs(event.values[1]) <= 5.0f && count <= 0) {
                lum = 1.0f;
            }

            lum -= 0.05f;
            lum = Math.max(0.0f,lum);

            mFrameLayout.setBackgroundColor(Color.rgb((int)(lum*255), 0, 0));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}