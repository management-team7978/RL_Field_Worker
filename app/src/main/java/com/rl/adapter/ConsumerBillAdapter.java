package com.rl.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rl.fieldworker.R;
import com.rl.pojo.ConsumerBillList;
import java.util.ArrayList;

public class ConsumerBillAdapter extends RecyclerView.Adapter<ConsumerBillAdapter.ViewHolder>{
    Context context;
    ArrayList<ConsumerBillList> consumerBillLists;
    DownloadManager manager;

    public ConsumerBillAdapter(Context context, ArrayList<ConsumerBillList> consumerBillLists) {
        this.context = context;
        this.consumerBillLists = consumerBillLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView  = layoutInflater.inflate(R.layout.custom_consumer_bill_layout, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsumerBillList consumerBillList =  consumerBillLists.get(position);
        holder.tvBillName.setText(consumerBillList.getBill());

        holder.tvBillDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(consumerBillList.getBilling());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Bill");
                request.setDescription("Download in progress");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Receipt.pdf");
                request.setMimeType("application/pdf");
                long reference = manager.enqueue(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consumerBillLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillName,tvBillDownload;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillName=itemView.findViewById(R.id.tvBillName);
            tvBillDownload=itemView.findViewById(R.id.tvBillDownload);
        }
    }
}
