package com.footprints.footprints.activities;

import android.app.ActivityOptions;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.controllers.test.NetworkSchedulerService;
import com.footprints.footprints.fragments.ExploreEvents;
import com.footprints.footprints.fragments.ExploreMemories;
import com.footprints.footprints.fragments.ExplorePlaces;
import com.footprints.footprints.models.User;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.UsersInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    private ExploreEvents exploreEvents;
    private ExploreMemories exploreMemories;
    private ExplorePlaces explorePlaces;

    Toolbar toolbar;
    private LayoutInflater layoutInflater;
    CircleImageView profieImage;
    public static String profileImage = "";
    BottomNavigationView bottomNavigationView;
    private ImageView notificationImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/

        setContentView(R.layout.activity_maps);
        frameLayout = findViewById(R.id.mainFramelayout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        exploreEvents = new ExploreEvents();
        exploreMemories = new ExploreMemories();
        explorePlaces = new ExplorePlaces();



        Log.d("TokenId", FirebaseInstanceId.getInstance().getToken());

        toolbar = (Toolbar) findViewById(R.id.maps_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View custumBarLayout = layoutInflater.inflate(R.layout.custom_menu_layout, null);
        actionBar.setCustomView(custumBarLayout);
        profieImage = custumBarLayout.findViewById(R.id.profile_icon);

        notificationImageView = custumBarLayout.findViewById(R.id.notification_icon_top);
        notificationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this,NotificationActivity.class));
            }
        });

        getAccountBasicInfo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        profieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(profieImage, "shared");
                Intent chatIntent = new Intent(MapsActivity.this, ProfileActivity.class);
                ActivityOptions activityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this, pairs);
                }
                chatIntent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(chatIntent, activityOptions.toBundle());
                } else {
                    startActivity(chatIntent);
                }
            }
        });


        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(bottomNavigationView.getContext(), R.color.nav_item_colors));
        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigationView.getContext(), R.color.nav_item_colors));

        Bundle bundle = getIntent().getExtras();
        String isFromNotification = "false";
        if (bundle != null) {
            isFromNotification = getIntent().getExtras().getString("isNotification", "false");
            if (isFromNotification.equals("true")) {
               // setFragment(exploreNotification);
                // here is the explore_notification
             //   bottomNavigationView.getMenu().findItem(R.id.explore_notification).setChecked(true);
                startActivity(new Intent(MapsActivity.this,NotificationActivity.class));
            } else {
                setFragment(exploreMemories);
            }
        } else {
            setFragment(exploreMemories);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.explore_memories:
                                // bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);
                                setFragment(exploreMemories);
                                break;
                            case R.id.explore_places:
                                // bottomNavigationView.setItemBackgroundResource(R.color.colorAccent);
                                setFragment(explorePlaces);
                                break;

                            case R.id.explore_events:
                                // bottomNavigationView.setItemBackgroundResource(R.color.cardview_dark_background);
                                setFragment(exploreEvents);
                                break;
                          /*  case R.id.explore_notification:
                                // bottomNavigationView.setItemBackgroundResource(R.color.cardview_dark_background);
                                setFragment(exploreNotification);
                                break;*/
                        }
                        return true;
                    }
                });


       // scheduleJob();
    }

    private void getAccountBasicInfo(String uid) {
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", uid);
        Call<User> call = postInterface.getOwnInfo(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                if (response.body() != null) {


                    profileImage = ApiClient.BASE_URL + response.body().getProfileUrl();

                    Log.d("checkherrr", profileImage + " hy");
                    Log.d("checkherrr", "llk hy");
                    Picasso.with(MapsActivity.this).load(profileImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(profieImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(MapsActivity.this).load(profileImage).placeholder(R.drawable.img_default_user).into(profieImage);
                        }
                    });

                } else {
                    Log.d("checkherrr", "failed  hy");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("checkherrr", "failed  hy" + t.getMessage());
                Toast.makeText(MapsActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFramelayout, fragment);
        fragmentTransaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }
}
