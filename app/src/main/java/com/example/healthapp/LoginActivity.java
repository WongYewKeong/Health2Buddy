package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


public class LoginActivity extends AppCompatActivity {

    protected CircularProgressButton btnLogin;
    protected EditText etEmail, etPassword;
    private FirebaseAuth firebaseAuth;
    CallbackManager mCallbackManager;
    private LoginButton fbloginButton;
    private Button googleSignin, fblogin;
    private TextView passwordReset;
    private long pressedTime;

    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    public static final int GOOGLE_SIGN_IN_CODE = 10005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }*/
        setContentView(R.layout.activity_login);

        btnLogin = (CircularProgressButton) findViewById(R.id.btn_login);
        passwordReset = findViewById(R.id.reset_pass);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        googleSignin = findViewById(R.id.google_signin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            GotoMainActivity();
        }

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = signInClient.getSignInIntent();


                startActivityForResult(intent, GOOGLE_SIGN_IN_CODE);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(LoginActivity.this);

        mCallbackManager = CallbackManager.Factory.create();
        fbloginButton = findViewById(R.id.login_button);
        fblogin = findViewById(R.id.btn_fb_login);
        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbloginButton.performClick();
            }
        });
        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {


            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email for resetting password", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("A password reset email has been sent to your email address").setTitle("Success").setPositiveButton("OK", null);

                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error occurred when resetting your email", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                btnLogin.startAnimation();
                if (password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please enter an email and password").setTitle("Warning").setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    btnLogin.revertAnimation();

                } else {

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //btnLogin.doneLoadingAnimation(R.color.green, null);
                                GotoMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "Your Email or Password is incorrect!", Toast.LENGTH_SHORT).show();
                                btnLogin.revertAnimation();
                            }


                        }
                    });
                }
            }
        });
    }


    public void onLoginClick(View View) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

    }

    private void GotoMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // GotoMainActivity();
            updateUI(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GotoMainActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();

            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //GotoMainActivity();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }
}
