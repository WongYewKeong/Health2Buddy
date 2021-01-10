package com.example.healthapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class StepsCountRecord extends AppCompatActivity {

    //ListView must have an ArrayAdapter
    ArrayAdapter<String> stepsListAdapter;
    ArrayList<String> stepsArray;

    //FireStore
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    //ListView
    ListView stepsCountRecord;

    //String
    String userId;
    String date;

    //Date
    Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_count_record);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        stepsCountRecord = findViewById(R.id.lv_record);

        stepsListAdapter = new ArrayAdapter<>(StepsCountRecord.this, android.R.layout.simple_list_item_1);
        stepsArray = new ArrayList<>();

        DocumentReference documentReference = db.collection("users").document(userId);

        stepsCountRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        db.collection("users").document(userId).collection("dailyStep").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //Clear the stepsArray
                stepsArray.clear();

                // This value return a list of document, now we use for loop to access them 1 by 1
                for (QueryDocumentSnapshot doc : value) {

                    // The doc is take from the list, it is a Hashmap.
                    Map<String, Object> stepsCountHM = doc.getData();

                    String stepInfor;

                    stepInfor = "Date: " + doc.getId() +
                            "\n Steps Count: " + stepsCountHM.get("stepCount").toString();

                    stepsArray.add(stepInfor);
            }
                showDataInListView();
            }
        });
    }

    public void showDataInListView(){

        stepsListAdapter.clear();
        stepsListAdapter.addAll(stepsArray);
        stepsCountRecord.setAdapter(stepsListAdapter);

    }
}