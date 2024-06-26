package com.rl.fieldworker;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.network.NetworkChangeListener;
import com.rl.util.AppController;
import com.rl.util.SharedPreference;

public class SplashScreenActivity extends AppCompatActivity {
    boolean isReady = false;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady=true;
                if (SharedPreference.contains("uuid")) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        },1000);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
//            View content = findViewById(android.R.id.content);
//
//            content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    if (isReady) {
//                        content.getViewTreeObserver().removeOnPreDrawListener(this);
//                    }
//                    closeSplashScreen();
//                    return false;
//                }
//            });
//        }else {
//            setContentView(R.layout.activity_splash_screen);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                   // isReady=true;
//                    if (SharedPreference.contains("uuid")) {
//                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
//                    } else {
//                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
//                    }
//                    finish();
//                }
//            },3000);
//
//        }

    }

//    private void closeSplashScreen() {
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isReady=true;
//                if (SharedPreference.contains("uuid")) {
//                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
//                } else {
//                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
//                }
//                finish();
//            }
//        },1000);
//    }

    @Override
    protected void onStart() {
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}
