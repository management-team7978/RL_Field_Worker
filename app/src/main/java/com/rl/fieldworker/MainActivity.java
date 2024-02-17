package com.rl.fieldworker;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
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
import com.rl.fragment.TenderListFragment;
import com.rl.network.NetworkChangeListener;
import com.rl.util.AppController;
import com.rl.util.AppVersion;
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
    AppVersion appVersion = new AppVersion();
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

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

                    case R.id.menu_tender_list:
                        tvTitle.setText("Tender List");
                        replaceFragment(new TenderListFragment());
                        break;

                    case R.id.menu_request_list:
                        tvTitle.setText("Request List");
                        replaceFragment(new GetRquestListFragment());
                        break;
                }
                return true;
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                customExitDialog();
                //finish(); // Finish the activity, for example
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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
        intent.putExtra(Intent.EXTRA_TEXT," Welcome to RL BDM... \n\nClick on below link to Download our application: \n\nhttps://play.google.com/store/apps/details?id=com.rl.fieldworker"+"\n\n\nUse my Referral Code : "+SharedPreference.get("referral_code"));
        intent.setType("text/plain");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, imageFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // Set type to only show apps that can open your PNG file
        intent.setType("image/png");
        startActivity(intent);
    }

    private void CheckVersionApi() {
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.getversion_bdm, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","getversion =>>"+response);
                String version_code=response;

                if (version_code.equals(appVersion.ver_name) ){
                }else {
                    customAppUpdate();
                    Log.i("pri","app =>>"+appVersion.ver_name);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        AppController.getInstance().add(request);
    }

    private void customAppUpdate() {

        new android.app.AlertDialog.Builder(MainActivity.this).setTitle("New Update")
                .setIcon(R.drawable.app_logo)
                .setMessage("A new version of this app is available. Please update it").setPositiveButton(
                        "Update now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rl.fieldworker"));
                                startActivity(intent);
                            }
                        }).setCancelable(false)
                .show();
    }

    private void checkmaintenance() {
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.maintainance_bdm, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("nik",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        String title = jsonObject.getString("title");
                        String msg = jsonObject.getString("msg");
                        Intent i = new Intent(MainActivity.this, MaintenanceActivity.class);
                        i.putExtra("title",title);
                        i.putExtra("msg",msg);
                        startActivity(i);
                        finish();
                    }else {
                        //  showDialog(jsonObject.getString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(LoginActivity.this, "Technical problem arises", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        });

        AppController.getInstance().add(request);

    }

    public  void customExitDialog() {
        // creating custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.custom_layout_exit_app);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        // getting reference of TextView
        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.textViewYes);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.textViewNo);

        // click listener for No
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();

            }
        });
        // click listener for Yes
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog and exit the exit
                dialog.dismiss();
                finish();

            }
        });

        // show the exit dialog
        dialog.show();
    }

    protected void onResume() {
        super.onResume();
        CheckVersionApi();
        checkmaintenance();
    }

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