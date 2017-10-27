package com.grunskis.bakingapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
    private int mId;
    private String mShortDescription;
    private String mDescription;
    private Uri mVideoUrl;
    private Uri mThumbnailUrl;

    public Step(int id, String shortDescription, String description, String videoUrl,
                String thumbnailUrl) {
        mId = id;
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoUrl = Uri.parse(videoUrl);
        mThumbnailUrl = Uri.parse(thumbnailUrl);
    }

    private Step(Parcel in) {
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoUrl = in.readParcelable(Uri.class.getClassLoader());
        mThumbnailUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeParcelable(mVideoUrl, 0);
        dest.writeParcelable(mThumbnailUrl, 0);
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        if (mId > 0) {
            return "" + mId + ". " + mShortDescription;
        } else {
            return mShortDescription;
        }
    }

    public Uri getVideoUrl() {
        return mVideoUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public Uri getThumbnailUrl() {
        return mThumbnailUrl;
    }
}
