package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {

    protected EditText etEmail,etPassword,etName,etMobile;
    protected CircularProgressButton btnRegister;
    private FirebaseAuth firebaseAuth;
    private ImageView fbSignup,googleSignup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail=findViewById(R.id.et_email);
        etPassword=findViewById(R.id.et_password);
        etMobile=findViewById(R.id.et_mobile);
        etName=findViewById(R.id.et_name);
        btnRegister=findViewById(R.id.btn_register);
        fbSignup=findViewById(R.id.fb_signup);
        googleSignup=findViewById(R.id.google_signup);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=etPassword.getText().toString().trim();
                String email=etEmail.getText().toString().trim();
                String username=etName.getText().toString().trim();
                String phone=etMobile.getText().toString().trim();


                btnRegister.startAnimation();


                if(password.isEmpty()||email.isEmpty()||username.isEmpty()){
                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Please enter all of the registration information").setTitle("Warning").setPositiveButton("OK",null);

                    AlertDialog dialog=builder.create();
                    dialog.show();
                    btnRegister.revertAnimation();

                }else{



                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if( task.isSuccessful()){


                                Toast.makeText(RegisterActivity.this,"Registration success",Toast.LENGTH_SHORT).show();
                                String userID =firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference=db.collection("users").document(userID);
                                Map<String,Object>user=new HashMap<>();
                                user.put("Username",username);
                                user.put("Email",email);
                                user.put("Phone Number",phone);
                                user.put("Weight","0");
                                user.put("Height","0");
                                user.put("Goal of steps count","0");

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                                //btnRegister.doneLoadingAnimation(R.color.green, null);
                                btnRegister.stopAnimation();
                                Intent intent=new Intent(RegisterActivity.this,MenuActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);



                            }else{


                                Toast.makeText(RegisterActivity.this,"This email is already exist with an account", Toast.LENGTH_SHORT).show();
                                btnRegister.revertAnimation();
                            }
                        }
                    });

                }


            }
        });

        fbSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage=Uri.parse("https://www.facebook.com/r.php");
                Intent webIntent=new Intent(Intent.ACTION_VIEW,webpage);

                if(webIntent.resolveActivity(getPackageManager())!=null) {
                    startActivity(webIntent);
                }else{
                    Toast.makeText(RegisterActivity.this,"Sorry, no app can handle this action and data",Toast.LENGTH_SHORT).show();

                }
            }
        });
        googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage=Uri.parse("https://accounts.google.com/signup/v2/webcreateaccount?hl=en&flowName=GlifWebSignIn&flowEntry=SignUp");
                Intent webIntent=new Intent(Intent.ACTION_VIEW,webpage);

                if(webIntent.resolveActivity(getPackageManager())!=null) {
                    startActivity(webIntent);
                }else{
                    Toast.makeText(RegisterActivity.this,"Sorry, no app can handle this action and data",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
   /* private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }*/
    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
}