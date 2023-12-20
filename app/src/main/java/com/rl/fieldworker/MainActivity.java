package com.rl.fieldworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rl.fragment.DashboardFragment;
import com.rl.fragment.ProfileFragment;
import com.rl.util.AppController;
import com.rl.util.SharedPreference;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    TextView tvTitle;
    ImageView imgShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreference.initialize(getApplicationContext());
        AppController.initialize(getApplicationContext());
        bottomNav=findViewById(R.id.bottomNav);
        tvTitle=findViewById(R.id.tvTitle);
        imgShare=findViewById(R.id.imgShare);
        tvTitle.setText("Dashboard");
        replaceFragment(new DashboardFragment());


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
}