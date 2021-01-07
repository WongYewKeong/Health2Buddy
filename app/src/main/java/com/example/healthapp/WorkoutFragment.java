package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.R;

public class WorkoutFragment extends Fragment implements View.OnClickListener{



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        View view =  inflater.inflate(R.layout.fragment_workout, container, false);
        ImageView overallbutton = view.findViewById(R.id.overallbutton);
        ImageView focusedbutton = view.findViewById(R.id.focusedbutton);
        ImageView advancedbutton = view.findViewById(R.id.advancedbutton);
        overallbutton.setOnClickListener(this);
        focusedbutton.setOnClickListener(this);
        advancedbutton.setOnClickListener(this);
        return view;



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.overallbutton:
                Intent intent = new Intent ( getActivity(),overallworkout.class);
                startActivity(intent);
                break;

            case R.id.focusedbutton:
                Intent intent1 = new Intent ( getActivity(),focusedworkout.class);
                startActivity(intent1);
                break;

            case R.id.advancedbutton:
                Intent intent2 = new Intent ( getActivity(),advanceworkout.class);
                startActivity(intent2);
                break;
        }

    }
}