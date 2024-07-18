package com.jingyuan.capstone.DTO.View;

import androidx.annotation.NonNull;

public class CartItem {
    private String doc;
    private String name;
    private int quantity;
    private int price;
    private String thumbnail;

    public CartItem() {
    }

    public CartItem(String doc, String name, int quantity, int price, String thumbnail) {
        this.doc = doc;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getPriceWithCurrency() {
        return price + " USD";
    }
    @NonNull
    public String toString() {
        return "\ndoc: " + doc
                + "\nname: " + name
                + "\nquantity: " + quantity
                + "\nprice: " + price;
    }
}
