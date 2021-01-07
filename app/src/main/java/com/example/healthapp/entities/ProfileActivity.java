package com.example.healthapp.entities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName ;
    EditText etAge , etGender ;
    Button btnUpdate;

    
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_name);

        etAge = findViewById(R.id.et_age);
        etGender = findViewById(R.id.et_gender);

        btnUpdate = findViewById(R.id.bt_update);

    }



}
