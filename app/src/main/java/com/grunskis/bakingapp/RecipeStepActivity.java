package com.grunskis.bakingapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.grunskis.bakingapp.models.Step;
import com.grunskis.bakingapp.ui.RecipeStepFragment;

public class RecipeStepActivity extends AppCompatActivity {

    static final String TAG = RecipeStepActivity.class.getSimpleName();

    public static final String EXTRA_STEPS = "com.grunskis.bakingapp.EXTRA_STEPS";
    public static final String EXTRA_STEP_INDEX = "com.grunskis.bakingapp.EXTRA_STEP_INDEX";
    public static final String EXTRA_RECIPE_NAME = "com.grunskis.bakingapp.EXTRA_RECIPE_NAME";

    private static final String BUNDLE_CURRENT_STEP = "BUNDLE_CURRENT_STEP";

    private static final String TAG_RECIPE_STEP_FRAGMENT = "TAG_RECIPE_STEP_FRAGMENT";

    private Step[] mSteps;
    private int mCurrentStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(EXTRA_STEPS);
        mSteps = new Step[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            mSteps[i] = (Step) parcelables[i];
        }

        mCurrentStep = getIntent().getIntExtra(EXTRA_STEP_INDEX, 0);
        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(BUNDLE_CURRENT_STEP, 0);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeStepFragment recipeStepFragment = (RecipeStepFragment)
                fragmentManager.findFragmentByTag(TAG_RECIPE_STEP_FRAGMENT);

        if (recipeStepFragment == null) {
            showStep(mSteps[mCurrentStep]);
        }

        final Button previousStepButton = findViewById(R.id.b_previous_step);
        final Button nextStepButton = findViewById(R.id.b_next_step);
        if (nextStepButton != null && previousStepButton != null) {
            if (mCurrentStep > 0) {
                previousStepButton.setEnabled(true);
            }
            if (mCurrentStep < mSteps.length - 1) {
                nextStepButton.setEnabled(true);
            }
            previousStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentStep--;

                    showStep(mSteps[mCurrentStep]);

                    nextStepButton.setEnabled(true);
                    if (mCurrentStep <= 0) {
                        previousStepButton.setEnabled(false);
                    }
                }
            });
            nextStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentStep++;

                    showStep(mSteps[mCurrentStep]);

                    previousStepButton.setEnabled(true);
                    if (mCurrentStep >= mSteps.length - 1) {
                        nextStepButton.setEnabled(false);
                    }
                }
            });
        }

        updateTitle();
    }

    void showStep(Step step) {
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setStep(step);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_step_container, recipeStepFragment, TAG_RECIPE_STEP_FRAGMENT)
                .commit();

        updateTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_CURRENT_STEP, mCurrentStep);
    }

    private void updateTitle() {
        String recipeName = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        setTitle(recipeName + " - " + mSteps[mCurrentStep].getTitle());
    }
}
