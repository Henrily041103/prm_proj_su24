package com.jingyuan.capstone.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jingyuan.capstone.Controller.CartActivity;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<CartItem> cartItemsList;
    ImageButton clearCartBtn;
    OnCartItemUpdateListener onCartItemUpdateListener;

    public CartItemAdapter(Context context, ArrayList<CartItem> cartItemsList, ImageButton clearCartBtn, OnCartItemUpdateListener onCartItemUpdateListener) {
        this.context = context;
        this.cartItemsList = cartItemsList;
        this.clearCartBtn = clearCartBtn;
        this.onCartItemUpdateListener = onCartItemUpdateListener;
    }

    @NonNull
    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recview_cart_item, parent, false);
        return new CartItemAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.MyViewHolder holder, int position) {
        CartItem item = cartItemsList.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText("Quantity: " + item.getQuantity());
        holder.price.setText(item.getPriceWithCurrency());
        String thumbnailURL = item.getThumbnail();
        Glide.with(context).load(thumbnailURL).into(holder.thumbnail);
        holder.delete.setOnClickListener(v -> {
            SharedPreferences sf = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            int removedIndex = holder.getAdapterPosition();
            cartItemsList.remove(removedIndex);
            Cart cart = new Cart();
            cart.setItems(cartItemsList);
            SharedPreferences.Editor editor = sf.edit();
            if (cartItemsList.isEmpty()) {
                editor.putString("Cart", "empty");
                clearCartBtn.setVisibility(View.GONE);
            } else {
                Gson gson = new Gson();
                String json = gson.toJson(cart);
                editor.putString("Cart", json);
            }
            editor.apply();
            notifyItemRemoved(removedIndex);
            notifyItemRangeChanged(removedIndex, cartItemsList.size() - removedIndex);
            onCartItemUpdateListener.onCartItemRemoved();
        });
        holder.addQuantity.setOnClickListener(v -> {
            incrementQuantity(holder.getAdapterPosition());
        });
        holder.minusQuantity.setOnClickListener(v -> {
            decrementQuantity(holder.getAdapterPosition());
        });
    }

    private void incrementQuantity(int position) {
        if (position < 0 || position >= cartItemsList.size()) {
            return; // Invalid position
        }

        CartItem item = cartItemsList.get(position);
        int newQuantity = item.getQuantity() + 1;

        int maxQuantity = 99; // Example max quantity
        if (newQuantity <= maxQuantity) {
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
            if (onCartItemUpdateListener != null) {
                onCartItemUpdateListener.onCartItemUpdated();
            }
        } else {
            // Optionally notify the user that they've reached the maximum quantity
//            showMaxQuantityReachedDialog();
        }
    }

    private void decrementQuantity(int position) {
        if (position < 0 || position >= cartItemsList.size()) {
            return; // Invalid position
        }

        CartItem item = cartItemsList.get(position);
        int newQuantity = item.getQuantity() - 1;

        if (newQuantity >= 1) {
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
            if (onCartItemUpdateListener != null) {
                onCartItemUpdateListener.onCartItemUpdated();
            }
        } else {
            // Ask the user if they want to remove the item from the cart
//            showRemoveItemDialog(position);
        }
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    public interface OnCartItemUpdateListener {
        void onCartItemRemoved();
        void onCartItemUpdated();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView thumbnail;
        TextView price;
        TextView quantity;
        ImageButton delete;

        ImageButton addQuantity;

        ImageButton minusQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_item_name);
            thumbnail = itemView.findViewById(R.id.cart_item_thumbnail);
            price = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            delete = itemView.findViewById(R.id.cart_item_delete);
            addQuantity= itemView.findViewById(R.id.cart_item_add);
            minusQuantity= itemView.findViewById(R.id.cart_item_minus);
        }
    }
}
