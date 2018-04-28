package com.footprints.footprints.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.PostInterface;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadMemories extends AppCompatActivity {

    LayoutInflater layoutInflater;
    private Toolbar toolbar;
    TextView postBtnTxt;
    EditText postEdtTxt;
    String name,latitude,longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        setContentView(R.layout.activity_upload_memories);
        toolbar = (Toolbar) findViewById(R.id.prepareStatusToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View custumBarLayout = layoutInflater.inflate(R.layout.toolbar_layout_preparestatus_activity, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(custumBarLayout);

        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

// init the bottom sheet behavior
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

// change the state of the bottom sheet
        //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// set the peek height
        bottomSheetBehavior.setPeekHeight((int) ControllerPixels.convertDpToPixel(50.0f, UploadMemories.this));


// set hideable or not
        bottomSheetBehavior.setHideable(false);

// set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        TextView textView = findViewById(R.id.bottom_sheet_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        postBtnTxt = custumBarLayout.findViewById(R.id.postBtnTxt);
        postEdtTxt = findViewById(R.id.status_edit);
        postBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadMemories.this,"Upload AddReviews",Toast.LENGTH_SHORT).show();
                String status = postEdtTxt.getText().toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Status postStatus = new Status(status, userId,"",latitude,longitude,0,0,0,name);
                // Now Store the data in the mysql Database
                PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
                Call<Integer> call = postInterface.postMemories(postStatus);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                        if(response.body()==1){
                            Intent intent = new Intent(UploadMemories.this,PlaceActivity.class);
                            intent.putExtra("name",name);
                            intent.putExtra("latitude",latitude+"");
                            intent.putExtra("longitude",longitude+"");
                            startActivity(intent);

                            Toast.makeText(UploadMemories.this,"Upload Successful ",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(UploadMemories.this,"Upload Failed ",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(UploadMemories.this,"Upload Failed... Please Retry !",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    public class Status{
        String post;
        String postUserId;
        String statusImage;
        String lattitude;
        String longnitude;
        String name;
        int privacy;
        int hasComment;
        int likeCount;


        public Status(String post, String postUserId, String statusImage, String lattitude, String longnitude, int privacy, int hasComment, int likeCount,String name) {
            this.post = post;
            this.postUserId = postUserId;
            this.statusImage = statusImage;
            this.lattitude = lattitude;
            this.longnitude = longnitude;
            this.privacy = privacy;
            this.hasComment = hasComment;
            this.likeCount = likeCount;
            this.name = name;
        }
    }
}
