package com.grunskis.bakingapp.utilities;

import android.support.annotation.Nullable;

import com.grunskis.bakingapp.models.Ingredient;
import com.grunskis.bakingapp.models.Recipe;
import com.grunskis.bakingapp.models.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtilities {
    final private static String DATA_URL = "https://go.udacity.com/android-baking-app-json";

    public static Recipe[] getRecipes() {
        Recipe[] recipes = null;

        try {
            URL url = new URL(DATA_URL);
            String json = getResponseFromHttpUrl(url);
            if (json == null) {
                return null;
            }
            JSONArray recipeArray = new JSONArray(json);

            recipes = new Recipe[recipeArray.length()];
            for (int i = 0; i < recipeArray.length(); i++) {
                JSONObject recipeObject = recipeArray.getJSONObject(i);

                int id = recipeObject.getInt("id");
                String name = recipeObject.getString("name");
                int servings = recipeObject.getInt("servings");
                String imageUrl = recipeObject.getString("image");

                JSONArray ingredientsArray = recipeObject.getJSONArray("ingredients");
                Ingredient[] ingredients = new Ingredient[ingredientsArray.length()];
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(j);

                    ingredients[j] = new Ingredient(ingredientObject.getString("ingredient"),
                            ingredientObject.getDouble("quantity"),
                            Ingredient.MEASURE_TYPE.valueOf(
                                    ingredientObject.getString("measure")));
                }

                JSONArray stepsArray = recipeObject.getJSONArray("steps");
                Step[] steps = new Step[stepsArray.length()];
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObject = stepsArray.getJSONObject(j);

                    steps[j] = new Step(stepObject.getInt("id"),
                            stepObject.getString("shortDescription"),
                            stepObject.getString("description"),
                            stepObject.getString("videoURL"),
                            stepObject.getString("thumbnailURL"));
                }

                recipes[i] = new Recipe(id, name, servings, imageUrl, ingredients, steps);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    @Nullable
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
