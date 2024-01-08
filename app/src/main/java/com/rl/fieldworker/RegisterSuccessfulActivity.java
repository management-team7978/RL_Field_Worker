package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterSuccessfulActivity extends AppCompatActivity {
    TextView tvUsername,tvUserid,tvPassword,tvReferral;
    AppCompatButton btnGotologin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_successful);

        tvUserid=findViewById(R.id.tvUserId);
        tvUsername=findViewById(R.id.tvUserName);
        tvPassword=findViewById(R.id.tvPassword);
        btnGotologin=findViewById(R.id.btLogin);
        tvReferral=findViewById(R.id.tvReferral);

        Intent i = getIntent();
        if (i.hasExtra("user_id")){
            tvUsername.setText("Hi, "+i.getStringExtra("user_name"));
            tvUserid.setText("Your Relational Manager Id : "+i.getStringExtra("user_id"));
            tvPassword.setText("password : "+i.getStringExtra("password"));
            tvReferral.setText("Referral code: "+i.getStringExtra("referral_code"));
        }

        btnGotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterSuccessfulActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}