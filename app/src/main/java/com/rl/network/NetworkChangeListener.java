package com.rl.network;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.rl.fieldworker.R;


public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetworkCommon.isConnectedToInternet(context)) {   // internret is not connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);

            View dailog = LayoutInflater.from(context).inflate(R.layout.custom_no_data_found_layout, null);
            builder.setView(dailog);

            AppCompatTextView btnRetry = dailog.findViewById(R.id.btRetry);


            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            //alertDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    onReceive(context, intent);
                }
            });

        }

    }
}
