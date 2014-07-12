package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
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
        title.setAnimation(alpha);
        ((TextView) findViewById(R.id.choose_instrument)).setTypeface(mWindSweptTf);

        final LinearLayout anchorView = (LinearLayout) findViewById(R.id.anchor);
        SystemUiHider.getInstance(this, anchorView, 0).hide();

        LinearLayout instrumentOptions = (LinearLayout) findViewById(R.id.instrument_options);
        addInstrumentOption(R.drawable.instrument_drum_set, R.string.drum_set, instrumentOptions,
                R.color.yellow_transparent);
        addInstrumentOption(R.drawable.instrument_guitar, R.string.guitar, instrumentOptions,
                R.color.red_transparent);
    }

    private void addInstrumentOption(int iconRes, int nameRes, ViewGroup instrumentOptionsGroup,
                                     int colorRes) {
        final LayoutInflater inflater = getLayoutInflater();
        View option = inflater.inflate(R.layout.instrument_option, instrumentOptionsGroup, false);
        ((ImageView) option.findViewById(R.id.instrument_icon)).setImageResource(iconRes);
        final TextView instrumentName = (TextView) option.findViewById(R.id.instrument_name);
        instrumentName.setText(nameRes);
        instrumentName.setTypeface(mWindSweptTf);
        option.setBackgroundColor(getResources().getColor(colorRes));
        instrumentOptionsGroup.addView(option);
    }
}
