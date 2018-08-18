package com.footprints.footprints.controllers.test;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }


    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
/*

        if (isConnected) {
            if (SharedPreferenceController.getInOut(getApplicationContext())) {

                EventInterface eventInterface = ApiClient.getApiClient().create(EventInterface.class);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                LatLng latLng = SharedPreferenceController.getUserLocation(getApplicationContext());
                Call<Integer> call = eventInterface.checkNewEvent(new NewEventModel(userId, latLng.latitude, latLng.latitude));
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, @NonNull final Response<Integer> response) {
                        if (response.body() != null) {

                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                        Toast.makeText(getApplicationContext(), "Event Detection failed !", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
*/

    }


}