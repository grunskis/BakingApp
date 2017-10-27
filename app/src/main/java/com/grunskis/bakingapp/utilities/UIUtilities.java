package com.grunskis.bakingapp.utilities;

import android.content.Context;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class UIUtilities {
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }
}
