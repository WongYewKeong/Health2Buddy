package com.example.healthapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class timerActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CircleTimeView circleTimeView=findViewById(R.id.circle_timer_view);
        CircleImageView circleImageView=findViewById(R.id.play);
        CircleImageView stop=findViewById(R.id.stop);
        EditText etduration=findViewById(R.id.et_duration);
        EditText etact=findViewById(R.id.et_activity);
        Button save=findViewById(R.id.btn_saveactivity);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


            circleTimeView.setCurrentTime(0);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etduration.getText().toString().isEmpty()&&etact.getText().toString().isEmpty()){
                    circleTimeView.setCurrentTime(0);
                    Toast.makeText(timerActivity.this,"Please enter your activity and duration to start",Toast.LENGTH_SHORT).show();
                }else {
                    circleTimeView.setCurrentTime(Long.parseLong(etduration.getText().toString()));
                    circleTimeView.startTimer();

                }

            }
        });
        circleImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long time=circleTimeView.getCurrentTimeInSeconds();
                circleTimeView.setCurrentTime(time);
                circleTimeView.startTimer();
                return true;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleTimeView.stopTimer();
            }
        });

        circleTimeView.setCircleTimerListener(new CircleTimeView.CircleTimerListener() {
            @Override
            public void onTimerStop() {
               circleTimeView.setLabelText("Activity stopped");
            }

            @Override
            public void onTimerStart(long time) {
                circleTimeView.setLabelText("Activity started");
            }

            @Override
            public void onTimerTimeValueChanged(long time) {

            }
        });

        CollectionReference documentReference = db.collection("users").document(userId).collection("Activity");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> activity = new HashMap<>();
                if(!etact.getText().toString().isEmpty()||!etduration.getText().toString().isEmpty()) {
                    activity.put("Activity", etact.getText().toString());
                    activity.put("Duration", etduration.getText().toString());
                    documentReference.add(activity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(timerActivity.this,"Activity added",Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    Toast.makeText(timerActivity.this,"Please enter the activity and duration!",Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}