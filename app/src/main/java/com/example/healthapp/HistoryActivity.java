package com.example.healthapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.entities.Workout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historylist;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirestoreRecyclerAdapter adapter;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        historylist=findViewById(R.id.workoutlist);

        Query query=firebaseFirestore.collection("users").document(userId).collection("Activity");

        FirestoreRecyclerOptions<Workout>options=new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query,Workout.class).build();

        adapter= new FirestoreRecyclerAdapter<Workout, WorkoutViewHolder>(options) {
            @NonNull
            @Override
            public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new WorkoutViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position, @NonNull Workout model) {
                holder.list_workout.setText("Activity : "+model.getActivity());
                if(Integer.parseInt(model.getDuration())<60) {
                    holder.list_duration.setText("Duration : " + model.getDuration() + " seconds");
                }else{
                    holder.list_duration.setText("Duration : "+Integer.parseInt(model.getDuration())/60+" minutes "+Integer.parseInt(model.getDuration())%60+ " seconds");
                }
            }
        };
        historylist.setHasFixedSize(true);
        historylist.setLayoutManager(new LinearLayoutManager(this));
        historylist.setAdapter(adapter);

    }
        private class WorkoutViewHolder extends RecyclerView.ViewHolder{
            private TextView list_workout;
            private TextView list_duration;

            public WorkoutViewHolder(@NonNull View itemView) {
                super(itemView);
                list_workout=itemView.findViewById(R.id.list_activity);
                list_duration=itemView.findViewById(R.id.list_duration);
            }
        }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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