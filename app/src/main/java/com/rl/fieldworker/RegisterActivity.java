package com.rl.fieldworker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.network.NetworkChangeListener;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFirstName,edtEmail,edtPhone,edtAddress,edtPin,edtPassword,edtAdhar,edtFatherName;
    AppCompatButton btCostRegister;
    RelativeLayout rlLogin;
    RelativeLayout rlLoader;
    String dialogMsg="";
    ImageView imgLanguage;
    Spinner spSalaryType;
    ArrayList<String> salaryType=new ArrayList<String>();
    String st_salary="0";
    int from;
    CheckBox CheckTermCondition;
    TextView tvView;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        edtFirstName=findViewById(R.id.edtFirstName);
        edtEmail=findViewById(R.id.edtEmail);
        edtPhone=findViewById(R.id.edtPhone);
        edtAddress=findViewById(R.id.edtAddress);
        edtPin=findViewById(R.id.edtPin);
        edtPassword=findViewById(R.id.edtPassword);
        btCostRegister=findViewById(R.id.btCostRegister);
        rlLogin=findViewById(R.id.rlLogin);
        rlLoader=findViewById(R.id.rlLoader);
        spSalaryType=findViewById(R.id.spSalaryType);
        imgLanguage = findViewById(R.id.imgLanguage);
        CheckTermCondition=findViewById(R.id.CheckTermCondition);
        edtAdhar=findViewById(R.id.edtAdhar);
        tvView=findViewById(R.id.tvView);
        edtFatherName=findViewById(R.id.edtFatherName);
        loadLocale();


        salaryType.add("commission");
        salaryType.add("salary");

        spSalaryType.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, R.layout.custom_spinner_list, salaryType));

        spSalaryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_salary= (String) parent.getSelectedItem();
                Log.i("pri","my model=>"+st_salary);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        });
        rlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
//        CheckTermCondition.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i= new Intent(RegisterActivity.this, TearmsConditionActivity.class);
//                startActivity(i);
//
//            }
//        });

        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(RegisterActivity.this, TearmsConditionActivity.class);
                startActivity(i);
            }
        });

        btCostRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtFirstName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String pin = edtPin.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String adhar = edtAdhar.getText().toString().trim();
                String f_name = edtFatherName.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || pin.isEmpty() || password.isEmpty()||f_name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                }
                else if (phone.length() != 10) {
                    Toast.makeText(getApplicationContext(), "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }else if (adhar.length() != 12) {
                    edtAdhar.setError("Aadhar number must be 12 digits");
                    Toast.makeText(RegisterActivity.this, "Aadhar number must be 12 digits", Toast.LENGTH_SHORT).show();
                }else if (!CheckTermCondition.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please Check Term and Condition", Toast.LENGTH_SHORT).show();
                }else {
                    RegisterCustomer(SharedPreference.get("uuid"),name,email,phone,address,pin,password,st_salary,adhar,f_name);
                }
            }
        });
    }

    private void RegisterCustomer(String uuid, String name, String email, String phone, String address, String pin, String password, String st_salary, String adhar, String f_name) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("register","register =>>"+response);
                rlLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("register","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        Intent i=new Intent(RegisterActivity.this, RegisterSuccessfulActivity.class);
                        i.putExtra("user_id",jsonObject.getString("user_id"));
                        i.putExtra("user_name",jsonObject.getString("name"));
                        i.putExtra("referral_code",jsonObject.getString("user_id"));
                        i.putExtra("password",jsonObject.getString("password"));
                        startActivity(i);
                        finish();
                    }else {
                        dialogMsg=jsonObject.getString("message");
                        openCustomFailedDialog(dialogMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    rlLoader.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                rlLoader.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                params.put("phone",phone);
                params.put("address",address);
                params.put("pincode",pin);
                params.put("password",password);
                params.put("salary_type",st_salary);
                params.put("adhar_card",adhar);
                params.put("father_name",f_name);
                Log.i("pri","params=>"+params);

                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void openCustomFailedDialog(String dialogMsg) {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.popup_failed_design);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        TextView textViewYes = (TextView) dialog.findViewById(R.id.textViewYes);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);

        txtMsg.setText(dialogMsg);
        textViewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // show the exit dialog
        dialog.show();
    }

    private void showLanguageDialog() {
        if (!isFinishing()) {
            final String[] listItems = {"English", "हिंदी"};
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Choose Language");
            builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (listItems[i].equals("English")) {
                        from = 1;
                        setLocale("en");
                        recreate();
                    } else if (listItems[i].equals("हिंदी")) {
                        from = 2;
                        setLocale("hi");
                        recreate();
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private void setLocale(String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("lang",lang);
        editor.apply();

    }
    public void loadLocale(){
        SharedPreferences preferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=preferences.getString("lang","");
        setLocale(language);
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