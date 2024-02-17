package com.rl.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class NetworkCommon {
    public static boolean isConnectedToInternet(Context context){
        boolean hasInternet=false;
        ConnectivityManager connectivityManager= (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                            return true;
                        Log.i("abc","lower");
                    }
                }
            }

        }else {
            Network[] networks = connectivityManager.getAllNetworks();
            if(networks.length>0){
                for(Network network :networks){
                    NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);
                    if(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) hasInternet = true;
                    Log.i("abc","higher");
                }
            }
        }

        return hasInternet;
    }
}
