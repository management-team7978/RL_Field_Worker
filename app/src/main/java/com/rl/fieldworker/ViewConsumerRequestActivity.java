package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rl.adapter.ConsumerBillAdapter;
import com.rl.network.NetworkChangeListener;
import com.rl.pojo.ConsumerBillList;
import com.rl.util.AppController;
import com.rl.util.Keys;
import com.rl.util.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewConsumerRequestActivity extends AppCompatActivity {
    String consumer_name="",consumer_phone="",id="",consumer_quotation="",consumer_quotation_path="",consumer_userId="",consumer_occupation;
    TextView tvConsumerName,tvConsumerAddress,tvConsumerPhone,tvTextNoData,tvFileName,tvOccupation,tvDesc;
    ImageView img_download,imgBack,imgSuccess;
    AppCompatButton btAccept,btReject;
    RecyclerView recyclerCustomerBill;
    RelativeLayout rlLoader;
    ArrayList<ConsumerBillList> consumerBillLists;
    ConsumerBillAdapter consumerBillAdapter;
    RelativeLayout rlNotFound,rlReview;
    DownloadManager manager;
    RatingBar ratingBar;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consumer_request);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        FirebaseAnalytics.getInstance(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        tvConsumerName=findViewById(R.id.tvConsumerName);
        tvConsumerAddress=findViewById(R.id.tvConsumerAddress);
        tvConsumerPhone=findViewById(R.id.tvConsumerPhone);
        img_download=findViewById(R.id.imgDownload);
        recyclerCustomerBill=findViewById(R.id.recyclerCustomerBill);
        rlLoader=findViewById(R.id.rlLoader);
        rlNotFound=findViewById(R.id.rlNotFound);
        imgBack=findViewById(R.id.imgBack);
        tvTextNoData=findViewById(R.id.tvTextNoData);
        tvFileName=findViewById(R.id.tvFileName);
        rlReview=findViewById(R.id.rlReview);
        tvOccupation=findViewById(R.id.tvOccupation);
        tvDesc=findViewById(R.id.tvDesc);
        ratingBar=findViewById(R.id.ratingBar);


        recyclerCustomerBill.setLayoutManager(new LinearLayoutManager(ViewConsumerRequestActivity.this));
        consumerBillLists=new ArrayList<ConsumerBillList>();
        recyclerCustomerBill.setHasFixedSize(true);


        Intent i = getIntent();
        if (i.hasExtra("consumer_serial")) {
            consumer_name = i.getStringExtra("consumer_name");
            consumer_phone = i.getStringExtra("consumer_phone");
            consumer_quotation_path = i.getStringExtra("consumer_quotation_path");
            consumer_quotation= i.getStringExtra("consumer_quotation");
            id = i.getStringExtra("id");
            consumer_userId=i.getStringExtra("consumer_userId");
            consumer_occupation=i.getStringExtra("consumer_occupation");

            tvConsumerName.setText(consumer_name);
            tvConsumerPhone.setText(consumer_phone);
            tvConsumerAddress.setText(consumer_userId);
            tvFileName.setText(consumer_quotation);

        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (consumer_quotation_path.equals("")){
                    Toast.makeText(ViewConsumerRequestActivity.this, "Quotation is missing", Toast.LENGTH_SHORT).show();
                }else {
                    manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(consumer_quotation_path);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(consumer_quotation);
                    request.setDescription("Download in progress");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.png");
                    request.setMimeType("image/png");
                    long reference = manager.enqueue(request);
                }
            }
        });

        getReview(SharedPreference.get("uuid"),consumer_userId,id);
        getCustomerBill(SharedPreference.get("uuid"),id);
    }

    private void getReview(String uuid, String consumer_userId, String id) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.get_review, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("pri","getReview =>>"+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")){

                        rlLoader.setVisibility(View.GONE);

                        rlReview.setVisibility(View.VISIBLE);
                        tvOccupation.setText(consumer_occupation);
                        tvDesc.setText(jsonObject.getString("description_consumer"));
                        ratingBar.setRating(Float.parseFloat(jsonObject.getString("rating")));

                    }else {
                        rlLoader.setVisibility(View.GONE);
                        rlReview.setVisibility(View.GONE);
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
                            }
                            Intent i = new Intent(ViewConsumerRequestActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    rlLoader.setVisibility(View.GONE);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
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
                params.put("consumer_user_id", consumer_userId);
                params.put("service_id", id);
                Log.i("priyu",""+params);
                return  params;
            }
        };
        AppController.getInstance().add(request);

    }

    private void getCustomerBill(String consumerUuid, String id) {
        rlLoader.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Keys.URL.show_bill, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //swipeRefresh.setRefreshing(false);
                rlLoader.setVisibility(View.GONE);
                Log.i("pri","customer bill"+response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")){
                        rlLoader.setVisibility(View.GONE);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            consumerBillLists.add(new ConsumerBillList(
                                    jsonObject1.getString("serial"),
                                    jsonObject1.getString("bill"),
                                    jsonObject1.getString("billing")));
                        }
                        consumerBillAdapter= new ConsumerBillAdapter(
                                ViewConsumerRequestActivity.this,consumerBillLists);
                        recyclerCustomerBill.setAdapter(consumerBillAdapter);
                    }else {
                        rlLoader.setVisibility(View.GONE);
                        rlNotFound.setVisibility(View.VISIBLE);
                        tvTextNoData.setText(jsonObject.getString("message"));
                        if (jsonObject.getString("message").equalsIgnoreCase("uuid missmatch logout")) {
                            if (SharedPreference.contains("uuid")) {
                                SharedPreference.removeKey("uuid");
                                SharedPreference.removeKey("name");
                                SharedPreference.removeKey("referral_code");
                            }
                            Intent i = new Intent(ViewConsumerRequestActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    rlLoader.setVisibility(View.GONE);
                }
                //swipeRefresh.setRefreshing(false);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                rlLoader.setVisibility(View.GONE);
                Toast.makeText(ViewConsumerRequestActivity.this, "Technical problem arises.", Toast.LENGTH_SHORT).show();
                //swipeRefresh.setRefreshing(false);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("uuid", SharedPreference.get("uuid"));
                params.put("service_id", id);
                Log.i("pri","dashboa: "+params.toString());
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