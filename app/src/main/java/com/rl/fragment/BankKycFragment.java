package com.rl.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class BankKycFragment extends Fragment {

    EditText edtBankName,edtHolderName,edtAccountNumber,edtIfsc;
    Button btSubmit;
    RelativeLayout rlLoader;
    String dialogMsg="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bank_kyc, container, false);
        edtBankName=v.findViewById(R.id.edtBankName);
        edtHolderName=v.findViewById(R.id.edtHolderName);
        edtAccountNumber=v.findViewById(R.id.edtAccountNumber);
        edtIfsc=v.findViewById(R.id.edtIfsc);

        btSubmit=v.findViewById(R.id.btSubmit);
        rlLoader=v.findViewById(R.id.rlLoader);

        getBankKyc(SharedPreference.get("uuid"));

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bName = edtBankName.getText().toString().trim();
                String bHolderName = edtHolderName.getText().toString().trim();
                String bAccNum = edtAccountNumber.getText().toString().trim();
                String ifsc = edtIfsc.getText().toString().trim();


                if (bName.isEmpty() || bHolderName.isEmpty() || bAccNum.isEmpty() || ifsc.isEmpty()) {
                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    AddBankDetails(SharedPreference.get("uuid"),bName,bHolderName,bAccNum,ifsc);
                }
            }
        });
        return v;
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
                        edtAccountNumber.setText(jsonObject.getString("account_number"));
                        edtBankName.setText(jsonObject.getString("bank_name"));
                        edtHolderName.setText(jsonObject.getString("holder_name"));
                        edtIfsc.setText(jsonObject.getString("ifsc_code"));

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

    private void AddBankDetails(String uuid, String bName, String bHolderName, String bAccNum, String ifsc) {
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

                Log.i("pri","params=>"+params);

                return  params;
            }
        };
        AppController.getInstance().add(request);
    }

    private void openCustomFailedDialog(String dialogMsg) {
        final Dialog dialog = new Dialog(getActivity());
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

    private void openCustomSuccessDialog(String text) {

        final Dialog dialog = new Dialog(getActivity());
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
}