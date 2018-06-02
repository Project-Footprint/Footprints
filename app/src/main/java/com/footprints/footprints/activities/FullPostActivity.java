package com.footprints.footprints.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.footprints.footprints.R;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.models.AddLike;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.ActionInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    boolean isFromNetwork;
    Post.Message post;
    LinearLayout likeSection, commentSection;
    ImageView profileImage, postImage, privacyIcon,likeIcon;
    TextView postTitle, postBody, postDate,likeCounter,commentCounter;
    SliderLayout sliderLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullpost_activity);

        toolbar = (Toolbar) findViewById(R.id.fullpost_toolbar);


        profileImage = findViewById(R.id.extra_imageView);
        postImage = findViewById(R.id.memory_image);
        postTitle = findViewById(R.id.extra_notification_id_name);
        postBody = findViewById(R.id.user_saying_top);
        postDate = findViewById(R.id.extra_rss_date);
        privacyIcon = findViewById(R.id.privacy);
        sliderLayout = findViewById(R.id.slider);

        likeSection = findViewById(R.id.boostSection);
        commentSection = findViewById(R.id.commentSection);

        likeIcon = findViewById(R.id.boost_img);
        likeCounter = findViewById(R.id.boosts_txt);
        commentCounter = findViewById(R.id.comments_txt_rss);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(FullPostActivity.this, MapsActivity.class));
                onBackPressed();
            }
        });
        isFromNetwork = getIntent().getBundleExtra("postBundel").getBoolean("fromNetwork", false);
        if (isFromNetwork) {

        } else {
            post = Parcels.unwrap(getIntent().getBundleExtra("postBundel").getParcelable("postModel"));
            setData(post);
        }
    }

    private void setData(final Post.Message post) {

        postTitle.setText(post.getName());
        if (post.getPost() != null && !post.getPost().equals("") && post.getPost().length() > 1) {
            postBody.setText(post.getPost());
        } else {
            postBody.setVisibility(View.GONE);
        }


        if (post.getIsLiked()) {
            likeIcon.setImageResource(R.drawable.icon_like_selected);
        } else {
            likeIcon.setImageResource(R.drawable.icon_like);
        }
        if (post.getLikeCount().equals("0") || post.getLikeCount().equals("1")) {
            likeCounter.setText(post.getLikeCount() + " Like");
        } else {
            likeCounter.setText(post.getLikeCount() + " Likes");
        }
        if (post.getCommentCount().equals("0") || post.getCommentCount().equals("1")) {
            commentCounter.setText(post.getCommentCount() + " Comment");
        } else {
            commentCounter.setText(post.getCommentCount() + " Comments");
        }



        if (post.getPrivacy().equals("0")) {
            privacyIcon.setImageResource(R.drawable.icon_friends);
        } else if (post.getPrivacy().equals("1")) {
            privacyIcon.setImageResource(R.drawable.icon_onlyme);
        } else {
            privacyIcon.setImageResource(R.drawable.icon_public);
        }


        final Uri profileUri = Uri.parse(ApiClient.BASE_URL + post.getProfileUrl());
        Picasso.with(FullPostActivity.this).load(profileUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(profileImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(FullPostActivity.this).load(profileUri).placeholder(R.drawable.default_image_placeholder).error(R.drawable.default_image_placeholder).into(profileImage);
            }
        });

        try {
            postDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(post.getStatusTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // For  post Image
        if (post.getHasImage().equals("1")) {

            if (post.getHasMultipleImage().equals("0")) {
                if (post.getImageurls().size() > 0) {

                    final Uri uri = Uri.parse(ApiClient.BASE_URL + post.getImageurls().get(0).getPath());

                    Picasso.with(FullPostActivity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(postImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(FullPostActivity.this).load(uri).placeholder(R.drawable.default_image_placeholder).error(R.drawable.default_image_placeholder).into(postImage);
                        }
                    });
                } else {
                    postImage.setVisibility(View.GONE);
                }

            } else {
                postImage.setVisibility(View.GONE);

                if (post.getImageurls().size() > 1) {
                    sliderLayout.setVisibility(View.VISIBLE);
                    DefaultSliderView defaultSliderView;
                    List<Post.Imageurl> urls = post.getImageurls();

                    for (int i = 0; i < urls.size(); i++) {

                        defaultSliderView = new DefaultSliderView(FullPostActivity.this);
                        defaultSliderView.empty(R.drawable.default_image_placeholder);
                        defaultSliderView.image(ApiClient.BASE_URL + urls.get(i).getPath())
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop);


                        sliderLayout.addSlider(defaultSliderView);

                    }


                    sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
                    sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
                    sliderLayout.setDuration(4000);


                    sliderLayout.setVisibility(View.VISIBLE);
                    sliderLayout.stopAutoCycle();


                } else {
                    sliderLayout.setVisibility(View.GONE);
                }
                // End of post Image part
            }
        }
    // for like part

        likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkDetectController.checkConnection(FullPostActivity.this)) {
                    Toast.makeText(FullPostActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                likeSection.setEnabled(false);
                if (!post.getIsLiked()) {


                    likeIcon.setImageResource(R.drawable.icon_like_selected);
                    int count = Integer.parseInt(post.getLikeCount());
                    count++;
                    post.setLikeCount(count + "");
                    post.setIsLiked(true);
                    if (count == 1) {
                        likeCounter.setText(count + " Like");
                    } else {
                        likeCounter.setText(count + " Likes");
                    }


                    ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                    Call<Integer> call = actionInterface.addLike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), post.getPostId(), post.getPostUserId(), "1"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                            if (response.body().equals("0")) {
                                likeIcon.setImageResource(R.drawable.icon_like);
                                int count = Integer.parseInt(post.getLikeCount());
                                count--;
                                if (count == 0 || count == 1) {
                                    likeCounter.setText(count + " Like");
                                } else {
                                    likeCounter.setText(count + " Likes");
                                }
                                post.setLikeCount(count + "");
                                Toast.makeText(FullPostActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                                post.setIsLiked(false);
                            }

                            likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            likeSection.setEnabled(true);
                            likeIcon.setImageResource(R.drawable.icon_like);
                            int count = Integer.parseInt(post.getLikeCount());
                            count--;
                            if (count == 0 || count == 1) {
                                likeCounter.setText(count + " Like");
                            } else {
                                likeCounter.setText(count + " Likes");
                            }
                            post.setLikeCount(count + "");
                            post.setIsLiked(false);
                            Toast.makeText(FullPostActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Handing the unlike event

                    int count = Integer.parseInt(post.getLikeCount());
                    count--;
                    if (count < 0) {
                        Toast.makeText(FullPostActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    likeIcon.setImageResource(R.drawable.icon_like);
                    post.setLikeCount(count + "");
                    post.setIsLiked(false);

                    if (count == 0 || count == 1) {
                        likeCounter.setText(count + " Like");
                    } else {
                        likeCounter.setText(count + " Likes");
                    }
                    likeSection.setEnabled(false);

                    ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                    Call<Integer> call = actionInterface.unLike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), post.getPostId(), post.getPostUserId(), "1"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                            if (response.body().equals("0")) {
                                likeIcon.setImageResource(R.drawable.icon_like_selected);
                                int count = Integer.parseInt(post.getLikeCount());
                                count++;
                                if (count == 0 || count == 1) {
                                    likeCounter.setText(count + " Like");
                                } else {
                                    likeCounter.setText(count + " Likes");
                                }

                                post.setLikeCount(count + "");
                                Toast.makeText(FullPostActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                                post.setIsLiked(true);
                            }

                            likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            likeSection.setEnabled(true);
                            likeIcon.setImageResource(R.drawable.icon_like_selected);
                            int count = Integer.parseInt(post.getLikeCount());
                            count++;
                            if (count == 0 || count == 1) {
                                likeCounter.setText(count + " Like");
                            } else {
                                likeCounter.setText(count + " Likes");
                            }
                            post.setLikeCount(count + "");
                            post.setIsLiked(true);
                            Toast.makeText(FullPostActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  startActivity(new Intent(FullPostActivity.this, MapsActivity.class));

    }


}
