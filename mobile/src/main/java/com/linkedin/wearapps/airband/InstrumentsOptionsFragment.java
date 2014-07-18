package com.linkedin.wearapps.airband;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkedin.wearapps.airband.util.AnimationUtils;
import com.linkedin.wearapps.airband.util.LayoutMeasurementsUtils;

import java.util.Random;


/**
 * A fragment that lists instruments, which each have their own fragment to replace this one.
 */
public class InstrumentsOptionsFragment extends Fragment {
    private static Fragment sDrumSetFragment;
    private static Fragment sGuitarFragment;
    private static Fragment sMaracasFragment;

    // Sound stuff
    SoundPool mSoundPool;
    private static int sDrumSetSelectedId;
    private static int sGuitarSelectedId;
    private static int sMaracasSelectedId;
    private static boolean mSoundsLoaded = false;
    private static final float VOLUME = 1.0f;
    private static final int MAX_STREAMS = 10;
    private static final int PRIORITY = 1;
    private static final int NUM_LOOPS = 1;
    private static final float RATE = 1f;

    private static final int NUM_FLOATING_MUSIC_NOTES = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sDrumSetFragment = new DrumSetFragment();
        sGuitarFragment = new GuitarFragment();
        sMaracasFragment = new MaracasFragment();
        // Load the sounds
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundsLoaded = true;
            }
        });
        sDrumSetSelectedId = mSoundPool.load(getActivity(), R.raw.instrument_selected_drum_kit,
                PRIORITY);
        sGuitarSelectedId = mSoundPool.load(getActivity(), R.raw.instrument_selected_guitar,
                PRIORITY);
        sMaracasSelectedId = mSoundPool.load(getActivity(), R.raw.instrument_selected_maracas,
                PRIORITY);

        FrameLayout frame = (FrameLayout) inflater.inflate(
                R.layout.fragment_instrument_options, container, false);
        LinearLayout instrumentOptions = (LinearLayout) frame.findViewById(R.id.instrument_options);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Windswept MF.ttf");
        ((TextView) instrumentOptions.findViewById(R.id.choose_instrument)).setTypeface(tf);

        addInstrumentOption(R.drawable.instrument_drum_set, R.string.drum_set, instrumentOptions,
                R.color.yellow_transparent, new OnDrumSetClickedListener());
        addInstrumentOption(R.drawable.instrument_guitar, R.string.guitar, instrumentOptions,
                R.color.red_transparent, new OnGuitarClickedListener());
        addInstrumentOption(R.drawable.instrument_maracas, R.string.maracas, instrumentOptions,
                R.color.blue_transparent, new OnMaracasClickedListener());

        return frame;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        final Activity activity = getActivity();
        for (int i = 0; i < NUM_FLOATING_MUSIC_NOTES; i++) {
            addFloatingNotesAnimation((ViewGroup) activity.findViewById(R.id.frame),
                    i * AnimationUtils.FLOAT_DURATION / NUM_FLOATING_MUSIC_NOTES);
        }
    }

    private void addInstrumentOption(int iconRes, int nameRes, ViewGroup instrumentOptionsGroup,
                                     int colorRes, View.OnClickListener onClickListener) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View option = inflater.inflate(R.layout.instrument_option, instrumentOptionsGroup, false);
        ((ImageView) option.findViewById(R.id.instrument_icon)).setImageResource(iconRes);
        final TextView instrumentName = (TextView) option.findViewById(R.id.instrument_name);
        instrumentName.setText(nameRes);
        Typeface windSweptTf = Typeface.createFromAsset(getActivity().getAssets(),
                "Windswept MF.ttf");
        instrumentName.setTypeface(windSweptTf);
        option.setBackgroundColor(getResources().getColor(colorRes));
        option.setOnClickListener(onClickListener);
        instrumentOptionsGroup.addView(option);
    }

    private class OnDrumSetClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            replaceFragment(sDrumSetFragment);
            if (mSoundsLoaded) {
                mSoundPool.play(sDrumSetSelectedId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
            }
        }
    }

    private class OnGuitarClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            replaceFragment(sGuitarFragment);
            if (mSoundsLoaded) {
                mSoundPool.play(sGuitarSelectedId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
            }
        }
    }

    private class OnMaracasClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            replaceFragment(sMaracasFragment);
            if (mSoundsLoaded) {
                mSoundPool.play(sMaracasSelectedId, VOLUME, VOLUME, PRIORITY, NUM_LOOPS, RATE);
            }
        }
    }

    /**
     * Replace this InstrumentsOptionsFragment with another fragment (one of the instruments).
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in,
                R.anim.fragment_animation_fade_out, R.anim.fragment_animation_fade_in,
                R.anim.fragment_animation_fade_out);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addFloatingNotesAnimation(final ViewGroup frame, final long startOffset) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ImageView musicNote = (ImageView) inflater.inflate(R.layout.image_music_note, null);

        // Measurements used to determine placement of notes.
        final Activity activity = getActivity();
        final int screenWidth = LayoutMeasurementsUtils.getScreenWidth(activity);
        final int screenHeight = LayoutMeasurementsUtils.getScreenHeight(activity);
        final int titleHeight = LayoutMeasurementsUtils.getMeasuredHeightOfView(activity
                .findViewById(R.id.title));
        final int musicNoteWidth = LayoutMeasurementsUtils.getMeasuredWidthOfView(musicNote);
        final int musicNoteHeight = LayoutMeasurementsUtils.getMeasuredHeightOfView(musicNote);

        final Random rand = new Random();
        int x = rand.nextInt(screenWidth - musicNoteWidth);
        int startY = 0;
        int endY = startY - rand.nextInt(screenHeight - titleHeight);
        final ViewGroup floatingMusic = (ViewGroup) frame.findViewById(R.id.floating_music);
        final Animation floatAndFade = AnimationUtils.floatAndFadeAnimation(x, startY, endY,
                startOffset, Animation.INFINITE);
        floatingMusic.addView(musicNote);
        // Make music note start just below the screen.
        musicNote.setY(musicNote.getY() + musicNoteHeight);
        floatAndFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                floatingMusic.removeView(musicNote);
                addFloatingNotesAnimation(frame,
                        AnimationUtils.FLOAT_DURATION / NUM_FLOATING_MUSIC_NOTES);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        musicNote.startAnimation(floatAndFade);
    }
}
