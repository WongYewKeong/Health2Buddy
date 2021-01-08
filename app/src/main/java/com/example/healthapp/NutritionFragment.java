package com.example.healthapp;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static androidx.core.content.ContextCompat.getSystemService;


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
    DecimalFormat df = new DecimalFormat("0");
    
    // Variables
    String date, date2;
    String userId;
    
    // Views
    TextView tv_carbs_status_nutrition, tv_fat_status_nutrition, tv_protein_status_nutrition;
    ProgressBar pb_cal_nutrition, pb_carbs_nutrition, pb_protein_nutrition, pb_fat_nutrition;
    TextView tv_pb_nutrition, tv_date_nutrition;
    Button btn_add_nutrition_data;
    EditText et_cal_nutrition, et_carbs_nutrition, et_protein_nutrition, et_fat_nutrition;
    
    // Firestore
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nutrition, container, false);
        
        date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Text Views
        tv_carbs_status_nutrition = root.findViewById(R.id.tv_carbs_status_nutrition);
        tv_fat_status_nutrition = root.findViewById(R.id.tv_fat_status_nutrition);
        tv_protein_status_nutrition = root.findViewById(R.id.tv_protein_status_nutrition);
        tv_date_nutrition = root.findViewById(R.id.tv_date_nutrition);
        tv_date_nutrition.setText(date2);
        
        // Progress bar
        pb_cal_nutrition = root.findViewById(R.id.pb_cal_nutrition);
        pb_carbs_nutrition = root.findViewById(R.id.pb_carb_nutrition);
        pb_fat_nutrition = root.findViewById(R.id.pb_fat_nutrition);
        pb_protein_nutrition = root.findViewById(R.id.pb_protein_nutrition);
        tv_pb_nutrition = root.findViewById(R.id.tv_pb_cal_nutrition);
        
        et_cal_nutrition = root.findViewById(R.id.et_cal_nutrition);
        et_carbs_nutrition = root.findViewById(R.id.et_carb_nutrition);
        et_protein_nutrition = root.findViewById(R.id.et_protein_nutrition);
        et_fat_nutrition = root.findViewById(R.id.et_fat_nutrition);
        btn_add_nutrition_data = root.findViewById(R.id.btn_add_nutrition_data);
        
        sv_food = root.findViewById(R.id.sv_food);
        sv_food.setIconifiedByDefault(false);
        sv_food.setSubmitButtonEnabled(true);
        sv_food.setQueryHint("Search food here");
        
        btn_add_nutrition_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> consumedNutrition = new HashMap<>();
                consumedCal += Double.parseDouble(et_cal_nutrition.getText().toString());
                consumedCarbs += Double.parseDouble(et_carbs_nutrition.getText().toString());
                consumedFat += Double.parseDouble(et_fat_nutrition.getText().toString());
                consumedProtein += Double.parseDouble(et_protein_nutrition.getText().toString());
                
                consumedNutrition.put("consumedCal", String.valueOf(consumedCal));
                consumedNutrition.put("consumedCarbs", String.valueOf(consumedCarbs));
                consumedNutrition.put("consumedFat", String.valueOf(consumedFat));
                consumedNutrition.put("consumedProtein", String.valueOf(consumedProtein));
                
                db.collection("users").document(userId).collection("dailyRecord").document(date).set(consumedNutrition);
            }
        });
        
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
        // Text Views
        tv_carbs_status_nutrition.setText((df.format(consumedCarbs) + "/" + df.format(neededCarbs) + " Carbs"));
        tv_fat_status_nutrition.setText((df.format(consumedFat) + "/" + df.format(neededFat) + " Fat"));
        tv_protein_status_nutrition.setText((df.format(consumedProtein) + "/" + df.format(neededProtein) + " Protein"));
        tv_pb_nutrition.setText((df.format(consumedCal) + "/" + df.format(lowRangeCal) + " Cal"));
        // Progress Bar
        pb_cal_nutrition.setProgress((int) consumedCal);
        pb_cal_nutrition.setMax((int) lowRangeCal);
        pb_carbs_nutrition.setProgress((int) consumedCarbs);
        pb_carbs_nutrition.setMax((int) neededCarbs);
        pb_protein_nutrition.setProgress((int) consumedProtein);
        pb_protein_nutrition.setMax((int) neededProtein);
        pb_fat_nutrition.setProgress((int) consumedFat);
        pb_fat_nutrition.setMax((int) neededFat);
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