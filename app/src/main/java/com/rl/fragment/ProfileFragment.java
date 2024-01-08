package com.rl.fragment;

import android.content.Intent;
import android.os.Bundle;

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
    TextView tvName,tvEmail,tvPhone,tvAddress,tvPassword,tvChangePassword,tvUserId;
    RelativeLayout rlLogout;
    String uuid;
    RelativeLayout rlLoader;
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
        return v;
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