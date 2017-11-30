package com.example.android.hospitalmanagementsystem;

/**
 * Created by DELL on 11/8/2017.
 */

public class Stock {

    private String name;
    private int quantity;
    private String type;
    private String id;

    public Stock(String name, int quantity, String type, String id) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.id = id;
    }

    public Stock(){

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }
}
