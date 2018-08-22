package com.footprints.footprints.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.footprints.footprints.R;
import com.footprints.footprints.adapter.ImagePreviewAdapter;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.models.UploadObject;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.PostInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadMemories extends AppCompatActivity {

    LayoutInflater layoutInflater;
    private Toolbar toolbar;
    TextView postBtnTxt;
    EditText postEdtTxt;
    String name, latitude, longitude;
    ProgressDialog progressDialog;
    List<String> imagesEncodedList;
    boolean hasImageSelected = false;
    boolean hasMultipleImageSelected = false;
    ArrayList<String> options = new ArrayList<>();
    ListView listView;
    CircleImageView circleImageView;
    RecyclerView imagePreviewRcy;
    ImagePreviewAdapter imagePreviewAdapter;
    ArrayList<File> imagesLists = new ArrayList<>();
    BottomSheetBehavior bottomSheetBehavior;
    int privacyLevel = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        setContentView(R.layout.activity_upload_memories);

        imagePreviewRcy = findViewById(R.id.status_image_reveiw);
        imagePreviewRcy.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(UploadMemories.this, LinearLayoutManager.VERTICAL, false);
        imagePreviewRcy.setLayoutManager(layoutManager);
        imagePreviewRcy.getRecycledViewPool().setMaxRecycledViews(0, 0);
        imagePreviewAdapter = new ImagePreviewAdapter(imagesLists, UploadMemories.this);

        circleImageView = findViewById(R.id.dialogAvatar);
        Picasso.with(UploadMemories.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(circleImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(UploadMemories.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.img_default_user).into(circleImageView);
            }
        });
        listView = findViewById(R.id.bottom_sheet_listview);
        options.add("Add Images");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ImagePicker.create(UploadMemories.this)
                            .folderMode(true)
                            .limit(10)
                            .start(); // start image picker activity with request code
                }
            }
        });
       /* btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_GALLERY_CODE);*//*

            }
        });

*/
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
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

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
        postEdtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        postBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasImageSelected) {
                    uploadPostWithoutImage();
                } else {
                    uploadMultiFile();
                }

            }
        });
        Spinner mySpinner = (Spinner) custumBarLayout.findViewById(R.id.privacy_spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(UploadMemories.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.privacy_level));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    privacyLevel = 0;
                } else if (i == 1) {
                    privacyLevel = 1;
                } else if (i == 2) {
                    privacyLevel = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                privacyLevel = -1;
            }
        });
    }

    private void uploadPostWithoutImage() {
        String status = postEdtTxt.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Status postStatus = new Status(status, userId, "", latitude, longitude, privacyLevel, 0, 0, name);
        // Now Store the data in the mysql Database
        if (status.isEmpty()) {
            Toast.makeText(UploadMemories.this, "Please Write Something ", Toast.LENGTH_SHORT).show();
            return;
        }
        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Call<Integer> call = postInterface.postMemories(postStatus);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Intent intent = new Intent(UploadMemories.this, PlaceActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("latitude", latitude + "");
                    intent.putExtra("longitude", longitude + "");
                    startActivity(intent);

                    Toast.makeText(UploadMemories.this, "Upload Successful ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadMemories.this, "Upload Failed ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(UploadMemories.this, "Upload Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class Status {
        String post;
        String postUserId;
        String statusImage;
        String lattitude;
        String longnitude;
        String name;
        int privacy;
        int hasComment;
        int likeCount;


        public Status(String post, String postUserId, String statusImage, String lattitude, String longnitude, int privacy, int hasComment, int likeCount, String name) {
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


    private void uploadMultiFile() {
        progressDialog.show();

        String status = postEdtTxt.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("post", status);
        builder.addFormDataPart("postUserId", userId);
        builder.addFormDataPart("statusImage", "");
        builder.addFormDataPart("lattitude", latitude);
        builder.addFormDataPart("longnitude", longitude);
        builder.addFormDataPart("privacy", privacyLevel+"");
        builder.addFormDataPart("likeCount", "0");
        builder.addFormDataPart("hasComment", "0");
        builder.addFormDataPart("name", name);
        if (hasMultipleImageSelected) {
            builder.addFormDataPart("multipleimage", "1");
        } else {
            builder.addFormDataPart("multipleimage", "0");
        }
        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < imagesEncodedList.size(); i++) {
            File file = new File(imagesEncodedList.get(i));
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        File file = new File("");
        final MultipartBody requestBody = builder.build();
        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Call<UploadObject> call = postInterface.uploadMultiFile(requestBody);

        call.enqueue(new Callback<UploadObject>() {
            @Override
            public void onResponse(@NonNull Call<UploadObject> call, @NonNull Response<UploadObject> response) {


                Intent intent = new Intent(UploadMemories.this, PlaceActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("latitude", latitude + "");
                intent.putExtra("longitude", longitude + "");
                startActivity(intent);

                Toast.makeText(UploadMemories.this, "Upload Successful " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<UploadObject> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("", "Error " + t.getMessage());
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagesEncodedList = new ArrayList<String>();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            hasImageSelected = true;
            // Get a list of picked imagesLists
            List<Image> images = ImagePicker.getImages(data);
            if (images.size() > 1) {
                hasMultipleImageSelected = true;
            }


            for (int i = 0; i < images.size(); i++) {

                try {
                    File compressedImageFile = new Compressor(this)
                            .setMaxHeight(350)
                            .setQuality(50)
                            .compressToFile(new File(images.get(i).getPath()));
                    imagesEncodedList.add(compressedImageFile.toString());
                    imagesLists.add(new File(images.get(i).getPath()));

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            if (images.size() >= 1) {
                imagePreviewRcy.setAdapter(imagePreviewAdapter);
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (imagesEncodedList != null) {
            imagesEncodedList.clear();
        }

    }
}
