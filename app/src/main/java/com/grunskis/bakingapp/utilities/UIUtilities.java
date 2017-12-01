package com.grunskis.bakingapp.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import com.grunskis.bakingapp.R;
import com.grunskis.bakingapp.models.Ingredient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class UIUtilities {
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }

    private static String getQuantityName(Ingredient ingredient, Context context) {
        int quantity = (int) ingredient.getQuantity();
        boolean hasFractionalPart = (ingredient.getQuantity() % 1) != 0;
        Resources resources = context.getResources();

        switch (ingredient.getMeasureType()) {
            case CUP:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + context.getString(R.string.cups);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_cup, quantity, quantity);
                }

            case UNIT:
                return "" + quantity;

            case G:
                return resources.getQuantityString(R.plurals.measurements_g, quantity, quantity);

            case K:
                return resources.getQuantityString(R.plurals.measurements_k, quantity, quantity);

            case TSP:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + context.getString(R.string.teaspoons);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_tsp, quantity, quantity);
                }

            case TBLSP:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + context.getString(R.string.tablespoons);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_tbsp, quantity, quantity);
                }

            case OZ:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + context.getString(R.string.ounces);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_oz, quantity, quantity);
                }

            default:
                return "" + ingredient.getQuantity() + " " + ingredient.getMeasureType().toString();
        }
    }

    public static SpannableString formatIngredientArray(Ingredient[] ingredients, Context context) {
        return formatIngredientString(formatIngradients(context, ingredients));
    }

    public static String formatIngradients(Context context, Ingredient[] ingredients) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Ingredient i : ingredients) {
            stringBuilder.append("\u2022 ");
            stringBuilder.append(getQuantityName(i, context));
            stringBuilder.append(" ");
            stringBuilder.append(i.getName());
            stringBuilder.append("\n\n");
        }

        return stringBuilder.toString();
    }

    public static SpannableString formatIngredientString(String ingredients) {
        SpannableString spannableString = new SpannableString(ingredients);

        // set custom line spacing, learned from https://stackoverflow.com/a/42691246
        Matcher matcher = Pattern.compile("\n\n").matcher(ingredients);
        while (matcher.find()) {
            spannableString.setSpan(new AbsoluteSizeSpan(10, true),
                    matcher.start() + 1, matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;

    }
}
