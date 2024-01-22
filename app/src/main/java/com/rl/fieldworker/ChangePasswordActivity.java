package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtOldPass,edtNewPass,edtConfirmPass;
    TextView tvSubmit;
    String uuid;
    RelativeLayout rlLoader;
    String dialogMsg = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());

        edtConfirmPass=findViewById(R.id.edtConfirmPass);
        edtOldPass=findViewById(R.id.edtOldPass);
        edtNewPass=findViewById(R.id.edtNewPass);
        tvSubmit=findViewById(R.id.tvSubmit);
        uuid= SharedPreference.get("uuid");
        rlLoader=findViewById(R.id.rlLoader);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass=edtOldPass.getText().toString().trim();
                String newPass=edtNewPass.getText().toString().trim();
                String confirmPass=edtConfirmPass.getText().toString().trim();

                if (oldPass.equals("")||newPass.equals("")||confirmPass.equals("")){
                    Toast.makeText(ChangePasswordActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    userChangePassword(uuid,oldPass,newPass,confirmPass);
                }

            }
        });
    }

    private void userChangePassword(String uuid, String oldPass, String newPass, String confirmPass) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.change_password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Change Pass","changePass =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("Change Pass","response=>"+jsonObject);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        dialogMsg=jsonObject.getString("message");
                        openCustomSucessDialog(dialogMsg);

                        if (SharedPreference.contains("consumer_uuid")) {
                            SharedPreference.removeKey("consumer_uuid");
                            SharedPreference.removeKey("userid");
                        }
                        Intent intent=new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }else {
                        rlLoader.setVisibility(View.GONE);
                        dialogMsg=jsonObject.getString("message");
                        openCustomFailedDialog(dialogMsg);

                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("userid");
                            }
                            Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
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
                params.put("uuid",uuid);
                params.put("oldpassword",oldPass);
                params.put("newpassword",newPass);
                params.put("confirmpassword",confirmPass);

                Log.i("change pass","uuid=>"+uuid+" old_password=>"+oldPass+" confirm_password=>"+confirmPass+" new_password=>"+newPass);

                return  params;
            }
        };
        AppController.getInstance().add(request);
    }
    private void openCustomFailedDialog(String dialogMsg) {
        final Dialog dialog = new Dialog(ChangePasswordActivity.this);
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

    private void openCustomSucessDialog(String text) {

        final Dialog dialog = new Dialog(ChangePasswordActivity.this);
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