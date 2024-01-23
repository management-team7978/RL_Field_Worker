package com.rl.fieldworker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.util.AppController;
import com.rl.util.AppVersion;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    AppCompatButton btLogin;
    EditText edtUserId,edtPassword;
    TextView tvForgotPass;
    RelativeLayout rlLoader,rlSignUp;
    String dialogMsg="";
    ImageView imgLanguage;
    int from;
    FloatingActionButton floatingActionButton;
    String st_language="";
    ArrayList<String> arr_language_type = new ArrayList<String>();
    AppVersion appVersion = new AppVersion();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        btLogin=findViewById(R.id.btLogin);
        edtUserId=findViewById(R.id.edtUserId);
        edtPassword=findViewById(R.id.edtPassword);
        tvForgotPass=findViewById(R.id.tvForgotPass);
        rlLoader=findViewById(R.id.rlLoader);
        rlSignUp=findViewById(R.id.rlSignUp);
        floatingActionButton = findViewById(R.id.add_fab);
        imgLanguage = findViewById(R.id.imgLanguage);
        loadLocale();
        imgLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCallDialog();
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

        rlSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);

            }
        });


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtUserId.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                if (email.equals("")||pass.equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
               }else {
                    UserLogin(email,pass);
                }
            }
        });
    }

    private void UserLogin(String userId, String password) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                 rlLoader.setVisibility(View.GONE);
                Log.i("pri","Login =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("pri","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        SharedPreference.save("uuid",jsonObject.getString("uuid"));
                        SharedPreference.save("name",jsonObject.getString("name"));
                        SharedPreference.save("referral_code",jsonObject.getString("referral_code"));

                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        dialogMsg=jsonObject.getString("message");
                        openCustomFailedDialog(dialogMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //rlLoader.setVisibility(View.GONE);
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
                params.put("user_id",userId);
                params.put("password",password);
                Log.i("prii","u=>"+userId+"pass=>"+password);
                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void openCustomFailedDialog(String dialogMsg) {
        final Dialog dialog = new Dialog(LoginActivity.this);
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

    private void ViewCallDialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.popup_call_request);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        Spinner spLanguage = (Spinner) dialog.findViewById(R.id.spLanguage);
        EditText edtHelper = (EditText) dialog.findViewById(R.id.edtHelper);
        AppCompatButton btSubmit = (AppCompatButton) dialog.findViewById(R.id.btSubmit);

        arr_language_type.clear();
        arr_language_type.add("English");
        arr_language_type.add("Hindi");

        spLanguage.setAdapter(new ArrayAdapter<String>(LoginActivity.this, R.layout.custom_spinner_list, arr_language_type));

        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_language= (String) parent.getSelectedItem();
                Log.i("pri","selected=> "+st_language);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String helper_text = edtHelper.getText().toString().trim();
                if (helper_text.equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill all the field", Toast.LENGTH_SHORT).show();
                }else {
                    RaiseHelpTicket(SharedPreference.get("consumer_uuid"),dialog,helper_text,st_language);
                }
            }
        });

        // show the exit dialog
        dialog.show();
    }

    private void RaiseHelpTicket(String uuid, Dialog dialog, String helper_text, String st_language) {
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.customer_call_request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("pri","Login =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("pri","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("uuid",uuid);
                params.put("language",st_language);
                params.put("message",helper_text);
                Log.i("prii","u=>"+params.toString());
                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void showLanguageDialog() {
        if (!isFinishing()) {
            final String[] listItems = {"English", "हिंदी"};
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

        new android.app.AlertDialog.Builder(LoginActivity.this).setTitle("New Update")
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
                        Intent i = new Intent(LoginActivity.this, MaintenanceActivity.class);
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
                // rlProgress.setVisibility(View.GONE);
                error.printStackTrace();
                // showDialog("Please check your Internet connection");
            }
        });

        AppController.getInstance().add(request);

        // Log.d("vic", "Default value: " + mFirebaseRemoteConfig1.getString(VERSION_CODE_KEY));
    }

    protected void onResume() {
        super.onResume();
        CheckVersionApi();
        checkmaintenance();
    }
}