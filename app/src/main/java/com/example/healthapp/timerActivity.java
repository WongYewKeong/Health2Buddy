package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import de.hdodenhof.circleimageview.CircleImageView;

public class timerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CircleTimeView circleTimeView=findViewById(R.id.circle_timer_view);
        CircleImageView circleImageView=findViewById(R.id.play);
        CircleImageView stop=findViewById(R.id.stop);
        EditText etduration=findViewById(R.id.et_duration);
        if(etduration.getText().toString().isEmpty()){
            circleTimeView.setCurrentTime(60);
        }else {
            circleTimeView.setCurrentTime(Long.parseLong(etduration.getText().toString()));
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etduration.getText().toString().isEmpty()){
                    circleTimeView.setCurrentTime(60);
                }else {
                    circleTimeView.setCurrentTime(Long.parseLong(etduration.getText().toString()));
                }
                circleTimeView.startTimer();
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