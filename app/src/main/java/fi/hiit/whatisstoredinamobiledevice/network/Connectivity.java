package fi.hiit.whatisstoredinamobiledevice.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connectivity {
    Context context;
    NetworkInfo activeNetwork;


    public Connectivity(Context context) {
        this.context = context;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
    }

    public boolean isConnected() {
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public boolean isWifi() {
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
