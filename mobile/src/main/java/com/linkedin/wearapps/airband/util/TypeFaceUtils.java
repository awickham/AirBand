package com.linkedin.wearapps.airband.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Get TypeFaces used throughout the application.
 */
public class TypefaceUtils {
    private static Typeface sWindSweptTypeface;

    /**
     * Sets the {@link android.graphics.Typeface} that will be returned with
     * {@link #getWindsweptTypeface}.
     */
    public static void setWindsweptTypeface(Context context) {
        sWindSweptTypeface = Typeface.createFromAsset(context.getAssets(), "Windswept MF.ttf");
    }

    /**
     * Returns the {@link android.graphics.Typeface} that was previously set by
     * {@link #setWindsweptTypeface(android.content.Context)}.
     */
    public static Typeface getWindsweptTypeface() {
        return sWindSweptTypeface;
    }
}
