package com.example.callmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {


    private Button DriverLoginButton;
    private Button DriverRegisterButton;
    private TextView CreateDriverAccount;
    private TextView TitleDriver;
    private TextView DriverRegisterLink;
    private TextView DriverStatus;
    private EditText EmailDriver;
    private EditText PasswordDriver;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference DriversDatabaseReference, driversDatabaseRef;
    private String onlineDriverID;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;


    private FirebaseUser currentUser;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                    startActivity(intent);
                }
            }
        };

        DriverLoginButton = (Button) findViewById(R.id.driver_login_btn);
        DriverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        DriverRegisterLink = (TextView) findViewById(R.id.driver_register_link);    // not driver_register_btn  - was driver_register_btn is wrong
        DriverStatus = (TextView) findViewById(R.id.driver_status);
        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);

        loadingBar = new ProgressDialog(this);

        DriverRegisterButton.setVisibility(View.INVISIBLE);
        DriverRegisterButton.setEnabled(false);


        CreateDriverAccount = (TextView) findViewById(R.id.driver_register_link);
        TitleDriver = (TextView) findViewById(R.id.driver_status);
        DriverLoginButton = (Button) findViewById(R.id.driver_login_btn);
        DriverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);
        loadingBar = new ProgressDialog(this);


        DriverRegisterButton.setVisibility(View.INVISIBLE);
        DriverRegisterButton.setEnabled(false);

        CreateDriverAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDriverAccount.setVisibility(View.INVISIBLE);
                DriverLoginButton.setVisibility(View.INVISIBLE);
                TitleDriver.setText("Driver Registration");

                DriverRegisterButton.setVisibility(View.VISIBLE);
                DriverRegisterButton.setEnabled(true);
            }
        });


        DriverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverLoginButton.setVisibility(View.INVISIBLE);
                DriverRegisterLink.setVisibility(View.INVISIBLE);
                DriverStatus.setText("Register Driver");

                DriverRegisterButton.setVisibility(View.VISIBLE);
                DriverRegisterButton.setEnabled(true);

            }
        });

        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                RegisterDriver(email, password);
            }
        });


        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                SignInDriver(email, password);

            }
        });


        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUserId = mAuth.getCurrentUser().getUid();
                                driversDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
                                driversDatabaseRef.setValue(true);

                                Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                            }
                        }
                    });

                    DriverLoginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String email = EmailDriver.getText().toString();
                            String password = PasswordDriver.getText().toString();

                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                            }

                            if (TextUtils.isEmpty(password)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.setTitle("Please wait :");
                                loadingBar.setMessage("While system is performing processing on your data...");
                                loadingBar.show();

                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(DriverLoginRegisterActivity.this, "Sign In , Successful...", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(DriverLoginRegisterActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        }


                        private void SignInDriver(String email, String password) {
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Pleas write Email...", Toast.LENGTH_SHORT).show();
                            }

                            if (TextUtils.isEmpty(password)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Pleas write Password...", Toast.LENGTH_SHORT).show();
                            } else {

                                loadingBar.setTitle("Driver Login");
                                loadingBar.setMessage("Please wait, while we checking your credientials...");
                                loadingBar.show();

                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                                    startActivity(driverIntent);

                                                    Toast.makeText(DriverLoginRegisterActivity.this, "Driver Logged in Successfuly...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                } else {
                                                    Toast.makeText(DriverLoginRegisterActivity.this, "Login Unsuccessfuly, Please try Again...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                            }

                        }

                        private void RegisterDriver(String email, String password) {
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Pleas write Email...", Toast.LENGTH_SHORT).show();
                            }

                            if (TextUtils.isEmpty(password)) {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Pleas write Password...", Toast.LENGTH_SHORT).show();
                            } else {

                                loadingBar.setTitle("Driver Registration");
                                loadingBar.setMessage("Please wait, while we are register your data...");
                                loadingBar.show();

                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    onlineDriverID = mAuth.getCurrentUser().getUid();
                                                    DriversDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                                            .child("Users").child("Drivers").child(onlineDriverID);


                                                    DriversDatabaseReference.setValue(true);


                                                    Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                                                    startActivity(driverIntent);

                                                    Toast.makeText(DriverLoginRegisterActivity.this, "Driver Register Successfuly...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();


                                                } else {
                                                    Toast.makeText(DriverLoginRegisterActivity.this, "Registeration Unsuccessfuly, Please try Again...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                            }
                        }
                    });
                }

            }
        });
    }

    private void SignInDriver(String email, String password) {

        String email1 = EmailDriver.getText().toString();
        String password1 = PasswordDriver.getText().toString();

        if (TextUtils.isEmpty(email1)) {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password1)) {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Please wait :");
            loadingBar.setMessage("While system is performing processing on your data...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(DriverLoginRegisterActivity.this, "Sign In , Successful...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(DriverLoginRegisterActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void RegisterDriver(String email, String password)
    {
        String email2 = EmailDriver.getText().toString();
        String password2 = PasswordDriver.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Please wait :");
            loadingBar.setMessage("While system is performing processing on your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        currentUserId = mAuth.getCurrentUser().getUid();
                        driversDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
                        driversDatabaseRef.setValue(true);

                        Intent intent = new Intent(DriverLoginRegisterActivity.this, DriversMapActivity.class);
                        startActivity(intent);

                        loadingBar.dismiss();
                    }
                    else
                    {
                        Toast.makeText(DriverLoginRegisterActivity.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();
                    }
                }
            });


        }
    }
    }
