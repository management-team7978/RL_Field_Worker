package com.rl.fieldworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.fragment.BankKycFragment;
import com.rl.fragment.DashboardFragment;
import com.rl.fragment.GetRquestListFragment;
import com.rl.fragment.ProfileFragment;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    TextView tvTitle;
    ImageView imgShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        AppController.initialize(getApplicationContext());
        bottomNav=findViewById(R.id.bottomNav);
        tvTitle=findViewById(R.id.tvTitle);
        imgShare=findViewById(R.id.imgShare);
        tvTitle.setText("Dashboard");

        
        replaceFragment(new DashboardFragment());
        Intent i = getIntent();
        if (i.hasExtra("redirect")){
            tvTitle.setText("Profile");
            bottomNav.setSelectedItemId(R.id.menu_request_list);
            replaceFragment(new ProfileFragment());
        }else {
            tvTitle.setText("Dashboard");
            replaceFragment(new DashboardFragment());
        }
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        tvTitle.setText("Dashboard");
                        replaceFragment(new DashboardFragment());
                        break;
                    case R.id.menu_profile:
                        tvTitle.setText("Profile");
                        replaceFragment(new ProfileFragment());
                        break;

                    case R.id.menu_bank:
                        tvTitle.setText("Add Bank Details");
                        replaceFragment(new BankKycFragment());
                        break;
                    case R.id.menu_request_list:
                        tvTitle.setText("Request List");
                        replaceFragment(new GetRquestListFragment());
                        break;
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer,fragment);
        fragmentTransaction.commit();
    }

    private void shareIt() {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "re_work.png");

        OutputStream os;
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
            os = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, os); // 100% quality
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        Intent intent =new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "RL Work");
        intent.putExtra(Intent.EXTRA_TEXT," Welcome to RL Work... \n\nClick on below link to Download our application: \n\nhttps://play.google.com/store/apps/details?id=com.rl.work"+"\n\n\nUse my Referral Code : "+SharedPreference.get("referral_code"));
        intent.setType("text/plain");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, imageFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // Set type to only show apps that can open your PNG file
        intent.setType("image/png");
        startActivity(intent);
    }



}