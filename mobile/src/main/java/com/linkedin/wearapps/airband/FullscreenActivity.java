package com.linkedin.wearapps.airband;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

        final View contentView = findViewById(R.id.fullscreen_content);
        SystemUiHider.getInstance(this, contentView, 0).hide();
    }
}
