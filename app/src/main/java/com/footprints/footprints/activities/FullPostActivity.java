package com.footprints.footprints.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.footprints.footprints.R;
import com.footprints.footprints.adapter.CommentAdapter;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.fragments.ProfilePostsFragment;
import com.footprints.footprints.models.AddComment;
import com.footprints.footprints.models.AddLike;
import com.footprints.footprints.models.Comment;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.models.PostCommentModel;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.ActionInterface;
import com.footprints.footprints.rest.callbacks.PostInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    boolean isFromNetwork;
    Post.Message post;
    LinearLayout likeSection, commentSection;
    ImageView profileImage, postImage, privacyIcon, likeIcon;
    TextView postTitle, postBody, postDate, likeCounter, commentCounter;
    SliderLayout sliderLayout;
    RecyclerView commentRecylerview;
    CommentAdapter commentAdapter;
    RelativeLayout topHideShow;
    ArrayList<Comment.CommentModel> comments = new ArrayList<>();
    boolean flagFab = true;
    CircleImageView commentProfile;
    EditText commentEditText;
    RelativeLayout commentRel;
    ImageView commentSendImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullpost_activity);

        toolbar = (Toolbar) findViewById(R.id.fullpost_toolbar);


        topHideShow = findViewById(R.id.top_hide_show);
        profileImage = findViewById(R.id.extra_imageView);
        postImage = findViewById(R.id.memory_image);
        postTitle = findViewById(R.id.extra_notification_id_name);
        postBody = findViewById(R.id.user_saying_top);
        postDate = findViewById(R.id.extra_rss_date);
        privacyIcon = findViewById(R.id.privacy);
        sliderLayout = findViewById(R.id.slider);

        commentProfile = findViewById(R.id.comment_profile_full);
        commentEditText = findViewById(R.id.comment_edt);
        commentRel = findViewById(R.id.chat_send_icon_1);
        commentSendImageView = findViewById(R.id.comment_send_img_full);

        likeSection = findViewById(R.id.boostSection);
        commentSection = findViewById(R.id.commentSection);
        commentRecylerview = findViewById(R.id.fullpost_commts_rcy);

        commentRecylerview.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(FullPostActivity.this);
        commentRecylerview.setLayoutManager(layoutManager);
        commentRecylerview.getRecycledViewPool().setMaxRecycledViews(0, 0);
        commentRecylerview.setNestedScrollingEnabled(false);


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
            String postId = getIntent().getBundleExtra("postBundel").getString("postId", "");
                retrivePost(postId);
        } else {
            post = Parcels.unwrap(getIntent().getBundleExtra("postBundel").getParcelable("postModel"));
            //Toast.makeText(FullPostActivity.this,post.getPostId()+" post id",Toast.LENGTH_SHORT).show();
            setData(post);
            commentAdapter = new CommentAdapter(comments, FullPostActivity.this, post, 0);
            retrivePostComments();
        }

        postTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullPostActivity.this, ProfileActivity.class);
                intent.putExtra("uid", post.getPostUserId());

                ProfilePostsFragment.isClearNeeded = false;
                ProfilePostsFragment.posts.clear();
                if (ProfilePostsFragment.memoriesAdapter != null) {
                    ProfilePostsFragment.memoriesAdapter.notifyDataSetChanged();
                }


                startActivity(intent);
            }
        });

        commentRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final String comment = commentEditText.getText().toString().trim();
                if (comment.length() == 0) {
                    Toast.makeText(FullPostActivity.this, "Please Enter comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentEditText.setText("");
                ((InputMethodManager) getSystemService(FullPostActivity.this.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                Call<PostCommentModel> call = actionInterface.postComment(new AddComment(comment, uid, "0", post.getPostId(), "0", "0", post.getPostUserId(), ""));
                call.enqueue(new Callback<PostCommentModel>() {
                    @Override
                    public void onResponse(@NonNull Call<PostCommentModel> call, Response<PostCommentModel> response) {
                        if (response.body().getSuccess() == 1) {

                            Toast.makeText(FullPostActivity.this, "Comment Successful ", Toast.LENGTH_SHORT).show();
                            int commentCount = Integer.parseInt(post.getCommentCount());
                            commentCount++;
                            post.setCommentCount(commentCount + "");

                            comments.add(response.body().getCommentModel());
                            int pos = comments.indexOf(response.body().getCommentModel());
                            commentAdapter.notifyItemInserted(pos);
                            commentRecylerview.scrollToPosition(pos);



                        } else {
                            Toast.makeText(FullPostActivity.this, "Something Went Wrong ! ", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<PostCommentModel> call, Throwable t) {
                        Toast.makeText(FullPostActivity.this, "Something Went Wrong ! ", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable img = getResources().getDrawable(R.drawable.ic_send_white_24dp);
                Drawable img1 = getResources().getDrawable(R.drawable.send_icon);

                if (s.toString().trim().length() != 0 && flagFab) {
                    commentRel.setBackgroundResource(R.drawable.back_fab);
                    ImageViewAnimatedChange(FullPostActivity.this, commentSendImageView, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    commentRel.setBackgroundResource(R.drawable.back_fab_initial);
                    ImageViewAnimatedChange(FullPostActivity.this, commentSendImageView, img1);
                    flagFab = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void retrivePost(String postId) {
        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", postId);
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<Post> call = postInterface.retrivepostsdetails(params);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, Response<Post> response) {
                if(response.body()!=null){

                    if(response.body().getMemories().getMessage().size()>0){
                        post = response.body().getMemories().getMessage().get(0);
                        setData(post);
                        commentAdapter = new CommentAdapter(comments, FullPostActivity.this, post, 0);
                        retrivePostComments();
                    }else{
                        topHideShow.setVisibility(View.GONE);
                        Toast.makeText(FullPostActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(FullPostActivity.this, "Review Failed... Please Retry !", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void retrivePostComments() {
        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", post.getPostId());
        Call<Comment> call = postInterface.retriveTopComment(params);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull final Response<Comment> response) {
                comments.addAll(response.body().getCommentModel());
                commentRecylerview.setAdapter(commentAdapter);

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

                Toast.makeText(FullPostActivity.this, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
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
    public void ImageViewAnimatedChange(Context c, final ImageView v, final Drawable new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageDrawable(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });

                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

}
