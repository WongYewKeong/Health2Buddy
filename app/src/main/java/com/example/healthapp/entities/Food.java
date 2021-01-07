package com.example.healthapp.entities;

public class Food {
    
    String item_name, brand_name;
    int nf_calories, nf_total_fat, nf_protein, nf_carb, nf_serving_size_qty;
    
    public Food() {
    }
    
    public Food(String item_name, String brand_name, int nf_calories, int nf_total_fat, int nf_protein, int nf_carb, int nf_serving_size_qty) {
        this.item_name = item_name;
        this.brand_name = brand_name;
        this.nf_calories = nf_calories;
        this.nf_total_fat = nf_total_fat;
        this.nf_protein = nf_protein;
        this.nf_carb = nf_carb;
        this.nf_serving_size_qty = nf_serving_size_qty;
    }
    
    public String getItem_name() {
        return item_name;
    }
    
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
    
    public String getBrand_name() {
        return brand_name;
    }
    
    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }
    
    public int getNf_calories() {
        return nf_calories;
    }
    
    public void setNf_calories(int nf_calories) {
        this.nf_calories = nf_calories;
    }
    
    public int getNf_total_fat() {
        return nf_total_fat;
    }
    
    public void setNf_total_fat(int nf_total_fat) {
        this.nf_total_fat = nf_total_fat;
    }
    
    public int getNf_protein() {
        return nf_protein;
    }
    
    public void setNf_protein(int nf_protein) {
        this.nf_protein = nf_protein;
    }
    
    public int getNf_carb() {
        return nf_carb;
    }
    
    public void setNf_carb(int nf_carb) {
        this.nf_carb = nf_carb;
    }
    
    
    public int getNf_serving_size_qty() {
        return nf_serving_size_qty;
    }
    
    public void setNf_serving_size_qty(int nf_serving_size_qty) {
        this.nf_serving_size_qty = nf_serving_size_qty;
    }
    
}
