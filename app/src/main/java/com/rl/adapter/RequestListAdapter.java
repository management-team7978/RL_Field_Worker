package com.rl.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rl.fieldworker.R;
import com.rl.fieldworker.ViewConsumerRequestActivity;
import com.rl.pojo.RequestList;
import com.rl.util.PdfDownloader;

import java.util.ArrayList;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Activity context;
    private ArrayList<RequestList> requestLists;
    private ArrayList<RequestList> orig;
    private int lastPosition = -1;
    DownloadManager manager;

    public RequestListAdapter(Activity context, ArrayList<RequestList> requestLists) {
        this.context = context;
        this.requestLists = requestLists;
    }

    @NonNull
    @Override
    public RequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.custom_request_list_layout, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestListAdapter.ViewHolder holder, int position) {
        RequestList requestList = requestLists.get(position);
        holder.tvName.setText(requestList.getName());
        holder.tvPhone.setText(requestList.getPhone());
        holder.tvUserId.setText(requestList.getConsumer_user_id());

        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewConsumerRequestActivity.class);
                i.putExtra("id",requestList.getService_request_id());
                i.putExtra("consumer_serial",requestList.getSerial());
                i.putExtra("consumer_name",requestList.getConsumer_user_id());
                i.putExtra("consumer_phone",requestList.getPhone());
                i.putExtra("consumer_quotation",requestList.getQuotation());
                i.putExtra("consumer_quotation_path",requestList.getQuotations());

                context.startActivity(i);
            }
        });

    }



    @Override
    public int getItemCount() {
        return requestLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvName,tvPhone,tvUserId,tvViewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvViewMore = itemView.findViewById(R.id.tvViewMore);

        }
    }

}
