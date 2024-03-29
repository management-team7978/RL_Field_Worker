package com.rl.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        holder.tvPhone.setText(requestList.getPhone()+" (Call)");
        holder.tvUserId.setText(requestList.getConsumer_user_id());
        holder.tvOccupation.setText(requestList.getOccupation());
        holder.tvWorkStatus.setText(requestList.getWork_status());
        holder.tvAddress.setText(requestList.getAddress());

        if (requestList.getStatus_code().equals("1")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.rl_orange));
        }else if (requestList.getStatus_code().equals("2")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }else if (requestList.getStatus_code().equals("3")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }else if (requestList.getStatus_code().equals("4")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
        }
        else if (requestList.getStatus_code().equals("5")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
        }else if (requestList.getStatus_code().equals("6")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
        }
        else if (requestList.getStatus_code().equals("7")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        else if (requestList.getStatus_code().equals("8")){
            holder.tvWorkStatus.setText(requestList.getStatus_message());
            holder.tvWorkStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
        
        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewConsumerRequestActivity.class);
               // i.putExtra("id",requestList.getService_request_id());
                i.putExtra("consumer_serial",requestList.getSerial());
                i.putExtra("consumer_name",requestList.getName());
                i.putExtra("consumer_userId",requestList.getConsumer_user_id());
                i.putExtra("consumer_phone",requestList.getPhone());
                i.putExtra("consumer_quotation",requestList.getQuotation());
                i.putExtra("consumer_quotation_path",requestList.getQuotations());
                i.putExtra("consumer_occupation",requestList.getOccupation());
                i.putExtra("consumer_address",requestList.getAddress());
                i.putExtra("id",requestList.getId());
                context.startActivity(i);
            }
        });

        holder.tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the phone number from the TextView
                String phone_number = holder.tvPhone.getText().toString().trim();

                // Create a dialog box before making the call
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Call");
                builder.setMessage("Do you want to call " + phone_number + "?");

                // Set positive button for the user to confirm the call
                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed the call, initiate the call
                        Intent phone_intent = new Intent(Intent.ACTION_CALL);
                        phone_intent.setData(Uri.parse("tel:" + phone_number));

                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(phone_intent);
                        } else {
                            Toast.makeText(context, "Call permission not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled the call, do nothing
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                builder.show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return requestLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvName,tvPhone,tvUserId,tvViewMore,tvOccupation,tvWorkStatus,tvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvViewMore = itemView.findViewById(R.id.tvViewMore);
            tvOccupation=itemView.findViewById(R.id.tvOccupation);
            tvWorkStatus=itemView.findViewById(R.id.tvWorkStatus);
            tvAddress=itemView.findViewById(R.id.tvAddress);
        }
    }

}
