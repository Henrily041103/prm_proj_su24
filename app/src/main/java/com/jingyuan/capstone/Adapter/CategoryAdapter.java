package com.jingyuan.capstone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<CategoryFDTO> categoryFDTOSList;
    private OnCategoryClickListener onCategoryClickListener;

    public CategoryAdapter(Context context, ArrayList<CategoryFDTO> categoryFDTOSList) {
        this.context = context;
        this.categoryFDTOSList = categoryFDTOSList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recview_category_item, parent, false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        CategoryFDTO category = categoryFDTOSList.get(position);
        holder.title.setText(category.getName());
        String thumbnailURL = category.getThumbnail();
        Glide.with(context).load(thumbnailURL).into(holder.thumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryFDTOSList.size();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryFDTO category);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
