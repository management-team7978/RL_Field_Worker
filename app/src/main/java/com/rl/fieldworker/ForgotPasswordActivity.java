package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    RelativeLayout BackFgLayout;
    TextView tvSubmit;
    EditText edtUserId;
    RelativeLayout rlLoader;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        tvSubmit=findViewById(R.id.btSubmit);
        edtUserId=findViewById(R.id.edtUserId);
        rlLoader=findViewById(R.id.rlLoader);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId=edtUserId.getText().toString().trim();
                if (userId.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter user Id", Toast.LENGTH_SHORT).show();
                }else {
                    ForgetPass(userId);
                }
            }
        });
    }

    private void ForgetPass(String userId) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.forgotpassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","forget =>>"+response);
                rlLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("pri","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        edtUserId.setText("");
                    }else {
                        Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    rlLoader.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rlLoader.setVisibility(View.GONE);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",userId);

                return  params;
            }
        };
        AppController.getInstance().add(request);
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