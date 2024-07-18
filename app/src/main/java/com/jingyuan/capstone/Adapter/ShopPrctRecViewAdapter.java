package com.jingyuan.capstone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jingyuan.capstone.Controller.DetailActivity;
import com.jingyuan.capstone.Controller.StorePrctDetailsActivity;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class ShopPrctRecViewAdapter extends RecyclerView.Adapter<ShopPrctRecViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProductItem> productItemsList;
    public ShopPrctRecViewAdapter(Context context, ArrayList<ProductItem> productItemsList) {
        this.context = context;
        this.productItemsList = productItemsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recview_product_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(productItemsList.get(position).getName());
        String thumbnailURL = productItemsList.get(position).getThumbnail();
        Glide.with(context).load(thumbnailURL).placeholder(R.drawable.loading).into(holder.thumbnail);
        holder.price.setText(productItemsList.get(position).getPrice()+"");
        holder.name.setOnClickListener(v -> {
            Intent intent = new Intent(context, StorePrctDetailsActivity.class);
            intent.putExtra("doc", productItemsList.get(holder.getAdapterPosition()).getDoc());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productItemsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView thumbnail;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            thumbnail = itemView.findViewById(R.id.item_thumbnail);
        }
    }
}
