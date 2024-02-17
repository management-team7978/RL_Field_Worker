package com.rl.adapter;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.rl.fieldworker.R;

import java.util.ArrayList;

public class TenderPdfAdapter extends RecyclerView.Adapter<TenderPdfAdapter.ViewHolder> {
    private ArrayList<String> tenderPdfList;

    public TenderPdfAdapter(ArrayList<String> tenderPdfList) {
        this.tenderPdfList = tenderPdfList;
    }

//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tender_pdf_list_layout, parent, false);
//        return new ViewHolder(view);
//
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View itemView  = layoutInflater.inflate(R.layout.custom_service_list_layout, parent, false);
//        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
//        return new CustomerServiceAdapter.ViewHolder(itemView);
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView  = layoutInflater.inflate(R.layout.custom_tender_pdf_list_layout, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tenderPdfName = tenderPdfList.get(position);

        String fileName = tenderPdfName.substring(tenderPdfName.lastIndexOf('/') + 1);

        holder.tvTenderPdfName.setText(fileName);
                holder.imgPdfDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quotation_path = tenderPdfName;
                if (quotation_path.equals("")){
                    Toast.makeText(v.getContext(), "Quotation is missing", Toast.LENGTH_SHORT).show();
                }else {
                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(quotation_path);
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
        //holder.tvTenderPdfName.setText(tenderPdfName);
    }

    @Override
    public int getItemCount() {
        return tenderPdfList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenderPdfName;
        ImageView imgPdfDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenderPdfName = itemView.findViewById(R.id.tvTenderPdfName);
            imgPdfDownload = itemView.findViewById(R.id.imgPdfDownload);
        }
    }
}
