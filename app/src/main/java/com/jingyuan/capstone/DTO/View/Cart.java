package com.jingyuan.capstone.DTO.View;

import java.util.ArrayList;

public class Cart {
    private ArrayList<CartItem> items;

    public Cart() {
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }
}
