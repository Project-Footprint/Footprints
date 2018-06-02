package com.footprints.footprints.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.adapter.NotificationAdapter;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.models.Notification;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.UsersInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notificationRecyclerView;
    NotificationAdapter notificationAdapter;
    ArrayList<Notification> notifications = new ArrayList<>();
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecyclerView = findViewById(R.id.notification_recy);
        notificationRecyclerView.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        notificationAdapter = new NotificationAdapter(notifications, NotificationActivity.this);

        toolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationActivity.this, MapsActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getNotification();
    }

    private void getNotification() {
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<List<Notification>> call = postInterface.getNotification(params);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.body() != null) {

                    notifications.addAll(response.body());
                    notificationRecyclerView.setAdapter(notificationAdapter);
                    Log.d("CheckZie",response.body().size()+" ");

                }


            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Notification Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this, MapsActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        notifications.clear();
        notificationAdapter.notifyDataSetChanged();
    }
}
