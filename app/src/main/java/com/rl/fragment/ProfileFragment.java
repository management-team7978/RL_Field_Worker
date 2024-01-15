package com.rl.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rl.fieldworker.BankActivity;
import com.rl.fieldworker.LoginActivity;
import com.rl.fieldworker.R;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    TextView tvName,tvEmail,tvPhone,tvAddress,tvPassword,tvChangePassword,tvUserId,tvBankName,tvBankAccount,tvaddbankacc;
    RelativeLayout rlLogout;
    String uuid;
    RelativeLayout rlLoader;
    CardView cdBankDetails;
    RelativeLayout rlNoBankDetail,rlBankDetails,rlPhone,rlWhatsapp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName=v.findViewById(R.id.tvName);
        tvEmail=v.findViewById(R.id.tvEmail);
        tvPhone=v.findViewById(R.id.tvPhone);
        tvAddress=v.findViewById(R.id.tvAddress);
        tvPassword=v.findViewById(R.id.tvPassword);
        tvChangePassword=v.findViewById(R.id.tvChangePassword);
        rlLogout=v.findViewById(R.id.rlLogout);
        rlLoader=v.findViewById(R.id.rlLoader);
        tvUserId=v.findViewById(R.id.tvUserId);
        cdBankDetails=v.findViewById(R.id.cdBankDetails);
        tvBankName=v.findViewById(R.id.tvBankName);
        tvBankAccount=v.findViewById(R.id.tvBankAccount);
        rlNoBankDetail=v.findViewById(R.id.rlNoBankDetail);
        rlBankDetails=v.findViewById(R.id.rlBankDetails);
        rlPhone=v.findViewById(R.id.rlPhone);
        rlWhatsapp=v.findViewById(R.id.rlWhatsapp);
        tvaddbankacc=v.findViewById(R.id.tvAddBank);

        cdBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), BankActivity.class);
                startActivity(i);
            }
        });

        rlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 100);
                } else {
                    // Permission is already granted, initiate the call
                    makePhoneCall();
                }
            }
        });

        rlWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "9517484939"; // Default number or provide a way to get it dynamically
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
//                    intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//
//                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        startActivity(intent);
//                        Log.i("pri","install");
//                    } else {
//                        Toast.makeText(getActivity(), "WhatsApp is not installed on this device.", Toast.LENGTH_SHORT).show();
//                        Log.i("pri","not install");
//                    }
                    // Replace "1234567890" with the actual phone number or contact ID you want to open in WhatsApp


// Create an Intent with the action set to ACTION_VIEW
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//
//// Set the data URI to the WhatsApp URI with the phone number
//// The "text" parameter can be used to pre-fill a message
//// For example, you can append "&text=Hello" to pre-fill a message with "Hello"
//                    Uri uri = Uri.parse("https://wa.me/" + phoneNumber);
//                    intent.setData(uri);
//
//// Check if WhatsApp is installed on the device
//                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        // Start the activity
//                        startActivity(intent);
//                    } else {
//                        // WhatsApp is not installed, handle this case
//                        // You can prompt the user to install WhatsApp from the Play Store
//                        // or show a custom message
//                        Toast.makeText(getActivity(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
//                    }

                    try {
                        // Open WhatsApp using the package name
                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        sendIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setPackage("com.whatsapp");

                        // Check if WhatsApp is installed on the device
                        if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(sendIntent);
                        } else {
                            // WhatsApp is not installed, handle this case
                            Toast.makeText(getActivity(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // Handle exceptions
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error opening WhatsApp", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Phone number not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });




        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreference.contains("uuid")) {
                    SharedPreference.removeKey("uuid");
                    SharedPreference.removeKey("name");
                    SharedPreference.removeKey("referral_code");
                }

                Intent i=new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });


        uuid=SharedPreference.get("uuid");
        getProfile(uuid);
        getBankKyc(uuid);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initiate the call
                makePhoneCall();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(getActivity(), "Permission denied. Unable to make a call.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "9867663024"));
        startActivity(callIntent);
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

                        rlBankDetails.setVisibility(View.VISIBLE);
                      //  rlNoBankDetail.setVisibility(View.GONE);
                        tvaddbankacc.setText("Update Bank Account");
                        tvBankAccount.setText(jsonObject.getString("account_number"));
                        tvBankName.setText(jsonObject.getString("bank_name"));



                    }else {
                        rlLoader.setVisibility(View.GONE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
                            }
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }else if (jsonObject.getString("status").equals("false")){
                            rlBankDetails.setVisibility(View.GONE);
                            tvaddbankacc.setText("Add Bank Account");
                           // rlNoBankDetail.setVisibility(View.VISIBLE);
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

    private void getProfile(String uuid) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.getprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","profile =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("pri","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        tvName.setText(jsonObject.getString("name"));
                        tvEmail.setText(jsonObject.getString("email"));
                        tvPhone.setText(jsonObject.getString("phone"));
                        tvAddress.setText(jsonObject.getString("address")+", "+jsonObject.getString("pincode"));
                        tvPassword.setText(jsonObject.getString("password"));
                        tvUserId.setText(jsonObject.getString("user_id"));

                    }else {
                        rlLoader.setVisibility(View.GONE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
                            }
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    rlLoader.setVisibility(View.GONE);
                    e.printStackTrace();
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
                params.put("uuid", uuid);
                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

}