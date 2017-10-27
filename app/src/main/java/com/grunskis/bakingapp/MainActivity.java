package com.grunskis.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import com.grunskis.bakingapp.models.Recipe;
import com.grunskis.bakingapp.ui.RecipeListAdapter;
import com.grunskis.bakingapp.utilities.NetworkUtilities;

public class MainActivity extends AppCompatActivity implements
        RecipeListAdapter.RecipeListOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BUNDLE_STATE = "BUNDLE_STATE";
    private static final String BUNDLE_RECIPES = "BUNDLE_RECIPES";

    private static final String EXTRA_RECIPE = "com.grunskis.bakingapp.EXTRA_RECIPE";

    private static final int RECIPE_LOADER_ID = 1;

    private Recipe[] mRecipes;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private View mErrorView;
    private RecipeListAdapter mRecipeListAdapter;
    private LoaderManager.LoaderCallbacks<Recipe[]> recipeLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Recipe[]>() {

                @Override
                public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mErrorView.setVisibility(View.INVISIBLE);

                    return new RecipeLoader(MainActivity.this);
                }

                @Override
                public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] data) {
                    if (data != null) {
                        mRecipeListAdapter.setRecipes(data);
                        mRecipes = data;

                        mProgressBar.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mErrorView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Recipe[]> loader) {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.pb_loading);
        mErrorView = findViewById(R.id.error_view);

        findViewById(R.id.b_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecipes();
            }
        });

        int spanCount = getSpanCount();
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);

        mRecyclerView = findViewById(R.id.rv_recipe_list);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipeListAdapter = new RecipeListAdapter(this, this);
        mRecyclerView.setAdapter(mRecipeListAdapter);

        Parcelable recyclerViewState = null;
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable(BUNDLE_STATE);
            mRecipes = (Recipe[]) savedInstanceState.getParcelableArray(BUNDLE_RECIPES);
        }

        if (mRecipes != null && recyclerViewState != null) {
            mRecipeListAdapter.setRecipes(mRecipes);
            layoutManager.onRestoreInstanceState(recyclerViewState);
        } else {
            loadRecipes();
        }
    }

    void loadRecipes() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(RECIPE_LOADER_ID, null, recipeLoaderCallbacks);
    }

    int getSpanCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float columns = displayMetrics.widthPixels / displayMetrics.density / 600;
        if (columns < 1) columns = 1;
        return (int) columns;
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE, recipe);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_STATE,
                mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArray(BUNDLE_RECIPES, mRecipes);
    }

    private static class RecipeLoader extends AsyncTaskLoader<Recipe[]> {
        private Recipe[] mRecipes;

        RecipeLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            if (mRecipes != null) {
                deliverResult(mRecipes);
                return;
            }

            if (takeContentChanged() || mRecipes == null) {
                forceLoad();
            }
        }

        @Override
        public Recipe[] loadInBackground() {
            return NetworkUtilities.getRecipes();
        }

        @Override
        public void deliverResult(Recipe[] recipes) {
            mRecipes = recipes;
            super.deliverResult(recipes);
        }
    }
}
