package com.example.project.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private int selectedPosition = -1;

    public CategoryAdapter(Context context,List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    //Tạo ViewHolder cho từng item
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    // Gán dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.txtCategory.setText(category.getName());
        Glide.with(context).load(category.getImage()).into(holder.imgCategory);

        holder.viewUnderline.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        holder.txtCategory.setTypeface(null, position == selectedPosition ? Typeface.BOLD : Typeface.NORMAL);
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position; // Cập nhật vị trí đã chọn
            notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size(); // Trả về số danh mục có trong danh sách
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtCategory;
        View viewUnderline;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            viewUnderline = itemView.findViewById(R.id.viewUnderline);
        }
    }
}
