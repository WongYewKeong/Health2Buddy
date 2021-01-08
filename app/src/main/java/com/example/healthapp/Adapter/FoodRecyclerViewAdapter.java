package com.example.healthapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.R;
import com.example.healthapp.entities.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<com.example.healthapp.Adapter.FoodRecyclerViewAdapter.FoodViewHolder> {
    
    
    public List<Food> foodList;
    private Context context;
    int counterCreateVH = 0, counterBindVH = 0;
    
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    
    String userId, date;
    private double consumedCal, consumedCarbs, consumedProtein, consumedFat;
    
    public FoodRecyclerViewAdapter(Context context, List<Food> foodList, String date) {
        this.context = context;
        this.foodList = foodList;
        this.date = date;
        
        // Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        
        DocumentReference documentReference2 = db.collection("users").document(userId).collection("dailyRecord").document(date);
        documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    consumedCal = Double.parseDouble(value.getString("consumedCal"));
                    consumedCarbs = Double.parseDouble(value.getString("consumedCarbs"));
                    consumedFat = Double.parseDouble(value.getString("consumedFat"));
                    consumedProtein = Double.parseDouble(value.getString("consumedProtein"));
                } catch (NullPointerException e) {
                    // Blank
                }
            }
        });
        
        
    }
    
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        View food_row = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_row, null);
        
        counterCreateVH++;
        Log.d("Debug", "onCreateViewHolder: " + counterCreateVH);
        return new FoodViewHolder(food_row);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        
        holder.tv_item_name.setText(foodList.get(position).getItem_name());
        holder.tv_cal.setText(String.valueOf(foodList.get(position).getNf_calories()));
        holder.tv_carbs.setText(String.valueOf(foodList.get(position).getNf_carb()));
        holder.tv_fats.setText(String.valueOf(foodList.get(position).getNf_total_fat()));
        holder.tv_protein.setText(String.valueOf(foodList.get(position).getNf_protein()));
        counterBindVH++;
        holder.tv_serving.setText((String.valueOf(foodList.get(position).getNf_serving_size_qty()) + " serving"));
        Log.d("Debug", "onBindViewHolder: " + counterBindVH);
    }
    
    @Override
    public int getItemCount() {
        return foodList.size();
    }
    
    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        
        public TextView tv_item_name, tv_cal, tv_serving, tv_carbs, tv_protein, tv_fats;
        public Button btn_add;
        
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_cal = itemView.findViewById(R.id.tv_cal);
            tv_serving = itemView.findViewById(R.id.tv_serving);
            tv_carbs = itemView.findViewById(R.id.tv_carbs);
            tv_fats = itemView.findViewById(R.id.tv_fat);
            tv_protein = itemView.findViewById(R.id.tv_protein);
            
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_add.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View view) {
            // Add consumed Food item to FireStore
            db.collection("users").document(userId).collection("dailyRecord").document(date).collection("consumedFood").document().set(foodList.get(getLayoutPosition()));
            
            // Add this food calories to FireStore
            consumedCal += foodList.get(getLayoutPosition()).getNf_calories();
            consumedCarbs += foodList.get(getLayoutPosition()).getNf_carb();
            consumedFat += foodList.get(getLayoutPosition()).getNf_total_fat();
            consumedProtein += foodList.get(getLayoutPosition()).getNf_protein();
            
            Map<String, Object> consumedNutrition = new HashMap<>();
            consumedNutrition.put("consumedCal", String.valueOf(consumedCal));
            consumedNutrition.put("consumedCarbs", String.valueOf(consumedCarbs));
            consumedNutrition.put("consumedFat", String.valueOf(consumedFat));
            consumedNutrition.put("consumedProtein", String.valueOf(consumedProtein));
            
            db.collection("users").document(userId).collection("dailyRecord").document(date).set(consumedNutrition);
            
            ((Activity) context).finish();
        }
    }
}
