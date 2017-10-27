package com.grunskis.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    public enum MEASURE_TYPE {
        CUP, TBLSP, TSP, K, G, OZ, UNIT
    }

    private double mQuantity;
    private MEASURE_TYPE mMeasureType;
    private String mName;

    private Ingredient(Parcel in) {
        mQuantity = in.readDouble();
        mMeasureType = MEASURE_TYPE.valueOf(in.readString());
        mName = in.readString();
    }

    static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mQuantity);
        dest.writeString(mMeasureType.name());
        dest.writeString(mName);
    }

    public Ingredient(String name, double quantity, MEASURE_TYPE measureType) {
        mName = name;
        mQuantity = quantity;
        mMeasureType = measureType;
    }

    public String getName() {
        return mName;
    }

    public MEASURE_TYPE getMeasureType() {
        return mMeasureType;
    }

    public double getQuantity() {
        return mQuantity;
    }
}
