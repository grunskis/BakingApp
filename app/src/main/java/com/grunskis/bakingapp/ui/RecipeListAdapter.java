package com.grunskis.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grunskis.bakingapp.R;
import com.grunskis.bakingapp.models.Recipe;


public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private Recipe[] mRecipes;

    private Context mContext;
    private RecipeListOnClickHandler mClickHandler;

    public RecipeListAdapter(Context context, RecipeListOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeListAdapter.ViewHolder holder, int position) {
        // Uri url = Uri.parse("https://www.chefbakers.com/userfiles/Dutch-Truffle38292.jpg");
        GlideApp.with(mContext)
                .load(mRecipes[position].getImageUrl())
                .error(R.drawable.ic_cake_black_24dp)
                .fitCenter()
                .into(holder.mRecipeImage);

        int servings = mRecipes[position].getServings();
        String numberOfServings = mContext.getResources().getQuantityString(R.plurals.servings,
                servings, servings);
        holder.mNumberOfServingsTextView.setText(numberOfServings);
        holder.mRecipeNameTextView.setText(mRecipes[position].getName());
    }

    public void setRecipes(Recipe[] recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        } else {
            return mRecipes.length;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mRecipeImage;
        final TextView mNumberOfServingsTextView;
        final TextView mRecipeNameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            mRecipeImage = itemView.findViewById(R.id.iv_recipe_image);
            mNumberOfServingsTextView = itemView.findViewById(R.id.tv_number_of_servings);
            mRecipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mRecipes[getAdapterPosition()]);
        }
    }

    public interface RecipeListOnClickHandler {
        void onClick(Recipe recipe);
    }
}
