package com.footprints.footprints.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.footprints.footprints.controllers.NetworkDetectController.checkConnection;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
      //  Toast.makeText(context, "Network Available Do operations",Toast.LENGTH_LONG).show();

        if(checkConnection(context))
        {
            Toast.makeText(context, "Network Available Do operations",Toast.LENGTH_LONG).show();
        }

    }


}
