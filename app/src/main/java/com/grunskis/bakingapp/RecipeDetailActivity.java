package com.grunskis.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.grunskis.bakingapp.models.Recipe;
import com.grunskis.bakingapp.models.Step;
import com.grunskis.bakingapp.ui.RecipeDetailFragment;
import com.grunskis.bakingapp.ui.RecipeStepClickHandler;
import com.grunskis.bakingapp.ui.RecipeStepFragment;

import java.util.Arrays;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepClickHandler {

    private static final String EXTRA_RECIPE = "com.grunskis.bakingapp.EXTRA_RECIPE";

    private static final String ARGUMENT_RECIPE = "ARGUMENT_RECIPE";

    private static final String BUNDLE_STEP_INDEX = "BUNDLE_STEP_INDEX";

    private Recipe mRecipe;
    private boolean mIsTwoPage;
    private int mStepIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_RECIPE)) {
            mRecipe = intent.getParcelableExtra(EXTRA_RECIPE);

            setTitle(mRecipe.getName());

            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(ARGUMENT_RECIPE, mRecipe);
            recipeDetailFragment.setArguments(arguments);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_detail_container, recipeDetailFragment)
                    .commit();

            mStepIndex = 0;
            if (savedInstanceState != null) {
                mStepIndex = savedInstanceState.getInt(BUNDLE_STEP_INDEX, 0);
            }

            mIsTwoPage = findViewById(R.id.recipe_two_pane) != null;
            if (mIsTwoPage) {
                showStep(mRecipe.getSteps()[mStepIndex]);
            }
        }
    }

    @Override
    public void onClick(Step step) {
        Step[] steps = mRecipe.getSteps();
        mStepIndex = Arrays.asList(steps).indexOf(step);

        if (mIsTwoPage) {
            showStep(step);
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);

            intent.putExtra(RecipeStepActivity.EXTRA_STEPS, steps);
            intent.putExtra(RecipeStepActivity.EXTRA_STEP_INDEX, mStepIndex);
            intent.putExtra(RecipeStepActivity.EXTRA_RECIPE_NAME, mRecipe.getName());

            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STEP_INDEX, mStepIndex);
    }

    void showStep(Step step) {
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setStep(step);
        recipeStepFragment.setIsTwoPane(mIsTwoPage);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_step_container, recipeStepFragment)
                .commit();

        setTitle(mRecipe.getName() + " - " + step.getTitle());
    }
}
