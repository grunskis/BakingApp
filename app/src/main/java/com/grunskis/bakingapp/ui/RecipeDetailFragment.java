package com.grunskis.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grunskis.bakingapp.R;
import com.grunskis.bakingapp.models.Recipe;

public class RecipeDetailFragment extends Fragment {

    static final String TAG = RecipeDetailFragment.class.getSimpleName();

    private static final String ARGUMENT_RECIPE = "ARGUMENT_RECIPE";

    private RecipeStepClickHandler mClickHandler;

    public RecipeDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        // TODO: 25.11.17 make items look clickable & highlight the currently selected one if two panes
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_recipe_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        Bundle args = getArguments();
        Recipe recipe = args.getParcelable(ARGUMENT_RECIPE);

        RecipeDetailAdapter adapter = new RecipeDetailAdapter(getContext(),
                recipe.getIngredients(), recipe.getSteps());
        adapter.setClickHandler(mClickHandler);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mClickHandler = (RecipeStepClickHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RecipeStepClickHandler");
        }
    }
}
