package com.example.healthapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

public class HomeFragment extends Fragment implements SensorEventListener {

    private TextView stepcount, calories, distance, user, weight, height, Bmi, bmiStatus, goal, goalnotification;
    private ImageView information;
    private SensorManager sensorManager;
    private Sensor stepcounter;
    private boolean isSensorPresent;
    private Button btnEditWeight, btnEditHeight, btnEditGoal;
    int stepCount = 0;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    int weightnum, goalnum;
    double heightnum, bmi;
    DecimalFormat df = new DecimalFormat("0.00");
    static final int REQUEST_CODE = 123;
    int numstep=0;
    String date;
    String userId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        Log.d("Debug", "Today: " + date);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stepcount = root.findViewById(R.id.tv_steps);
        calories = root.findViewById(R.id.tv_calories);
        distance = root.findViewById(R.id.tv_distance);
        btnEditWeight = root.findViewById(R.id.btn_edit_weight);
        btnEditHeight = root.findViewById(R.id.btn_edit_height);
        btnEditGoal = root.findViewById(R.id.btn_edit_goal);
        goal = root.findViewById(R.id.tv_goal);
        goalnotification = root.findViewById(R.id.tv_goal_notification);
        weight = root.findViewById(R.id.tv_weight);
        height = root.findViewById(R.id.tv_height);
        Bmi = root.findViewById(R.id.tv_bmi);
        bmiStatus = root.findViewById(R.id.tv_bmi_status);
        information = root.findViewById(R.id.img_info);
        user = root.findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


        DocumentReference documentReference = db.collection("users").document(userId);
        DocumentReference documentReference2 = db.collection("users").document(userId).collection("dailyStep").document(date);


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Grant permission");
                builder.setMessage("This app need to use step sensor in your phone in order to make step counter function normally.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepcounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isSensorPresent = true;
        } else {
            stepcount.setText("Counter sensor is not present");
            isSensorPresent = false;
        }

        btnEditWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter your weight (in Kg)");


                final EditText input = new EditText(getActivity());

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);


                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            if (!input.getText().toString().isEmpty()) {

                                documentReference.update("Weight", input.getText().toString());
                            } else {
                                dialog.cancel();
                            }
                        }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnEditHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter your height (in Cm)");


                final EditText height = new EditText(getActivity());

                height.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(height);


                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!height.getText().toString().isEmpty()) {
                            documentReference.update("Height", height.getText().toString());
                        }else{
                            dialog.cancel();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        btnEditGoal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter your goal of steps count");


                final EditText goal = new EditText(getActivity());

                goal.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(goal);


                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!goal.getText().toString().isEmpty()) {
                            documentReference.update("Goal of steps count", goal.getText().toString());
                        } else {
                            dialog.cancel();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    return;
                }


                goal.setText(value.getString("Goal of steps count"));
                weight.setText(value.getString("Weight") + " Kg");
                height.setText(value.getString("Height") + " Cm");
                user.setText(value.getString("Username"));

                goalnum = Integer.parseInt(value.getString("Goal of steps count"));
                weightnum = Integer.parseInt(value.getString("Weight"));
                heightnum = Double.parseDouble(value.getString("Height")) / 100;
                bmi = weightnum / (heightnum * heightnum);


                Bmi.setText(String.valueOf(df.format(bmi)) + " Kg/m\u00B2");



                documentReference.update("BMI", String.valueOf(df.format(bmi)));

                if(getActivity()!=null&&isAdded()) {
                    if (bmi < 18.5) {
                        bmiStatus.setText("Underweight");
                        bmiStatus.setBackgroundColor(getActivity().getResources().getColor(R.color.underweight));
                    } else if (bmi >= 18.5 && bmi < 25) {
                        bmiStatus.setText("Normal");
                        bmiStatus.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
                    } else if (bmi >= 25 && bmi < 30) {
                        bmiStatus.setText("Overweight");
                        bmiStatus.setBackgroundColor(getActivity().getResources().getColor(R.color.overweight));
                    } else if (bmi >= 30) {
                        bmiStatus.setText("Obese");
                        bmiStatus.setBackgroundColor(getActivity().getResources().getColor(R.color.obese));
                    } else {
                        bmiStatus.setText("No data");
                        bmiStatus.setBackgroundColor(getActivity().getResources().getColor(R.color.teal_200));
                    }
                }
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
                    //stepCount = Integer.parseInt(value.getString("stepCount"));
                    stepcount.setText(value.getString("stepCount"));
                    if (goalnum == 0 ){
                        goalnotification.setText("You have not set your goal of steps count today. Please set your goal below.");
                    }

                    else if (Integer.parseInt(value.getString("stepCount")) < goalnum){
                        goalnotification.setText("Unfortunately, you have not reach your goal of steps count. Try harder.");
                    }
                    else if (Integer.parseInt(value.getString("stepCount")) >= goalnum){
                        goalnotification.setText("Congratulation! You have reached your goal of steps count!");
                    }
                    int cal = (int) ((int) (Integer.parseInt(value.getString("stepCount"))) * 0.045);
                    calories.setText(String.valueOf(cal) + " calories");
                    int feet = (int) ((int) (Integer.parseInt(value.getString("stepCount")))* 2.5);
                    String finaldistance = String.format("%.2f", feet / 3.281 / 1000);
                    distance.setText(finaldistance + " km");
                    //onSensorChanged();
                } catch (NullPointerException e) {
                    Log.d("Debug", e.getMessage());
                }
            }
        });

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.Info).setTitle("Information").setPositiveButton("OK",null);

                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });


        return root;
    }
    int total=0;
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == stepcounter&&isAdded()) {

            stepCount=numstep++;
            DocumentReference documentReference2 = db.collection("users").document(userId).collection("dailyStep").document(date);

                    documentReference2.update("stepCount",String.valueOf(stepCount));



           //stepCount = (int) sensorEvent.values[0];
            //stepcount.setText(String.valueOf(stepCount));

        }

    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this, stepcounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
           sensorManager.unregisterListener(this, stepcounter);
        }
    }

    private void createTodayRecord() {
         //Create the FireStore document for today record
        Map<String, Object> totalstepcount = new HashMap<>();
        totalstepcount.put("stepCount", "0");

        db.collection("users").document(userId).collection("dailyStep").document(date).set(totalstepcount);
    }


}