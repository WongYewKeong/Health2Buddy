package com.example.healthapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NutritionFragment extends Fragment {
    
    SearchView sv_food;
    
    // Calculate from using weight, height and age.
    private double lowRangeCal, highRangeCal, REE, neededCarbs, neededProtein, neededFat;
    
    // Get from firestore, update when new food added
    private double consumedCal, consumedCarbs, consumedProtein, consumedFat;
    
    // Get from firestore
    private double weight, height;
    private int age;
    private String gender;
    
    // double format
    DecimalFormat df = new DecimalFormat("0.00");
    
    // Variables
    String date;
    String userId;
    
    // Views
    //TextView tv_lowRangeCal, tv_highRangeCal, tv_neededCarbs, tv_neededFat, tv_neededProtein;
    ProgressBar pb_cal_nutrition;
    TextView tv_pb_nutrition;
    
    // Firestore
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nutrition, container, false);
        
        
        date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        Log.d("Debug", "Today: " + date);
        
        // Text Views
        //tv_lowRangeCal = root.findViewById(R.id.tv_lowRangeCal);
        //tv_neededCarbs = root.findViewById(R.id.tv_neededProtein);
        //tv_neededFat = root.findViewById(R.id.tv_neededProtein);
        //tv_neededProtein = root.findViewById(R.id.tv_neededProtein);
        //tv_highRangeCal = root.findViewById(R.id.tv_highRangeCal);
        
        pb_cal_nutrition = root.findViewById(R.id.pb_cal_nutrition);
        tv_pb_nutrition = root.findViewById(R.id.tv_pb_cal_nutrition);
        
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
                    sv_food.setQuery("", false);
                    goToFoodResultActivity(f_url, food, date);
                    return false;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        
        DocumentReference documentReference = db.collection("users").document(userId);
        DocumentReference documentReference2 = db.collection("users").document(userId).collection("dailyRecord").document(date);
        
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                
                weight = Double.parseDouble(value.getString("Weight"));
                height = Double.parseDouble(value.getString("Height"));
                
                //age = Integer.parseInt(value.getString("Age"));
                //gender = value.getString("Gender");
                
                // Waiting Mai firestore
                age = 50;
                gender = "Male";
                
                Log.d("Debug", "OK");
                calculateNeededNutrition();
            }
        });
        
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Debug", "Document exists!");
                    } else {
                        createTodayRecord();
                    }
                } else {
                    Log.d("Debug", "Failed with: ", task.getException());
                }
            }
        });
        
        documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    consumedCal = Double.parseDouble(value.getString("consumedCal"));
                    consumedCarbs = Double.parseDouble(value.getString("consumedCarbs"));
                    consumedFat = Double.parseDouble(value.getString("consumedFat"));
                    consumedProtein = Double.parseDouble(value.getString("consumedProtein"));
                    displayNutritionData();
                } catch (NullPointerException e) {
                    Log.d("Debug", e.getMessage());
                }
            }
        });
        
        return root;
    }
    
    private void goToFoodResultActivity(String f_url, String food, String date) {
        Intent intent = new Intent(getActivity(), NutritionSearchFood.class);
        intent.putExtra("food", food);
        intent.putExtra("f_url", f_url);
        intent.putExtra("date", date);
        startActivity(intent);
    }
    
    private void calculateNeededNutrition() {
        // Execute if all required data provided
        if (!TextUtils.isEmpty(gender) && weight > 0 && height > 0 && age > 0) {
            // Calculate REE, REE is the amount of calories you burn if you lie motionless in bed all day.
            if (gender.equals("Male")) {
                REE = 66 + (13.7 * weight) + (5.0 * height) - (6.8 * age);
            } else {
                REE = 655 + (9.6 * weight) + (1.85 * height) - (4.7 * age);
            }
            // Calculate low range and high range of calories
            lowRangeCal = REE * 1.6;
            highRangeCal = REE * 2.4;
            // How much carbs must eat
            neededCarbs = (lowRangeCal * 0.5) / 4;
            // How much fat must eat
            neededFat = (lowRangeCal * 0.2) / 9;
            // How much protein must eat
            neededProtein = (lowRangeCal * 0.3) / 4;
            
            // Updates data to Text View
            displayNutritionData();
            
        } else {
            // Tell user to fill personal data.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("You are required to fill the weight, height, age and gender, in order to use Nutrition Helper");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
    
    private void displayNutritionData() {
        //tv_lowRangeCal.setText(("Low Range Calories: " + df.format(lowRangeCal)));
        //tv_highRangeCal.setText(("High Range Calories: " + df.format(highRangeCal)));
        //tv_neededProtein.setText(("Protein needed: " + df.format(neededProtein)));
        //tv_neededCarbs.setText(("Carbs needed: " + df.format(neededCarbs)));
        //tv_neededFat.setText(("Fat needed: " + df.format(neededFat)));
        tv_pb_nutrition.setText(String.valueOf(df.format(consumedCal) + "/" + df.format(lowRangeCal) + " cal"));
        pb_cal_nutrition.setProgress((int) consumedCal);
        pb_cal_nutrition.setMax((int) lowRangeCal);
    }
    
    private void createTodayRecord() {
        // Create the FireStore document for today record
        Map<String, Object> consumedNutrition = new HashMap<>();
        consumedNutrition.put("consumedCal", "0");
        consumedNutrition.put("consumedCarbs", "0");
        consumedNutrition.put("consumedFat", "0");
        consumedNutrition.put("consumedProtein", "0");
        
        db.collection("users").document(userId).collection("dailyRecord").document(date).set(consumedNutrition);
    }
    
}