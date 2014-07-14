package com.linkedin.wearapps.airband;



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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


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

        LinearLayout instrumentOptions = (LinearLayout) inflater.inflate(
                R.layout.fragment_instrument_options, container, false);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Windswept MF.ttf");
        ((TextView) instrumentOptions.findViewById(R.id.choose_instrument)).setTypeface(tf);

        addInstrumentOption(R.drawable.instrument_drum_set, R.string.drum_set, instrumentOptions,
                R.color.yellow_transparent, new OnDrumSetClickedListener());
        addInstrumentOption(R.drawable.instrument_guitar, R.string.guitar, instrumentOptions,
                R.color.red_transparent, new OnGuitarClickedListener());
        addInstrumentOption(R.drawable.instrument_maracas, R.string.maracas, instrumentOptions,
                R.color.blue_transparent, new OnMaracasClickedListener());

        return instrumentOptions;
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
}
