package com.footprints.footprints.controllers.test.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.controllers.SharedPreferenceController;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.EventInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsLocationReceiver extends BroadcastReceiver {
    private static boolean firstConnect = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction()== LocationManager.PROVIDERS_CHANGED_ACTION) {
            final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

            if (isGPSEnabled(context) ) {
                    if( firstConnect){
                        firstConnect=false;


                    /* //context.startActivity(new Intent(context,StartGpsWork.class));

                        Intent startServiceIntent = new Intent(context, NetworkSchedulerService.class);
                        context.startService(startServiceIntent);

                        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(context, GpsLocationReceiver.class))
                                .setRequiresCharging(true)
                                .setMinimumLatency(1000)
                                .setOverrideDeadline(2000)
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .setPersisted(true)
                                .build();

                        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        jobScheduler.schedule(myJob);*/


                        if (NetworkDetectController.checkConnection(context)) {

                          //  Toast.makeText(context, "All set Ready to go", Toast.LENGTH_LONG).show();
                                EventInterface eventInterface = ApiClient.getApiClient().create(EventInterface.class);
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                LatLng latLng = SharedPreferenceController.getUserLocation(context);
                                Call<Integer> call = eventInterface.checkNewEvent(new NewEventModel(userId, latLng.latitude, latLng.latitude));
                                call.enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Integer> call, @NonNull final Response<Integer> response) {
                                        if (response.body() != null) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {

                                        Toast.makeText(context, "Event Detection failed !", Toast.LENGTH_LONG).show();
                                    }
                                });

                        }
                    }

            }else{

                if(!firstConnect){

                    firstConnect=true;
                }
            }
        }
    }

    public boolean isGPSEnabled(Context mContext)
    {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public class NewEventModel {
        String uid;
        double lattitude;
        double longnitude;

        public NewEventModel(String uid, double lattitude, double longnitude) {
            this.uid = uid;
            this.lattitude = lattitude;
            this.longnitude = longnitude;
        }
    }


}