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
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.R;
import com.example.healthapp.entities.Food;

import java.util.List;

public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<com.example.healthapp.Adapter.FoodRecyclerViewAdapter.FoodViewHolder>{
    
    
    public List<Food> foodList;
    private Context context;
    int counterCreateVH = 0, counterBindVH = 0;
    int index;
    
    public FoodRecyclerViewAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
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
        
        //public TextView tvBeverageName;
        //public ImageView imgViewBeverageImage;
        
        public TextView tv_item_name, tv_cal, tv_serving, tv_carbs, tv_protein, tv_fats;
        public Button btn_add;
        
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            //tvBeverageName = itemView.findViewById(R.id.tv_beverage_name);
            //imgViewBeverageImage = itemView.findViewById(R.id.img_beverage);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_cal = itemView.findViewById(R.id.tv_cal);
            tv_serving = itemView.findViewById(R.id.tv_serving);
            tv_carbs = itemView.findViewById(R.id.tv_carbs);
            tv_fats = itemView.findViewById(R.id.tv_fat);
            tv_protein = itemView.findViewById(R.id.tv_protein);
            
            
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_add.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Button Working", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
        }
    }
}
