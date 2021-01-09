package com.example.healthapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class UserFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;

    ConstraintLayout cl_userage, cl_usergender;

    TextView tvName , tvGender;
    EditText etAge ;
    Button btnUpdate;
    ImageView profile;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String SP_GENDER = "gender";

    String[] gender = {"Male","Female"};
    AlertDialog alertDialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        Button signout = root.findViewById(R.id.btn_signout);
        tvName = root.findViewById(R.id.tv_name);
        tvGender = root.findViewById(R.id.tv_gender);
        profile=root.findViewById(R.id.iv_profilepicture);
        cl_userage = root.findViewById(R.id.cl_user_age);
        cl_usergender = root.findViewById(R.id.cl_user_gender);

        etAge = root.findViewById(R.id.et_age);


        btnUpdate =root.findViewById(R.id.bt_update);
        firebaseAuth = FirebaseAuth.getInstance();

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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        return root;


    }


    public void OnEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
    {
        if(error!=null){
            return;
        }

        tvName.setText(value.getString("Username"));
    }


    private void ShowGenderOptions() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(UserFragment.this);
        builder.setTitle("Select your gender");
        builder.setSingleChoiceItems(gender, sharedPref.getInt(SP_GENDER, 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                switch(i){
                    case 0:
                        editor.putInt(SP_GENDER,0);
                        editor.commit();
                        break;

                    case 1:
                        editor.putInt(SP_GENDER,1);
                        editor.commit();
                        break;

                }
                alertDialog.dismiss();
                tvGender.setText(gender[sharedPref.getInt(SP_GENDER,0)]);
            }
        });

    }




}