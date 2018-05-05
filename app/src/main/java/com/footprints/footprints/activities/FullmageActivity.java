package com.footprints.footprints.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.footprints.footprints.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FullmageActivity extends AppCompatActivity {
    PhotoView mPhotoDraweeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.acivity_fullimagesize);



        final String imageUrl = getIntent().getStringExtra("imageUrl");
        mPhotoDraweeView = (PhotoView) findViewById(R.id.full_image_id);
        if(!imageUrl.isEmpty()){
            Picasso.with(FullmageActivity.this).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(mPhotoDraweeView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(FullmageActivity.this).load(imageUrl).into(mPhotoDraweeView);
                }
            });

        }

/*
        imageView = (ImageView) findViewById(R.id.goback);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // ProfilefullImage.super.onBackPressed();

            }
        });*/
    }
}
