package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.linkedin.wearapps.airband.util.SystemUiHider;
import com.linkedin.wearapps.airband.util.TypefaceUtils;


/**
 * The main Activity, which is FullScreen unless the user explicitly taps the top of the screen to
 * show the status bar. This page displays instrument options for the user to choose from.
 */
public class FullscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);

        TypefaceUtils.setWindsweptTypeface(this);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.4f);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        alpha.setDuration(1200);
        final TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(TypefaceUtils.getWindsweptTypeface());
        title.startAnimation(alpha);
        findViewById(R.id.wind_underline).startAnimation(alpha);

        // Set up music volume slider control.
        setUpMusicVolumeSeekBar();

        final LinearLayout anchorView = (LinearLayout) findViewById(R.id.anchor);
        SystemUiHider.getInstance(this, anchorView, 0).hide();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InstrumentsOptionsFragment fragment = new InstrumentsOptionsFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void setUpMusicVolumeSeekBar() {
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final SeekBar musicVolume = (SeekBar) findViewById(R.id.music_volume);
        musicVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        musicVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        musicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
