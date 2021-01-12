package com.example.healthapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.healthapp.entities.Food;
import com.example.healthapp.entities.Journal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static androidx.core.content.ContextCompat.getSystemService;


public class NutritionFragment extends Fragment {
    
    // Variables
    private String foodName;
    private double lowRangeCal, highRangeCal, REE, neededCarbs, neededProtein, neededFat;
    private double consumedCal, consumedCarbs, consumedProtein, consumedFat;
    
    // Get from firestore
    private double weight, height;
    private double age;
    private String gender;
    
    // Others
    DecimalFormat df = new DecimalFormat("0");
    String date, date2;
    String userId;
    Date currentTime;
    
    ScrollView parent_scroll_nutrition;
    
    // Search View
    SearchView sv_food;
    
    // Progress Views
    TextView tv_carbs_status_nutrition, tv_fat_status_nutrition, tv_protein_status_nutrition, tv_cal_nutrition;
    ProgressBar pb_cal_nutrition, pb_carbs_nutrition, pb_protein_nutrition, pb_fat_nutrition;
    TextView tv_date_nutrition;
    
    // Manually Add Views
    Button btn_add_nutrition_data;
    EditText et_food_name_nutrition, et_cal_nutrition, et_carbs_nutrition, et_protein_nutrition, et_fat_nutrition;
    
    // List view
    ListView lv_consumed_food_nutrition;
    ArrayAdapter<String> foodListAdapter;
    ArrayList<String> foodArray;
    ArrayList<String> foodID;
    ArrayList<String> foodNameList;
    ArrayList<Food> foodList;
    
    // FireStore
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nutrition, container, false);
        
        parent_scroll_nutrition = root.findViewById(R.id.parent_scroll_nutrition);
        
        // Date View
        currentTime = Calendar.getInstance().getTime();
        date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tv_date_nutrition = root.findViewById(R.id.tv_date_nutrition);
        tv_date_nutrition.setText(date2);
        
        // Progress bar
        pb_cal_nutrition = root.findViewById(R.id.pb_cal_nutrition);
        pb_carbs_nutrition = root.findViewById(R.id.pb_carb_nutrition);
        pb_fat_nutrition = root.findViewById(R.id.pb_fat_nutrition);
        pb_protein_nutrition = root.findViewById(R.id.pb_protein_nutrition);
        tv_cal_nutrition = root.findViewById(R.id.tv_pb_cal_nutrition);
        tv_carbs_status_nutrition = root.findViewById(R.id.tv_carbs_status_nutrition);
        tv_fat_status_nutrition = root.findViewById(R.id.tv_fat_status_nutrition);
        tv_protein_status_nutrition = root.findViewById(R.id.tv_protein_status_nutrition);
        
        // Add Intake Manually
        et_food_name_nutrition = root.findViewById(R.id.et_food_name_nutrition);
        et_cal_nutrition = root.findViewById(R.id.et_cal_nutrition);
        et_carbs_nutrition = root.findViewById(R.id.et_carb_nutrition);
        et_protein_nutrition = root.findViewById(R.id.et_protein_nutrition);
        et_fat_nutrition = root.findViewById(R.id.et_fat_nutrition);
        btn_add_nutrition_data = root.findViewById(R.id.btn_add_nutrition_data);
        
        // Food List View
        lv_consumed_food_nutrition = root.findViewById(R.id.lv_consumed_food_nutrition);
        foodListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        foodArray = new ArrayList<>();
        foodID = new ArrayList<>();
        foodNameList = new ArrayList<>();
        foodList = new ArrayList<>();
        
        // Search View
        sv_food = root.findViewById(R.id.sv_food);
        sv_food.setIconifiedByDefault(false);
        sv_food.setSubmitButtonEnabled(true);
        sv_food.setQueryHint("Search food here");
        
        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        
        DocumentReference documentReference = db.collection("users").document(userId);
        DocumentReference documentReference2 = db.collection("users").document(userId).collection("dailyRecord").document(date);
        
        // Solve Scroll View Problem
        lv_consumed_food_nutrition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        
        lv_consumed_food_nutrition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Remove Food");
                alertDialogBuilder.setMessage("Are you sure you want to remove the Food " + foodNameList.get(position));
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                    }
                });
                
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        deleteConsumedFoodRecord(position);
                    }
                });
                alertDialogBuilder.show();
            }
        });
        
        btn_add_nutrition_data.setOnClickListener(view -> {
            if (!et_food_name_nutrition.getText().toString().equals("") && !et_cal_nutrition.getText().toString().equals("") && !et_carbs_nutrition.getText().toString().equals("") && !et_protein_nutrition.getText().toString().equals("") && !et_fat_nutrition.getText().toString().equals("")) {
                addManualConsumedFoodRecord();
                clearEditText();
            } else {
                if (et_food_name_nutrition.getText().toString().equals(""))
                    et_food_name_nutrition.setError("Cannot be Empty");
                if (et_cal_nutrition.getText().toString().equals(""))
                    et_cal_nutrition.setError("Cannot be Empty");
                if (et_carbs_nutrition.getText().toString().equals(""))
                    et_carbs_nutrition.setError("Cannot be Empty");
                if (et_protein_nutrition.getText().toString().equals(""))
                    et_protein_nutrition.setError("Cannot be Empty");
                if (et_fat_nutrition.getText().toString().equals(""))
                    et_fat_nutrition.setError("Cannot Empty");
            }
            
        });
        
        if (sv_food != null) {
            sv_food.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String food;
                    food = query;
                    food = food.replace(" ", "");
                    String f_url = "https://api.nutritionix.com/v1_1/search/" + food + "?results=0:20&fields=item_name,brand_name,item_id,nf_calories,nf_total_carbohydrate,nf_total_fat,nf_protein&appId=60a71170&appKey=b5e07b91274873a936b6099c165e4b15";
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
        
        
        // Calculate the needed nutrition intake of user
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                
                try {
                    weight = Double.parseDouble(value.getString("Weight"));
                    height = Double.parseDouble(value.getString("Height"));
                    
                    age = Double.parseDouble(value.getString("Age"));
                    gender = value.getString("Gender");
                    
                    calculateNeededNutrition();
                } catch (NullPointerException e) {
                    Log.d("Debug", "Nutrition:" + e.getMessage());
                }
            }
        });
        
        // Check if document for today's record exist, if not, create a new document.
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("Debug", "Nutrition Check if document exists");
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
        
        // Get today nutrition intake
        documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d("Debug", "Nutrition Read Nutrition data");
                try {
                    if(value.getString("consumedCal")!=null){
                        consumedCal = Double.parseDouble(value.getString("consumedCal"));
                        consumedCarbs = Double.parseDouble(value.getString("consumedCarbs"));
                        consumedFat = Double.parseDouble(value.getString("consumedFat"));
                        consumedProtein = Double.parseDouble(value.getString("consumedProtein"));
                        displayNutritionData();
                    }
                } catch (NullPointerException e) {
                    Log.d("Debug", "get Nutrition:" + e.getMessage());
                }
            }
        });
        
        // Get consumed Food
        db.collection("users").document(userId).collection("dailyRecord").document(date).collection("consumedFood").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                
                try {
                    foodArray.clear();
                    foodID.clear();
                    foodNameList.clear();
                    foodList.clear();
                    
                    for (QueryDocumentSnapshot doc : value) {
                        
                        Map<String, Object> consumedNutrition = doc.getData();
                        String foodInfor;
                        
                        foodInfor = consumedNutrition.get("item_name").toString() +
                                "\n" + consumedNutrition.get("nf_calories").toString() + " Cal," +
                                " " + consumedNutrition.get("nf_carb").toString() + " Carb," +
                                " " + consumedNutrition.get("nf_protein").toString() + " Protein," +
                                " " + consumedNutrition.get("nf_total_fat").toString() + " Fat";
                        
                        foodArray.add(foodInfor);
                        foodID.add(doc.getId());
                        foodNameList.add(consumedNutrition.get("item_name").toString());
                        
                        foodList.add(new Food(consumedNutrition.get("item_name").toString(), Integer.parseInt(consumedNutrition.get("nf_calories").toString()), Integer.parseInt(consumedNutrition.get("nf_total_fat").toString()), Integer.parseInt(consumedNutrition.get("nf_protein").toString()), Integer.parseInt(consumedNutrition.get("nf_carb").toString())));
                    }
                    
                    showDataInListView();
                } catch (NullPointerException e) {
                    Log.d("Debug", "get Consumed Food " + e.getMessage());
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
        if (!gender.equals("") && weight > 0 && height > 0 && age > 0) {
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
            builder.setTitle("Personal Data Required");
            builder.setMessage("You are required to fill the weight, height, age and gender, in order to use our Nutrition");
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
        tv_cal_nutrition.setText((df.format(consumedCal) + "/" + df.format(lowRangeCal) + " Cal"));
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
    
    public void showDataInListView() {
        foodListAdapter.clear();
        foodListAdapter.addAll(foodArray);
        lv_consumed_food_nutrition.setAdapter(foodListAdapter);
    }
    
    private void addManualConsumedFoodRecord() {
        Map<String, Object> consumedFood = new HashMap<>();
        foodName = et_food_name_nutrition.getText().toString();
        int nf_calories = Integer.parseInt(et_cal_nutrition.getText().toString());
        int nf_carb = Integer.parseInt(et_carbs_nutrition.getText().toString());
        int nf_total_fat = Integer.parseInt(et_fat_nutrition.getText().toString());
        int nf_protein = Integer.parseInt(et_protein_nutrition.getText().toString());
        
        consumedFood.put("item_name", foodName);
        consumedFood.put("nf_calories", nf_calories);
        consumedFood.put("nf_carb", nf_carb);
        consumedFood.put("nf_protein", nf_protein);
        consumedFood.put("nf_total_fat", nf_total_fat);
        
        db.collection("users").document(userId).collection("dailyRecord").document(date).collection("consumedFood").document().set(consumedFood);
        
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
    
    private void clearEditText() {
        et_food_name_nutrition.setText("");
        et_cal_nutrition.setText("");
        et_carbs_nutrition.setText("");
        et_protein_nutrition.setText("");
        et_fat_nutrition.setText("");
    }
    
    private void deleteConsumedFoodRecord(int position) {
        // Deduct the current nutrition intake
        consumedCal -= foodList.get(position).getNf_calories();
        consumedCarbs -= foodList.get(position).getNf_carb();
        consumedFat -= foodList.get(position).getNf_total_fat();
        consumedProtein -= foodList.get(position).getNf_protein();
        
        Map<String, Object> consumedNutrition = new HashMap<>();
        consumedNutrition.put("consumedCal", String.valueOf(consumedCal));
        consumedNutrition.put("consumedCarbs", String.valueOf(consumedCarbs));
        consumedNutrition.put("consumedFat", String.valueOf(consumedFat));
        consumedNutrition.put("consumedProtein", String.valueOf(consumedProtein));
        db.collection("users").document(userId).collection("dailyRecord").document(date).set(consumedNutrition);
        
        // Delete the selected food
        db.collection("users").document(userId).collection("dailyRecord").document(date).collection("consumedFood").document(foodID.get(position)).delete();
    }
}