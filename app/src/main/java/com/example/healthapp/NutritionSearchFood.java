package com.example.healthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.healthapp.Adapter.FoodRecyclerViewAdapter;
import com.example.healthapp.entities.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NutritionSearchFood extends AppCompatActivity {
    
    Handler handler = new Handler();
    
    LinearLayoutManager linearLayoutManager;
    ArrayList<Food> food_list;
    
    // Recyclerview
    FoodRecyclerViewAdapter foodRecyclerViewAdapter;
    RecyclerView recyclerView;
    
    // Search view
    SearchView sv_food;
    
    // Button
    ImageButton btn_back, btn_refresh;
    
    private String food_query, date;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_search_food);
        
        food_list = new ArrayList<>();
        
        // Intent
        Intent intent = getIntent();
        getResult(intent.getStringExtra("f_url"));
        food_query = intent.getStringExtra("food");
        date = intent.getStringExtra("date");
        
        // Recycler view
        recyclerView = findViewById(R.id.recycler_nutrition_search_food);
        
        linearLayoutManager = new LinearLayoutManager(NutritionSearchFood.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        
        foodRecyclerViewAdapter = new FoodRecyclerViewAdapter(NutritionSearchFood.this, food_list, date);
        recyclerView.setAdapter(foodRecyclerViewAdapter);
        
        notifyDataChanged();
        
        // Search View
        sv_food = findViewById(R.id.sv_nutrition_search_food);
        sv_food.setIconifiedByDefault(false);
        sv_food.setSubmitButtonEnabled(true);
        sv_food.setQueryHint("Search food here");
        sv_food.setQuery(food_query, false);
        
        if (sv_food != null) {
            
            sv_food.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    food_list.clear();
                    String food = query;
                    food = food.replace(" ", "");
                    String f_url = "https://api.nutritionix.com/v1_1/search/" + food + "?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id%2Citem_description%2Cnf_protein%2Cnf_calories%2Cnf_total_carbohydrate%2Cnf_total_fat%2Cnf_serving_size_qty&appId=42e8cbe9&appKey=a4e373fe0f10ab1de40cffbffb9db544";
                    getResult(f_url);
                    notifyDataChanged();
                    return false;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        
        btn_back = findViewById(R.id.btn_back_nutrition_search_view);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        btn_refresh = findViewById(R.id.btn_refresh_nutrition_search_food);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    private void notifyDataChanged() {
        Toast.makeText(NutritionSearchFood.this, "Loading Food Data.. Please Wait", Toast.LENGTH_LONG).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                foodRecyclerViewAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }
    
    private void getResult(String url) {
        StringRequest myReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("hits");
                    
                    int length;
                    if (jsonArray.length() > 8)
                        length = 8;
                    else
                        length = jsonArray.length();
                    
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject jsonObject2 = jsonObject.getJSONObject("fields");
                        String item_name = jsonObject2.getString("item_name");
                        String brand_name = jsonObject2.getString("brand_name");
                        int nf_calories = jsonObject2.getInt("nf_calories");
                        int nf_total_fat = jsonObject2.getInt("nf_total_fat");
                        int nf_protein = jsonObject2.getInt("nf_protein");
                        int nf_carb = jsonObject2.getInt("nf_total_carbohydrate");
                        int nf_serving_size_qty = jsonObject2.getInt("nf_serving_size_qty");
                        
                        food_list.add(new Food(item_name, brand_name, nf_calories, nf_total_fat, nf_protein, nf_carb, nf_serving_size_qty));
                        //Log.d("Debug", "Food_item:" + food_list.get(i).item_name);
                    }
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                
            }
        }, new Response.ErrorListener() {
            
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Debug", error.toString());
            }
        });
        
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
        
    }
}