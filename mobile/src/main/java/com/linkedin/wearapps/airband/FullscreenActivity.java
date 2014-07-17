package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkedin.wearapps.airband.util.SystemUiHider;


/**
 * The main Activity, which is FullScreen unless the user explicitly taps the top of the screen to
 * show the status bar. This page displays instrument options for the user to choose from.
 */
public class FullscreenActivity extends Activity {

    private Typeface mWindSweptTf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);

        mWindSweptTf = Typeface.createFromAsset(getAssets(), "Windswept MF.ttf");
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.4f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        final TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(mWindSweptTf);
        title.startAnimation(alpha);
        findViewById(R.id.wind_underline).startAnimation(alpha);

        // turn up the volume to max
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        final LinearLayout anchorView = (LinearLayout) findViewById(R.id.anchor);
        SystemUiHider.getInstance(this, anchorView, 0).hide();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InstrumentsOptionsFragment fragment = new InstrumentsOptionsFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
