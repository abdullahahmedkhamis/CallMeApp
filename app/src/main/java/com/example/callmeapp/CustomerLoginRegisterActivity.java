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

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button CustomerLoginButton;
    private Button CustomerRegisterButton;
    private TextView CreateCustomerAccount;
    private TextView TitleCustomer;
    private TextView CustomerRegisterLink;
    private TextView CustomerStatus;
    private EditText EmailCustomer;
    private EditText PasswordCustomer;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference CustomersDatabaseReference;
    private String onlineCustomerID;

    private Button LoginCustomerButton;
    private Button RegisterCustomerButton;

    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private FirebaseUser currentUser;
    String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        mAuth = FirebaseAuth.getInstance();


        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentUser != null)
                {
                    Intent intent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                    startActivity(intent);
                }
            }
        };

        CreateCustomerAccount = (TextView) findViewById(R.id.register_customer_link);
        TitleCustomer = (TextView) findViewById(R.id.customer_status);
        CustomerLoginButton = (Button) findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = (Button) findViewById(R.id.customer_register_btn);
        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);
        loadingBar = new ProgressDialog(this);


        CustomerRegisterButton.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setEnabled(false);

        CreateCustomerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateCustomerAccount.setVisibility(View.INVISIBLE);
                LoginCustomerButton.setVisibility(View.INVISIBLE);
                TitleCustomer.setText("Driver Registration");

                RegisterCustomerButton.setVisibility(View.VISIBLE);
                RegisterCustomerButton.setEnabled(true);
            }
        });




        CustomerLoginButton = (Button)findViewById(R.id.customer_login_btn);
         CustomerRegisterButton= (Button)findViewById(R.id.customer_register_btn);
          CustomerRegisterLink= (TextView) findViewById(R.id.register_customer_link);
          CustomerStatus= (TextView) findViewById(R.id.customer_status);
        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);

        loadingBar = new ProgressDialog(this);


          CustomerRegisterButton.setVisibility(View.INVISIBLE);
          CustomerRegisterButton.setEnabled(false);


        CreateCustomerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateCustomerAccount.setVisibility(View.INVISIBLE);
                LoginCustomerButton.setVisibility(View.INVISIBLE);
                TitleCustomer.setText("Driver Registration");

                RegisterCustomerButton.setVisibility(View.VISIBLE);
                RegisterCustomerButton.setEnabled(true);
            }
        });

          CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  CustomerLoginButton.setVisibility(View.INVISIBLE);
                  CustomerRegisterLink.setVisibility(View.INVISIBLE);
                  CustomerStatus.setText("Register Customer");

                  CustomerRegisterButton.setVisibility(View.VISIBLE);
                  CustomerRegisterButton.setEnabled(true);

              }
          });


        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                RegisterCustomer(email, password);
            }
        });

        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();
  SignInCustomer(email, password);

            }
        });

    }

    private void SignInCustomer(String email, String password)
    {

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Pleas write Email...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Pleas write Password...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Customer Login");
            loadingBar.setMessage("Please wait, while we are checking your credientials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                Intent customerIntent = new Intent (CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(customerIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Login Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login Unsuccessfully, Please try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }
    }

    private void RegisterCustomer(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Pleas write Email...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Pleas write Password...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Customer Registration");
            loadingBar.setMessage("Please wait, while we are register your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                onlineCustomerID = mAuth.getCurrentUser().getUid();
                                CustomersDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Customers").child(onlineCustomerID);


                                CustomersDatabaseReference.setValue(true);

                                Intent driverIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Login Successfuly...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login Unsuccessfuly, Please try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

            RegisterCustomerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String email = EmailCustomer.getText().toString();
                    String password = PasswordCustomer.getText().toString();

                    if(TextUtils.isEmpty(email))
                    {
                        Toast.makeText(CustomerLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                    }

                    if(TextUtils.isEmpty(password))
                    {
                        Toast.makeText(CustomerLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
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
                                    CustomersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(currentUserId);
                                    CustomersDatabaseReference.setValue(true);

                                    Intent intent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                    startActivity(intent);

                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(CustomerLoginRegisterActivity.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                }
            });

            LoginCustomerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String email = EmailCustomer.getText().toString();
                    String password = PasswordCustomer.getText().toString();

                    if(TextUtils.isEmpty(email))
                    {
                        Toast.makeText(CustomerLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                    }

                    if(TextUtils.isEmpty(password))
                    {
                        Toast.makeText(CustomerLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        loadingBar.setTitle("Please wait :");
                        loadingBar.setMessage("While system is performing processing on your data...");
                        loadingBar.show();

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(CustomerLoginRegisterActivity.this, "Sign In , Successful...", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                    startActivity(intent);

                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(CustomerLoginRegisterActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();

                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                }
            });

        }
    }
}
