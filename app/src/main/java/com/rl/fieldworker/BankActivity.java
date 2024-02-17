package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class BankActivity extends AppCompatActivity {
    EditText edtBankName,edtHolderName,edtAccountNumber,edtIfsc,edtpan,edtaadhar;
    Button btSubmit;
    RelativeLayout rlLoader;
    String dialogMsg="";
    TextView tvKYCStatus;
    ImageView imgBack;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        edtBankName=findViewById(R.id.edtBankName);
        edtHolderName=findViewById(R.id.edtHolderName);
        edtAccountNumber=findViewById(R.id.edtAccountNumber);
        edtIfsc=findViewById(R.id.edtIfsc);
        edtpan=findViewById(R.id.edtpan);
        edtaadhar=findViewById(R.id.edtaadhar);
        tvKYCStatus=findViewById(R.id.tvKYCStatus);

        btSubmit=findViewById(R.id.btSubmit);
        rlLoader=findViewById(R.id.rlLoader);
        imgBack=findViewById(R.id.imgBack);

        edtaadhar.setText(SharedPreference.get("adhar_card"));
        getBankKyc(SharedPreference.get("uuid"));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BankActivity.this, MainActivity.class);
                i.putExtra("redirect","1");
                startActivity(i);
                finish();
            }
        });

        Intent i = getIntent();
        if (i.hasExtra("redirect_to")) {
            String redirectTo = i.getStringExtra("redirect_to");

            // Check the value of "redirect_to" and show/hide the button accordingly
            if (redirectTo.equals("show_update_bt")) {
                btSubmit.setVisibility(View.VISIBLE);
            } else {
                btSubmit.setVisibility(View.GONE);
            }
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bName = edtBankName.getText().toString().trim();
                String bHolderName = edtHolderName.getText().toString().trim();
                String bAccNum = edtAccountNumber.getText().toString().trim();
                String ifsc = edtIfsc.getText().toString().trim();
                String pan = edtpan.getText().toString().trim();
                String aadhar = edtaadhar.getText().toString().trim();

                if (bName.isEmpty() || bHolderName.isEmpty() || bAccNum.isEmpty() || ifsc.isEmpty() || pan.isEmpty()
                        || aadhar.isEmpty()) {
                    Toast.makeText(BankActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                }else if (pan.length() != 10) {
                    edtpan.setError( "Pan Card must be 10 digits");
                    Toast.makeText(BankActivity.this, "Pincode must be 6 digits", Toast.LENGTH_SHORT).show();
                } else {
                    AddBankDetails(SharedPreference.get("uuid"),bName,bHolderName,bAccNum,ifsc,pan);
                }
            }
        });
    }

    private void getBankKyc(String uuid) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.kyc_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","get bank =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        btSubmit.setText("Update");
                        edtAccountNumber.setText(jsonObject.getString("account_number"));
                        edtBankName.setText(jsonObject.getString("bank_name"));
                        edtHolderName.setText(jsonObject.getString("holder_name"));
                        edtIfsc.setText(jsonObject.getString("ifsc_code"));
                        edtaadhar.setText(jsonObject.getString("adhar_no"));
                        edtpan.setText(jsonObject.getString("pan_no"));

                        if (jsonObject.has("pan_no") && !jsonObject.getString("pan_no").isEmpty()) {
                            edtpan.setFocusable(false);
                            edtpan.setClickable(false);
                            edtpan.setLongClickable(false);
                        } else {
                            edtpan.setFocusableInTouchMode(true);
                            edtpan.setClickable(true);
                            edtpan.setLongClickable(true);
                        }

                        if (jsonObject.getString("kyc_status").equalsIgnoreCase("verification")){
                            tvKYCStatus.setBackgroundResource(R.color.yellow);
                            tvKYCStatus.setText(jsonObject.getString("kyc_status"));
                        }else if (jsonObject.getString("kyc_status").equalsIgnoreCase("incomplete")){
                            tvKYCStatus.setBackgroundResource(R.color.red);
                            tvKYCStatus.setText(jsonObject.getString("kyc_status"));
                            edtAccountNumber.setText("");
                            edtBankName.setText("");
                            edtHolderName.setText("");
                            edtaadhar.setText("");
                            edtpan.setText("");
                            edtIfsc.setText("");
                        } else if (jsonObject.getString("kyc_status").equalsIgnoreCase("complete")) {
                            tvKYCStatus.setBackgroundResource(R.color.green);
                            tvKYCStatus.setText(jsonObject.getString("kyc_status"));
                        }

                    }else {
                        rlLoader.setVisibility(View.GONE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
                                SharedPreference.removeKey("adhar_card");
                            }
                            Intent i = new Intent(BankActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }else if (jsonObject.getString("status").equals("false")){
                            tvKYCStatus.setVisibility(View.GONE);
                        }
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
                params.put("uuid",uuid);
                Log.i("pri",""+params);
                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void AddBankDetails(String uuid, String bName, String bHolderName, String bAccNum, String ifsc, String pan) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.add_bank_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("register","register =>>"+response);
                rlLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("register","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        openCustomSuccessDialog(jsonObject.getString("message"));
                        getBankKyc(uuid);
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
                params.put("uuid",uuid);
                params.put("bank_name",bName);
                params.put("holder_name",bHolderName);
                params.put("account_number",bAccNum);
                params.put("ifsc_code",ifsc);
                params.put("pan_no",pan);

                Log.i("pri","params=>"+params);

                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void openCustomFailedDialog(String dialogMsg) {
        final Dialog dialog = new Dialog(BankActivity.this);
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
                Intent i=new Intent(BankActivity.this, MainActivity.class);
                i.putExtra("redirect","1");
                startActivity(i);
                finish();
                //dialog.dismiss();
            }
        });

        // show the exit dialog
        dialog.show();
    }

    private void openCustomSuccessDialog(String text) {

        final Dialog dialog = new Dialog(BankActivity.this);
        dialog.setContentView(R.layout.popup_success_design);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        TextView textViewYes = (TextView) dialog.findViewById(R.id.textViewYes);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);

        txtMsg.setText(text);

        textViewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // show the exit dialog
        dialog.show();
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