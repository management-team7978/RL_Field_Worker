package com.rl.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.rl.fieldworker.R;
import com.rl.fieldworker.ViewTenderListActivity;
import com.rl.pojo.TenderList;

import java.util.ArrayList;

public class ConsumerTenderListAdapter extends RecyclerView.Adapter<ConsumerTenderListAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<TenderList> tenderLists;
    ArrayList<TenderList> orig;
    int lastposition = -1;

    DownloadManager manager;
    PermissionListener permissionlistener;


    public ConsumerTenderListAdapter(Context context, ArrayList<TenderList> tenderLists) {
        this.context = context;
        this.tenderLists = tenderLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.custom_tender_list_layout, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenderList tenderList = tenderLists.get(position);
        holder.tvName.setText(tenderList.getName());
        holder.tvPhone.setText(tenderList.getMobile());
        holder.tvOccupation.setText(tenderList.getOccupation());
        holder.tvAddress.setText(tenderList.getWorkplace_address()+", "+tenderList.getPincode());
        holder.tvDate.setText(tenderList.getDate());
        holder.tvEmail.setText(tenderList.getEmail_id());

//        if (holder.getAdapterPosition() > lastposition) {
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
//            ((ViewHolder) holder).itemView.startAnimation(animation);
//            lastposition = holder.getAdapterPosition();
//        }

        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewTenderListActivity.class);
                i.putExtra("serial", tenderList.getSerial());
                i.putExtra("tender_no", tenderList.getTender_no());
                i.putExtra("name", tenderList.getName());
                i.putExtra("mobile", tenderList.getMobile());
                i.putExtra("occupation", tenderList.getOccupation());
                i.putExtra("date", tenderList.getDate());
                i.putExtra("id", tenderList.getId());
                i.putExtra("address", tenderList.getWorkplace_address() + " " + tenderList.getPincode());
                i.putStringArrayListExtra("tender_pdf", tenderList.getTender_pdf());
                Log.i("pri","tt=>"+tenderList.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tenderLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvOccupation, tvAddress, tvDate,tvViewMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvOccupation = itemView.findViewById(R.id.tvOccupation);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvViewMore = itemView.findViewById(R.id.tvViewMore);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i("nik", constraint + "");

                final FilterResults oReturn = new FilterResults();
                final ArrayList<TenderList> results = new ArrayList<TenderList>();
                if (orig == null)
                    orig = tenderLists;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {

                        for (final TenderList g : orig) {
                            if (g.getTender_no().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                results.add(g);
                                Log.i("nik", g + "");
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                tenderLists = (ArrayList<TenderList>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        //here clear animation
        super.onViewDetachedFromWindow(holder);
        ((ViewHolder) holder).itemView.clearAnimation();
    }

}
