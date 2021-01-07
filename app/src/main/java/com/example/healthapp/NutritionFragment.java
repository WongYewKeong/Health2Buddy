package com.example.healthapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.text.DecimalFormat;


public class NutritionFragment extends Fragment {
    
    SearchView sv_food;
    
    // Calculate from using weight, height and age.
    private double lowRangeCal, highRangeCal, REE, neededCarbs, neededProtein, neededFat;
    
    // Get from firestore, update when new food added
    private double consumedCalories, consumedCarbs, consumedProtein, consumedFat;
    
    // Get from firestore
    private double weight, height;
    private int age;
    private String gender;
    
    // Get current date
    String date;
    
    // Views
    TextView tv_neededCal, tv_neededCarbs, tv_neededFat, tv_neededProtein;
    
    DecimalFormat df = new DecimalFormat("0.00");
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nutrition, container, false);
        
        sv_food = root.findViewById(R.id.sv_food);
        sv_food.setIconifiedByDefault(false);
        sv_food.setSubmitButtonEnabled(true);
        sv_food.setQueryHint("Search food here");

        
        if (sv_food != null) {
            
            sv_food.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String food;
                    food = query;
                    food = food.replace(" ", "");
                    String f_url = "https://api.nutritionix.com/v1_1/search/" + food + "?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id%2Citem_description%2Cnf_protein%2Cnf_calories%2Cnf_total_carbohydrate%2Cnf_total_fat%2Cnf_serving_size_qty&appId=42e8cbe9&appKey=a4e373fe0f10ab1de40cffbffb9db544";
                    sv_food.clearFocus();
                    goToFoodResultActivity(f_url);
                    return false;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        
        return root;
    }
    
    private void goToFoodResultActivity(String f_url) {
        Intent intent = new Intent(getActivity(), NutritionSearchFood.class);
        intent.putExtra("f_url", f_url);
        startActivity(intent);
    }
    
    
}