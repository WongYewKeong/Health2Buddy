package com.example.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UserFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;

    ConstraintLayout cl_userage, cl_usergender;

    TextView tvName,tvAge,tvGender;
    Button btnUpdate;
    ImageView profile;
    FirebaseFirestore db;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    int age; String gender;
    String SP_GENDER = "gender";
    String date , userId;
    String[] genderArray = {"Male","Female"};
    AlertDialog alertDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        Button signout = root.findViewById(R.id.btn_signout);
        //btnUpdate = root.findViewById(R.id.btn);
        tvName = root.findViewById(R.id.tv_name);
        tvGender = root.findViewById(R.id.tv_gender);
        tvAge = root.findViewById(R.id.tv_age);
        profile=root.findViewById(R.id.iv_profilepicture);
        cl_userage = root.findViewById(R.id.cl_user_age);
        cl_usergender = root.findViewById(R.id.cl_user_gender);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        sharedPref = this.getActivity().getSharedPreferences("user_settings" , Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        tvGender.setText(genderArray[sharedPref.getInt(SP_GENDER, 0)]);

        DocumentReference documentReference = db.collection("users").document(userId).collection("UserDetails").document(date);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getContext(), gso);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                });

                   /* Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);*/

            }
        });





        cl_userage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        tvAge.setText(value.getString("Age"));
                        //gender = values.getString("Gender");

                    }
                });
                EnterUserAge();
            }
        });



        cl_usergender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowGenderOptions();
            }
        });

                return root;
    }



    private void ShowGenderOptions() {
        DocumentReference documentReference = db.collection("users").document(userId);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Select your gender");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                tvGender.setText(value.getString("Gender"));
                //gender = values.getString("Gender");

            }
        });

        builder.setSingleChoiceItems(genderArray, sharedPref.getInt(SP_GENDER, 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                switch(i){
                    case 0:
                        editor.putInt(SP_GENDER,0);
                        editor.commit();
                        documentReference.update("Gender", "Male");
                        break;

                    case 1:
                        editor.putInt(SP_GENDER,1);
                        editor.commit();
                        documentReference.update("Gender", "Female");
                        break;

                }
                alertDialog.dismiss();
                tvGender.setText(genderArray[sharedPref.getInt(SP_GENDER,0)]);
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void EnterUserAge() {
        DocumentReference documentReference = db.collection("users").document(userId);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Enter your Age");

            final EditText input = new EditText(getActivity());

            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }

                    tvAge.setText(value.getString("Age"));
                    //gender = values.getString("Gender");
                    //hi
                }
            });


            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    if (!input.getText().toString().isEmpty()) {

                        documentReference.update("Age", input.getText().toString());
                    } else {
                        dialog.cancel();
                    }
                        //String a = input.getText().toString();
                        //editor.putString(age, a);
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



}