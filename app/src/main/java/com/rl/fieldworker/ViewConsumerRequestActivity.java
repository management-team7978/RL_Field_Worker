package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rl.adapter.ConsumerBillAdapter;
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
    String consumer_name="",consumer_phone="",consumer_location="",consumer_desc="",consumer_work_progress="",id="";
    TextView tvConsumerName,tvConsumerAddress,tvConsumerPhone,tvTextStatus,tvTextNoData;
    ImageView img_download,imgBack,imgSuccess;
    AppCompatButton btAccept,btReject;
    RecyclerView recyclerCustomerBill;
    RelativeLayout rlLoader;
    ArrayList<ConsumerBillList> consumerBillLists;
    ConsumerBillAdapter consumerBillAdapter;
    RelativeLayout rlNotFound;
    DownloadManager manager;
    String quote_url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consumer_request);
        AppController.initialize(getApplicationContext());
        SharedPreference.initialize(getApplicationContext());
        tvConsumerName=findViewById(R.id.tvConsumerName);
        tvConsumerAddress=findViewById(R.id.tvConsumerAddress);
        tvConsumerPhone=findViewById(R.id.tvConsumerPhone);
        img_download=findViewById(R.id.imgDownload);
        recyclerCustomerBill=findViewById(R.id.recyclerCustomerBill);
        rlLoader=findViewById(R.id.rlLoader);
        rlNotFound=findViewById(R.id.rlNotFound);
        imgBack=findViewById(R.id.imgBack);
        tvTextStatus=findViewById(R.id.tvTextStatus);
        tvTextNoData=findViewById(R.id.tvTextNoData);


        recyclerCustomerBill.setLayoutManager(new LinearLayoutManager(ViewConsumerRequestActivity.this));
        consumerBillLists=new ArrayList<ConsumerBillList>();
        recyclerCustomerBill.setHasFixedSize(true);


        Intent i = getIntent();
        if (i.hasExtra("consumer_serial")) {
            consumer_name = i.getStringExtra("consumer_name");
            consumer_phone = i.getStringExtra("consumer_phone");
            consumer_location = i.getStringExtra("consumer_location");
            consumer_desc = i.getStringExtra("consumer_desc");
            consumer_work_progress = i.getStringExtra("consumer_work_progress");
            id = i.getStringExtra("id");

            tvConsumerName.setText(consumer_name);
            tvConsumerPhone.setText(consumer_phone);

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
                if (quote_url.equals("")){
                    Toast.makeText(ViewConsumerRequestActivity.this, "Quotation is missing", Toast.LENGTH_SHORT).show();
                }else {
                    manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(quote_url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle("Quotation");
                    request.setDescription("Download in progress");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.png");
                    request.setMimeType("image/png");
                    long reference = manager.enqueue(request);
                }
            }
        });

        getCustomerBill(SharedPreference.get("customer_uuid"),id);
    }

    private void getCustomerBill(String consumerUuid, String service_id) {
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
                Toast.makeText(ViewConsumerRequestActivity.this, "Technical problem arises", Toast.LENGTH_SHORT).show();
                //swipeRefresh.setRefreshing(false);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("uuid",consumerUuid);
                params.put("service_request_id",service_id);
                Log.i("pri","dashboa: "+params.toString());
                return  params;
            }
        };
        AppController.getInstance().add(request);

    }
}