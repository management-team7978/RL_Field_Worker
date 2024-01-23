package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MaintenanceActivity extends AppCompatActivity {
    TextView tvtitle,tvmsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        // tvtitle = findViewById(R.id.title);
        tvmsg = findViewById(R.id.desc);
        Intent i = getIntent();
        //tvtitle.setText(i.getStringExtra("title"));
        tvmsg.setText(i.getStringExtra("msg"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // tvtitle.setVisibility(View.VISIBLE);
                tvmsg.setVisibility(View.VISIBLE);
            }
        },1000);

    }
}