package com.pbt.raadrivers.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by BlackHat on 28/03/2017.
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    protected Set<ConnectivityReceiverListener> listeners;
    public static Boolean connected;

    public ConnectivityReceiver() {
        listeners = new HashSet<ConnectivityReceiverListener>();
        connected = null;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = manager.getActiveNetworkInfo();

        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            connected = false;
        } else {
            connected = false;
        }
        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for (ConnectivityReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(ConnectivityReceiverListener listener) {
        if (connected == null || listener == null) return;

        if (connected == true) listener.networkAvailable();
        else listener.networkUnavailable();
    }

    public void addListener(ConnectivityReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(ConnectivityReceiverListener l) {
        if(l != null && listeners != null) {
            listeners.remove(l);
        }
    }

    public interface ConnectivityReceiverListener {
        public void networkAvailable();

        public void networkUnavailable();
    }
}