package com.example.healthapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;

public class HomeFragment extends Fragment implements SensorEventListener {

    private TextView stepcount,calories,distance,user,weight,height,Bmi;
    private SensorManager sensorManager;
    private Sensor stepcounter;
    private boolean isSensorPresent;
    private Button btnEditWeight,btnEditHeight;
    int stepCount=0;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    int weightnum,heightnum,bmi;






    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View root= inflater.inflate(R.layout.fragment_home, container, false);

       getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       stepcount=root.findViewById(R.id.tv_steps);
       calories=root.findViewById(R.id.tv_calories);
       distance=root.findViewById(R.id.tv_distance);
       btnEditWeight=root.findViewById(R.id.btn_edit_weight);
       btnEditHeight=root.findViewById(R.id.btn_edit_height);
       weight=root.findViewById(R.id.tv_weight);
       height=root.findViewById(R.id.tv_height);
       Bmi=root.findViewById(R.id.tv_bmi);
       user=root.findViewById(R.id.username);
       firebaseAuth=FirebaseAuth.getInstance();
       db=FirebaseFirestore.getInstance();
       String userId=firebaseAuth.getCurrentUser().getUid();


        DocumentReference documentReference=db.collection("users").document(userId);





       sensorManager= (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
       if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
           stepcounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
           isSensorPresent=true;
       }else {
           stepcount.setText("Counter sensor is not present");
           isSensorPresent=false;
       }

       btnEditWeight.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
               builder.setTitle("Enter your weight (in Kg)");


               final EditText input = new EditText(getContext());

               input.setInputType(InputType.TYPE_CLASS_NUMBER);
               builder.setView(input);


               builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                   @Override
                           public void onClick(DialogInterface dialog, int which) {



                               documentReference.update("Weight", input.getText().toString() );
                           }
                       });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               AlertDialog dialog=builder.create();
               builder.show();
           }
       });

       btnEditHeight.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
               builder.setTitle("Enter your height (in Cm)");


               final EditText height = new EditText(getContext());

               height.setInputType(InputType.TYPE_CLASS_NUMBER);
               builder.setView(height);


               builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                   @Override
                           public void onClick(DialogInterface dialog, int which) {

                       documentReference.update("Height", height.getText().toString() );
                           }
                       });

               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               AlertDialog dialog=builder.create();
               builder.show();

           }
       });
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        weight.setText(value.getString("Weight") + " Kg");
                        height.setText(value.getString("Height") + " Cm");
                        user.setText(value.getString("Username"));





            }

        });
       // weightnum= Integer.parseInt(weight.getText().toString());
        //heightnum= Integer.parseInt(height.getText().toString())/100;
        //bmi=weightnum/(heightnum*heightnum);
       // Bmi.setText(String.valueOf(bmi));

       return root;
    }

    public void onSensorChanged(SensorEvent sensorEvent){
        if(sensorEvent.sensor==stepcounter){
            stepCount= (int) sensorEvent.values[0];
            stepcount.setText(String.valueOf(stepCount));
            int cal= (int) (stepCount*0.045);
            calories.setText(String.valueOf(cal)+ " calories");
            int feet= (int) (stepCount*2.5);
            String finaldistance=String.format("%.2f",feet/3.281/1000);
            distance.setText(finaldistance + " km");


        }

    }
    public void onAccuracyChanged(Sensor sensor,int i){

    }

    @Override
    public void onResume() {
        super.onResume();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.registerListener(this,stepcounter,SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.unregisterListener(this,stepcounter);
        }
    }

}