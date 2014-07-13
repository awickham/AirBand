package com.linkedin.wearapps.airband;



import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            replaceFragment(new DrumSetFragment());
        }
    }

    private class OnGuitarClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            replaceFragment(new GuitarFragment());
        }
    }

    private class OnMaracasClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            replaceFragment(new MaracasFragment());
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
