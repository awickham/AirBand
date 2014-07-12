package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.linkedin.wearapps.airband.util.SystemUiHider;


/**
 * The main Activity, which is FullScreen unless the user explicitly taps the top of the screen to
 * show the status bar.
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

        Typeface tf = Typeface.createFromAsset(getAssets(), "Windswept MF.ttf");
        ((TextView) findViewById(R.id.title)).setTypeface(tf);

        final View anchorView = findViewById(R.id.anchor);
        SystemUiHider.getInstance(this, anchorView, 0).hide();
    }
}
