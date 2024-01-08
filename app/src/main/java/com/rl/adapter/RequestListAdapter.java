package com.rl.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rl.fieldworker.R;
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
        holder.tvEmail.setText(requestList.getEmail());
        holder.tvAddress.setText(requestList.getAddress());
        holder.tvDate.setText(requestList.getDate());
        holder.tvDesc.setText(requestList.getWork_description());

        if (requestList.getStatus().equalsIgnoreCase("pending")){
            holder.tvStatus.setBackgroundResource(R.color.yellow);
            holder.tvStatus.setText(requestList.getStatus());
        } else if (requestList.getStatus().equalsIgnoreCase("reject")){
            holder.tvStatus.setBackgroundResource(R.color.red);
            holder.tvStatus.setText(requestList.getStatus());
        }else if (requestList.getStatus().equalsIgnoreCase("success")){
            if (requestList.getBill().equalsIgnoreCase("null")){
                holder.tvStatus.setBackgroundResource(R.color.green);
                holder.tvStatus.setText(requestList.getStatus());
            }else {
                holder.tvStatus.setBackgroundResource(R.color.green);
                holder.tvStatus.setText(requestList.getStatus());
                holder.tvBill.setVisibility(View.VISIBLE);
            }

        }

        holder.tvBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pdfUrl = "https:\\/\\/rlwork.in\\/api_gfhjfyRETfkmghTYudgnm\\/field_bill\\/KALASHKUBER NIDHI LIMITED incorporation certificate.pdf";
                String fileName = "abc.pdf";

                PdfDownloader.downloadPdf(context, pdfUrl, fileName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvName, tvEmail, tvAddress, tvDate,tvPhone,tvBill,tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvBill = itemView.findViewById(R.id.tvBill);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

}
