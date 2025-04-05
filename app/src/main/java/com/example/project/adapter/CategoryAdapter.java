package com.example.project.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.interfaces.OnItemClickListener;
import com.example.project.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    public int selectedPosition = -1;

    private AdapterView.OnItemClickListener listener;


    public CategoryAdapter(Context context,List<Category> categoryList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onItemClickListener = onItemClickListener;
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
        Typeface normalFont = ResourcesCompat.getFont(context, R.font.roboto);
        Typeface boldFont = ResourcesCompat.getFont(context, R.font.fonts_com_merge_bold);
        holder.viewUnderline.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        holder.txtCategory.setTypeface(position == selectedPosition ? boldFont : normalFont);
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    int oldPosition = selectedPosition;
                    selectedPosition = position;

                    onItemClickListener.onItemClick(view, position);

                    // Chỉ cập nhật lại các mục cần thiết thay vì toàn bộ danh sách
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);
                }
            }
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
