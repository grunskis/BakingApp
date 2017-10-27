package com.grunskis.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;

public class Recipe implements Parcelable {
    private int mId;
    private String mName;
    private int mServings;
    private URL mImageUrl;
    private Ingredient[] mIngredients;
    private Step[] mSteps;

    public Recipe(int id, String name, int servings, String imageUrl,
                  Ingredient[] ingredients, Step[] steps) {
        mId = id;
        mName = name;
        mServings = servings;
        mIngredients = ingredients;
        mSteps = steps;

        try {
            mImageUrl = new URL(imageUrl);
        } catch (MalformedURLException e) {
            mImageUrl = null;
        }
    }

    private Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mIngredients = in.createTypedArray(Ingredient.CREATOR);
        mSteps = in.createTypedArray(Step.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getServings() {
        return mServings;
    }

    public Ingredient[] getIngredients() {
        return mIngredients;
    }

    public Step[] getSteps() {
        return mSteps;
    }

    public URL getImageUrl() {
        return mImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeTypedArray(mIngredients, 0);
        dest.writeTypedArray(mSteps, 0);
    }
}
