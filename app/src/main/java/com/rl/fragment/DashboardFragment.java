package com.rl.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.rl.fieldworker.LoginActivity;
import com.rl.fieldworker.MainActivity;
import com.rl.fieldworker.R;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {
    EditText edtFirstName,edtEmail,edtPhone,edtAddress,edtWorkDesc;
    AppCompatButton btSubmit;
   // RelativeLayout rlLoader;
    String dialogMsg="";
    FloatingActionButton floatingActionButton;
    String st_language="";
    ArrayList<String> arr_language_type = new ArrayList<String>();
    TextView tvUserId,tvName;
    String uuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        AppController.initialize(getActivity());
        SharedPreference.initialize(getActivity());
        FirebaseAnalytics.getInstance(getActivity());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        floatingActionButton = v.findViewById(R.id.add_fab);
        tvName=v.findViewById(R.id.tvName);
        tvUserId=v.findViewById(R.id.tvUserId);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCallDialog();
            }
        });


        uuid=SharedPreference.get("uuid");
        getProfile(uuid);

//        edtEmail=v.findViewById(R.id.edtEmail);
//        edtPhone=v.findViewById(R.id.edtPhone);
//        edtAddress=v.findViewById(R.id.edtAddress);
//        edtFirstName=v.findViewById(R.id.edtFirstName);
//        edtWorkDesc=v.findViewById(R.id.edtWorkDesc);
//        btSubmit=v.findViewById(R.id.btSubmit);
//        rlLoader=v.findViewById(R.id.rlLoader);

//        btSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = edtFirstName.getText().toString().trim();
//                String email = edtEmail.getText().toString().trim();
//                String phone = edtPhone.getText().toString().trim();
//                String address = edtAddress.getText().toString().trim();
//                String desc = edtWorkDesc.getText().toString().trim();
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || desc.isEmpty()) {
//                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();
//                }
//                else if (phone.length() != 10) {
//                    Toast.makeText(getActivity(), "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
//                } else if (!email.matches(emailPattern)) {
//                    Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    AddRequest(SharedPreference.get("uuid"),name,email,phone,address,desc);
//                }
//            }
//        });
        return v;
    }

    private void getProfile(String uuid) {
       // rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.getprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","profile =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("pri","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                     //   rlLoader.setVisibility(View.GONE);
                        tvName.setText(jsonObject.getString("name"));
                        tvUserId.setText("User id is "+jsonObject.getString("user_id"));
                    }else {
                       // rlLoader.setVisibility(View.GONE);
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
                    //rlLoader.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //rlLoader.setVisibility(View.GONE);
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

    private void ViewCallDialog() {
        final Dialog dialog = new Dialog(getActivity());
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

        spLanguage.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_list, arr_language_type));

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
                    Toast.makeText(getActivity(), "Please fill all the field", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

//    private void AddRequest(String uuid, String name, String email, String phone, String address, String desc) {
//        rlLoader.setVisibility(View.VISIBLE);
//        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.add_request, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.i("register","register =>>"+response);
//                rlLoader.setVisibility(View.GONE);
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    Log.i("register","response=>"+jsonObject);
//                    if (jsonObject.getString("status").equals("true")){
//                        openCustomSuccessDialog(jsonObject.getString("message"));
//                        clear();
//                    }else {
//                        dialogMsg=jsonObject.getString("message");
//                        openCustomFailedDialog(dialogMsg);
//                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
//                            if (SharedPreference.contains("uuid")) {
//                                SharedPreference.removeKey("uuid");
//                                SharedPreference.removeKey("name");
//                                SharedPreference.removeKey("referral_code");
//                            }
//                            Intent i = new Intent(getActivity(), LoginActivity.class);
//                            startActivity(i);
//                            getActivity().finish();
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    rlLoader.setVisibility(View.GONE);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                rlLoader.setVisibility(View.GONE);
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params=new HashMap<>();
//                params.put("uuid",uuid);
//                params.put("name",name);
//                params.put("email",email);
//                params.put("phone",phone);
//                params.put("address",address);
//                params.put("work_description",desc);
//
//                Log.i("pri","params=>"+params);
//
//                return  params;
//            }
//        };
//        AppController.getInstance().add(request);
//    }
//
//    private void clear() {
//        edtFirstName.setText("");
//        edtEmail.setText("");
//        edtEmail.setText("");
//        edtAddress.setText("");
//        edtWorkDesc.setText("");
//        edtPhone.setText("");
//    }
//
//    private void openCustomFailedDialog(String dialogMsg) {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.popup_failed_design);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
//
//        TextView textViewYes = (TextView) dialog.findViewById(R.id.textViewYes);
//        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);
//
//        txtMsg.setText(dialogMsg);
//        textViewYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        // show the exit dialog
//        dialog.show();
//    }

//    private void openCustomSuccessDialog(String text) {
//
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.popup_success_design);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
//
//        TextView textViewYes = (TextView) dialog.findViewById(R.id.textViewYes);
//        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);
//
//        txtMsg.setText(text);
//
//        textViewYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        // show the exit dialog
//        dialog.show();
//    }
}