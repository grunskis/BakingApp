package com.grunskis.bakingapp.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grunskis.bakingapp.R;
import com.grunskis.bakingapp.models.Ingredient;
import com.grunskis.bakingapp.models.Step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RecipeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_INGREDIENTS = 0;
    private final int VIEW_TYPE_STEP = 1;

    private Context mContext;
    private Ingredient[] mIngredients;
    private Step[] mSteps;

    private RecipeStepClickHandler mClickHandler;

    RecipeDetailAdapter(Context context, Ingredient[] ingredients, Step[] steps) {
        mContext = context;
        mIngredients = ingredients;
        mSteps = steps;
    }

    void setClickHandler(RecipeStepClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS:
                view = inflater.inflate(R.layout.item_ingredients, parent, false);
                viewHolder = new IngredientsViewHolder(view);
                break;
            case VIEW_TYPE_STEP:
            default:
                view = inflater.inflate(R.layout.item_step, parent, false);
                viewHolder = new StepViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_INGREDIENTS:
                IngredientsViewHolder ingredientsViewHolder = (IngredientsViewHolder)holder;
                ingredientsViewHolder.setContent(formatIngredientArray(mIngredients));
                break;
            case VIEW_TYPE_STEP:
            default:
                StepViewHolder stepViewHolder = (StepViewHolder)holder;
                stepViewHolder.setTitle(mSteps[position-1].getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_INGREDIENTS;
        } else {
            return VIEW_TYPE_STEP;
        }
    }

    @Override
    public int getItemCount() {
        return 1 + /* 1st item is ingredient list */ mSteps.length;
    }

    private class IngredientsViewHolder extends RecyclerView.ViewHolder {

        final TextView mName;

        IngredientsViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tv_name);
        }

        void setContent(SpannableString content) {
            mName.setText(content);
        }
    }

    private class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTitle;

        StepViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_step_title);

            itemView.setOnClickListener(this);
        }

        public void setTitle(String title) {
            mTitle.setText(title);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mSteps[getAdapterPosition()-1]);
        }
    }

    private String getQuantityName(Ingredient ingredient) {
        int quantity = (int) ingredient.getQuantity();
        boolean hasFractionalPart = (ingredient.getQuantity() % 1) != 0;
        Resources resources = mContext.getResources();

        switch (ingredient.getMeasureType()) {
            case CUP:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + mContext.getString(R.string.cups);
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
                    return "" + ingredient.getQuantity() + " " + mContext.getString(R.string.teaspoons);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_tsp, quantity, quantity);
                }

            case TBLSP:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + mContext.getString(R.string.tablespoons);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_tbsp, quantity, quantity);
                }

            case OZ:
                if (hasFractionalPart) {
                    return "" + ingredient.getQuantity() + " " + mContext.getString(R.string.ounces);
                } else {
                    return resources.getQuantityString(R.plurals.measurements_oz, quantity, quantity);
                }

            default:
                return "" + ingredient.getQuantity() + " " + ingredient.getMeasureType().toString();
        }
    }

    private SpannableString formatIngredientArray(Ingredient[] ingredients) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Ingredient i : ingredients) {
            stringBuilder.append("\u2022 ");
            stringBuilder.append(getQuantityName(i));
            stringBuilder.append(" ");
            stringBuilder.append(i.getName());
            stringBuilder.append("\n\n");
        }

        String ingredientString = stringBuilder.toString();
        SpannableString spannableString = new SpannableString(ingredientString);

        // set custom line spacing, learned from https://stackoverflow.com/a/42691246
        Matcher matcher = Pattern.compile("\n\n").matcher(ingredientString);
        while (matcher.find()) {
            spannableString.setSpan(new AbsoluteSizeSpan(10, true),
                    matcher.start() + 1, matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }
}
