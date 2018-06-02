package com.footprints.footprints.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.footprints.footprints.R;
import com.footprints.footprints.adapter.ProfileViewPagerAdapter;
import com.footprints.footprints.models.User;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.UsersInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, DialogInterface.OnDismissListener {
    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ProfileViewPagerAdapter profileViewPagerAdapter;
    public static String uid = "1";
    private int[] tabIcons = {
            R.drawable.icon_posts,
            R.drawable.icon_image,
            R.drawable.icon_checkin
    };
    private String[] titles = {
            "Posts",
            "Photos",
            "Check In"

    };
    CircleImageView circleImageView;
    ImageView coverPicture;
    Button pofile_option_btn;
    ProgressDialog progressDialog;
    private static int imageUploadType = 0;
    String profileUrl = "", coverUrl = "";
    CollapsingToolbarLayout collapsingToolbarLayout = null;
       /*

    0 = profile is still loading
    1=  two people are friends ( unfriend )
    2 = this person has sent friend request to another friend ( cancel sent requeset )
    3 = this person has received friend request from another friend  (  reject or accept request )
    4 = people are unkown ( you can send requeset )
    5 = own profile
     */

    int current_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_profile);
        uid = getIntent().getStringExtra("uid");

        circleImageView = findViewById(R.id.profile_image);
        coverPicture = findViewById(R.id.profile_cover);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MapsActivity.class));
            }
        });

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.profileExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


        mViewPager = findViewById(R.id.ViewPager_profile);
        mTabLayout = findViewById(R.id.profile_tab_profile);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 2, this);
        mViewPager.setAdapter(profileViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mTabLayout.setupWithViewPager(mViewPager);
        pofile_option_btn = findViewById(R.id.pofile_option_btn);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            current_state = 5;
            pofile_option_btn.setText("Edit Profile");
            loadOwnProfile();
        } else {
            loadOthersProfile();
        }
        TextView newTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_menu_tablayout, null);

       /* for (int i = 0; i < tabIcons.length; i++) {


            mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }*/
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");



        coverPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullCover();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFulllProfile();
            }
        });



            pofile_option_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pofile_option_btn.setEnabled(false);

                    if(current_state==1){
                        pofile_option_btn.setText("Processing...");
                        CharSequence options[] = new CharSequence[]{"UnFriend", "Message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setOnDismissListener(ProfileActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) {

                                   performAction(1);
                                } else if (i == 1) {

                                    performAction(1);
                                }

                            }
                        });
                        builder.show();

                    }else if(current_state==2){
                        pofile_option_btn.setText("Processing...");
                        CharSequence options[] = new CharSequence[]{"Cancel Request"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setOnDismissListener(ProfileActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) {
                                    performAction(2);

                                }

                            }
                        });
                        builder.show();
                    }else if(current_state==3){
                        pofile_option_btn.setText("Processing...");
                        CharSequence options[] = new CharSequence[]{"Accept Request","Ignore Request"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setOnDismissListener(ProfileActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) {
                                    performAction(3);

                                }else if(i==1){


                                }

                            }
                        });
                        builder.show();
                    }else if(current_state==4){
                        pofile_option_btn.setText("Processing...");
                        CharSequence options[] = new CharSequence[]{"Send Request"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setOnDismissListener(ProfileActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) {
                                    performAction(4);

                                }

                            }
                        });
                        builder.show();
                    }else if(current_state==5){
                        CharSequence options[] = new CharSequence[]{"Change Cover Picture", "Change Profile Picture", "View Cover Picture", "View Profile Picture"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setOnDismissListener(ProfileActivity.this);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    imageUploadType = 0;
                                    ImagePicker.create(ProfileActivity.this)
                                            .folderMode(true)
                                            .single().toolbarFolderTitle("Select Cover Picture ")
                                            .start();
                                } else if (i == 1) {
                                    imageUploadType = 1;
                                    ImagePicker.create(ProfileActivity.this)
                                            .folderMode(true)
                                            .single().toolbarFolderTitle("Select Profile Picture ")
                                            .start();
                                } else if (i == 2) {
                                    viewFullCover();
                                } else if (i == 3) {
                                    viewFulllProfile();
                                }

                            }
                        });
                        builder.show();


                    }

                }
            });

    }

    private void performAction(final int i) {
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;

        Call<Integer> call = postInterface.performBtnAction(new PeformActoin(i+"",userId,uid));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull final Response<Integer> response) {
                if (response.body() != null) {
                    pofile_option_btn.setEnabled(true);
                    if(response.body()==1){
                        if(i==1){
                            current_state = 4;
                            pofile_option_btn.setText("Send Request");
                            Toast.makeText(ProfileActivity.this,"Unfriend Successful",Toast.LENGTH_SHORT).show();
                        }else if(i==2){
                            current_state = 4;
                            pofile_option_btn.setText("Send Request");
                            Toast.makeText(ProfileActivity.this,"Request Cancelled Successful",Toast.LENGTH_SHORT).show();
                        }else if(i==3){
                            current_state = 1;
                            pofile_option_btn.setText("You are Friends");
                            Toast.makeText(ProfileActivity.this,"You are now Friends on Footprints",Toast.LENGTH_SHORT).show();
                        }else if(i==4){
                            current_state = 2;
                            pofile_option_btn.setText("Request Sent");
                            Toast.makeText(ProfileActivity.this,"Request Sent Successful",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        pofile_option_btn.setEnabled(false);
                        pofile_option_btn.setText("Error...");
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

                Toast.makeText(ProfileActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadOthersProfile() {
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        params.put("profileId", uid);
        Call<User> call = postInterface.getOthersInfo(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                if (response.body() != null) {


                    profileUrl = ApiClient.BASE_URL + response.body().getProfileUrl();
                    coverUrl = ApiClient.BASE_URL + response.body().getCoverUrl();
                    collapsingToolbarLayout.setTitle(response.body().getName());
                    current_state  = Integer.parseInt(response.body().getState());

                    Picasso.with(ProfileActivity.this).load(profileUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(circleImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(profileUrl).placeholder(R.drawable.img_default_user).into(circleImageView);
                        }
                    });

                    Picasso.with(ProfileActivity.this).load(coverUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(coverPicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(coverUrl).placeholder(R.drawable.default_image_placeholder).into(coverPicture);
                        }
                    });


                    if(response.body().getState().equals("1")){
                        current_state=1;
                        pofile_option_btn.setText("You are Friends");
                    }else if(response.body().getState().equals("2")){
                        current_state=2;
                        pofile_option_btn.setText("Cancel Request");
                    }else if(response.body().getState().equals("3")){
                        current_state=3;
                        pofile_option_btn.setText("Accept Request");
                    }else if(response.body().getState().equals("4")){
                        current_state=4;
                        pofile_option_btn.setText("Send Request");
                    }else if(response.body().getState().equals("5")) {
                        current_state=5;
                        pofile_option_btn.setText("Edit Profile");
                    }else{
                        current_state=0;
                        pofile_option_btn.setText("State");
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("checkherrr", "failed  hy" + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void viewFullCover() {
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(coverPicture, "shared");
        Intent chatIntent = new Intent(ProfileActivity.this, FullmageActivity.class);
        ActivityOptions activityOptions = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
        }
        chatIntent.putExtra("imageUrl", coverUrl);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivity(chatIntent, activityOptions.toBundle());
        } else {
            startActivity(chatIntent);
        }
    }

    private void viewFulllProfile() {
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(circleImageView, "shared");
        Intent chatIntent = new Intent(ProfileActivity.this, FullmageActivity.class);
        ActivityOptions activityOptions = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
        }
        chatIntent.putExtra("imageUrl", profileUrl);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivity(chatIntent, activityOptions.toBundle());
        } else {
            startActivity(chatIntent);
        }
    }

    private void loadOwnProfile() {
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", uid);
        Call<User> call = postInterface.getOwnInfo(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                if (response.body() != null) {


                    profileUrl = ApiClient.BASE_URL + response.body().getProfileUrl();
                    coverUrl = ApiClient.BASE_URL + response.body().getCoverUrl();
                    collapsingToolbarLayout.setTitle(response.body().getName());

                    Picasso.with(ProfileActivity.this).load(profileUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(circleImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(profileUrl).placeholder(R.drawable.img_default_user).into(circleImageView);
                        }
                    });

                    Picasso.with(ProfileActivity.this).load(coverUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(coverPicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(coverUrl).placeholder(R.drawable.default_image_placeholder).into(coverPicture);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("checkherrr", "failed  hy" + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            Image image = ImagePicker.getFirstImageOrNull(data);

            if (image != null) {
                progressDialog.show();
                try {
                    File compressedImageFile = new Compressor(this)
                            .setMaxHeight(350)
                            .setQuality(50)
                            .compressToFile(new File(image.getPath()));
                    uploadFile(compressedImageFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(String image) {
        progressDialog.show();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);


        builder.addFormDataPart("postUserId", userId);
        builder.addFormDataPart("imageUploadType", String.valueOf(imageUploadType));


        final File file = new File(image);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));


        final MultipartBody requestBody = builder.build();
        UsersInterface postInterface = ApiClient.getApiClient().create(UsersInterface.class);
        Call<Integer> call = postInterface.uploadImage(requestBody);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.body() == 1) {
                    if (imageUploadType == 0) {
                        Picasso.with(ProfileActivity.this).load(file).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(coverPicture, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(file).placeholder(R.drawable.default_image_placeholder).into(coverPicture);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Cover Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    } else {

                        Picasso.with(ProfileActivity.this).load(file).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(circleImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(file).placeholder(R.drawable.img_default_user).into(circleImageView);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(ProfileActivity.this, "Upload Failed ", Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Upload Failed ", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
      pofile_option_btn.setEnabled(true);
    }

    public  class PeformActoin{
             String operationType;
             String userId;
             String profileId;

            public PeformActoin(String operationType, String userId, String profileId) {
                this.operationType = operationType;
                this.userId = userId;
                this.profileId = profileId;
            }
        }


}
