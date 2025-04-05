package com.example.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.interfaces.OnCategoryScrollListener;
import com.example.project.model.Category;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    private OnCategoryScrollListener onCategoryScrollListener;
    private Context context;
    private List<Category> categoryList;

    public CategoryListAdapter(Context context, List<Category> categoryList, OnCategoryScrollListener onCategoryScrollListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onCategoryScrollListener = onCategoryScrollListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getName());

        // Set the food items in the nested RecyclerView
        FoodAdapter foodAdapter = new FoodAdapter(context, category.getFoods(),R.layout.item_food);
        holder.foodRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));  // 2 columns for food items
        holder.foodRecyclerView.setAdapter(foodAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView foodRecyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.category_name);
            foodRecyclerView = itemView.findViewById(R.id.food_gridview);
        }
    }
    // Gắn sự kiện cuộn vào RecyclerView danh sách món ăn
    public void attachScrollListenerToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Gọi callback để cập nhật RecyclerView category
                    if (onCategoryScrollListener != null && firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                        onCategoryScrollListener.onCategoryScrolled(firstVisibleItemPosition);
                    }
                }
            }
        });
    }
}
