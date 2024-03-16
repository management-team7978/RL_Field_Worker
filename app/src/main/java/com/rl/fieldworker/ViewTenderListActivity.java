package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rl.adapter.TenderPdfAdapter;
import com.rl.network.NetworkChangeListener;
import com.rl.util.SharedPreference;

import java.util.ArrayList;

public class ViewTenderListActivity extends AppCompatActivity {
    String tender_no,name,mobile,occupation,date,address,tender_id,getConsumer_mob_no,Tender_accepted_consumer_id,consumer_Name;
    ArrayList<String> tenderPdfList = new ArrayList<>();
    TextView tvConsumerName,tvConsumerPhone,tvConsumerAddress,tvConsumerOccupation,tvTenderNumber,tvConName,tvConPhone,tvConId;
    RecyclerView recyclerTenderPdf;
    TenderPdfAdapter tenderPdfAdapter;
    ImageView imgBack;
    RelativeLayout rlLoader,rlNotFound;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    CardView cdConsumerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tender_list);

        tvConsumerName=findViewById(R.id.tvConsumerName);
        tvConsumerPhone=findViewById(R.id.tvConsumerPhone);
        tvConsumerAddress=findViewById(R.id.tvConsumerAddress);
        tvConsumerOccupation=findViewById(R.id.tvConsumerOccupation);
        tvTenderNumber=findViewById(R.id.tvTenderNumber);
        recyclerTenderPdf=findViewById(R.id.recyclerTenderPdf);
        imgBack=findViewById(R.id.imgBack);
        tvConName=findViewById(R.id.tvConName);
        tvConPhone=findViewById(R.id.tvConPhone);
        cdConsumerDetails=findViewById(R.id.cdConsumerDetails);
        tvConId=findViewById(R.id.tvConId);

        rlLoader=findViewById(R.id.rlLoader);
        rlNotFound=findViewById(R.id.rlNotFound);
        //tvKYCStatus=findViewById(R.id.tvKYCStatus);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i =getIntent();
        if (i.hasExtra("serial")){
            tender_no=i.getStringExtra("tender_no");
            name=i.getStringExtra("name");
            mobile=i.getStringExtra("mobile");
            occupation=i.getStringExtra("occupation");
            date=i.getStringExtra("date");
            address=i.getStringExtra("address");
            tenderPdfList=i.getStringArrayListExtra("tender_pdf");
            tender_id=i.getStringExtra("id");

            tvConsumerAddress.setText(address);
            tvConsumerOccupation.setText(occupation);
            tvConsumerName.setText(name);
            tvConsumerPhone.setText(mobile);
            tvTenderNumber.setText(tender_no);

            getConsumer_mob_no=i.getStringExtra("getConsumer_mob_no");
            Tender_accepted_consumer_id=i.getStringExtra("Tender_accepted_consumer_id");
            consumer_Name=i.getStringExtra("consumer_Name");

            if (Tender_accepted_consumer_id.equals("0")){
                cdConsumerDetails.setVisibility(View.GONE);
            }else {
                tvConName.setText(consumer_Name);
                tvConPhone.setText(getConsumer_mob_no);
                tvConId.setText(Tender_accepted_consumer_id);
            }
            Log.i("pri","tt=>"+tender_no);
        }


        recyclerTenderPdf.setLayoutManager(new LinearLayoutManager(ViewTenderListActivity.this));
        recyclerTenderPdf.setHasFixedSize(true);

        tenderPdfAdapter = new TenderPdfAdapter(tenderPdfList);
        recyclerTenderPdf.setAdapter(tenderPdfAdapter);

        // Check if tenderPdfList is empty and show rlNotFound layout if true
        if (tenderPdfList.isEmpty()) {
            rlNotFound.setVisibility(View.VISIBLE);
        } else {
            rlNotFound.setVisibility(View.GONE);
        }

       // getTenderDetails();
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