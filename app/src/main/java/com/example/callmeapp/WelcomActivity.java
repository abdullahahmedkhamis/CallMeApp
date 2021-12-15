package com.example.callmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WelcomActivity extends AppCompatActivity {
private FirebaseDatabase firebaseDatabase;
private DatabaseReference databaseReference;
private Button WelcomeCustomerButton;
private Button WelcomeDriverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("1");
        FirebaseDatabase.getInstance().getReference().setValue(1);

        WelcomeCustomerButton = (Button) findViewById(R.id.welcom_customer_btn);
        WelcomeDriverButton = (Button) findViewById(R.id.welcom_driver_btn);

        WelcomeCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginRegisterCustomerIntent = new Intent(WelcomActivity.this, CustomerLoginRegisterActivity.class);
                startActivity(LoginRegisterCustomerIntent);
            }
        });
        WelcomeDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginRegisterDriverIntent = new Intent(WelcomActivity.this, DriverLoginRegisterActivity.class);
                startActivity(LoginRegisterDriverIntent);
            }
        });

    }
}
