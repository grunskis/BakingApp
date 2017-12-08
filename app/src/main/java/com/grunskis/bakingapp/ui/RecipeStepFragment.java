package com.grunskis.bakingapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.grunskis.bakingapp.R;
import com.grunskis.bakingapp.models.Step;
import com.grunskis.bakingapp.utilities.UIUtilities;

public class RecipeStepFragment extends Fragment {
    private static final String TAG = RecipeStepFragment.class.getSimpleName();

    private static final String BUNDLE_PLAYER_POSITION = "BUNDLE_PLAYER_POSITION";
    private static final String BUNDLER_PLAYER_READY = "BUNDLER_PLAYER_READY";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private Step mStep;
    private boolean mIsTwoPane;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private long mPlayerPosition;
    private boolean mPlayerPlayWhenReady;

    public RecipeStepFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        mPlayerView = rootView.findViewById(R.id.player_view);
        ImageView thumbnailImageView = rootView.findViewById(R.id.iv_step_thumbnail);

        Uri videoUrl = mStep.getVideoUrl();
        if (videoUrl.getScheme() != null && videoUrl.getScheme().startsWith("http")) {
            thumbnailImageView.setVisibility(View.GONE);
        } else {
            mPlayerView.setVisibility(View.GONE);

            Uri thumbnailUrl = mStep.getThumbnailUrl();
            //Uri thumbnailUrl = Uri.parse("https://www.chefbakers.com/userfiles/Dutch-Truffle38292.jpg");
            if (thumbnailUrl.getScheme() != null && thumbnailUrl.getScheme().startsWith("http")) {
                GlideApp.with(this)
                        .load(thumbnailUrl)
                        .fitCenter()
                        .into(thumbnailImageView);
            } else {
                thumbnailImageView.setVisibility(View.GONE);
            }
        }

        TextView instructionsView = rootView.findViewById(R.id.tv_step_instructions);
        instructionsView.setText(mStep.getDescription());

        if (!mIsTwoPane) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            if (UIUtilities.isLandscape(getContext())) {
                if (mPlayerView.getVisibility() == View.VISIBLE ||
                        thumbnailImageView.getVisibility() == View.VISIBLE) {
                    instructionsView.setVisibility(View.GONE);

                    if (actionBar != null) {
                        actionBar.hide();
                    }
                }
            } else {
                if (actionBar != null) {
                    actionBar.show();
                }
            }

            if (mPlayerView.getVisibility() == View.VISIBLE) {
                if (UIUtilities.isLandscape(getContext())) {
                    rootView.setBackgroundColor(getResources().getColor(android.R.color.black));
                } else {
                    rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
            }
        }

        rootView.post(new Runnable() {
            @Override
            public void run() {
                // videos have 16/9 aspect ratio
                double aspectRatio = (double) 16 / (double) 9;

                mPlayerView.getLayoutParams().height = (int)
                        (rootView.getMeasuredWidth() / aspectRatio);
            }
        });

        return rootView;
    }

    public void setStep(Step step) {
        mStep = step;
    }

    public void setIsTwoPane(boolean isTwoPane) {
        mIsTwoPane = isTwoPane;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(BUNDLE_PLAYER_POSITION, mPlayerPosition);
        outState.putBoolean(BUNDLER_PLAYER_READY, mPlayerPlayWhenReady);
    }

    private void initializePlayer(Uri mediaUri, long position, boolean playWhenReady) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);

            if (position > 0) {
                mExoPlayer.seekTo(position);
            }
            mExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            mPlayerPosition = 0;
            mPlayerPlayWhenReady = true;
        } else {
            mPlayerPosition = savedInstanceState.getLong(BUNDLE_PLAYER_POSITION, 0);
            mPlayerPlayWhenReady = savedInstanceState.getBoolean(BUNDLER_PLAYER_READY, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPlayerView.getVisibility() == View.VISIBLE) {
            initializePlayer(mStep.getVideoUrl(), mPlayerPosition, mPlayerPlayWhenReady);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mPlayerPlayWhenReady = mExoPlayer.getPlayWhenReady();

            releasePlayer();
        }
    }
}
