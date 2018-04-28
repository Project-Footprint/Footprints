package com.footprints.footprints.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.PlaceActivity;
import com.footprints.footprints.adapter.ReviewsAdapter;
import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.models.Review;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.AddressInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceReviewFragment extends android.support.v4.app.Fragment {
    EditText commentEditTxt;
    ImageView commentSendImagebtn;
    RelativeLayout chatRel;
    boolean flagFab = true;
    Context context;
    CircleImageView reveiwPersonImage;
    RecyclerView reviewRecyclerview;
    List<Review.Message> reviews = new ArrayList<>();
    ReviewsAdapter reviewAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_review, container, false);
        commentEditTxt = (EditText) view.findViewById(R.id.comment_edt);
        commentSendImagebtn = (ImageView) view.findViewById(R.id.comment_send_img_full);
        chatRel = (RelativeLayout) view.findViewById(R.id.chat_send_icon_1);
        reveiwPersonImage = view.findViewById(R.id.comment_profile_full);
        reviewRecyclerview = view.findViewById(R.id.recy_reveiws);
        reviewRecyclerview.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        reviewRecyclerview.setLayoutManager(layoutManager);
        reviewAdapter = new ReviewsAdapter(reviews, context);


        Picasso.with(context).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(reveiwPersonImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.img_default_user).into(reveiwPersonImage);
            }
        });

        commentEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable img = getResources().getDrawable(R.drawable.ic_send_white_24dp);
                Drawable img1 = getResources().getDrawable(R.drawable.send_icon);

                if (s.toString().trim().length() != 0 && flagFab) {
                    chatRel.setBackgroundResource(R.drawable.back_fab);
                    ImageViewAnimatedChange(getContext(), commentSendImagebtn, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    chatRel.setBackgroundResource(R.drawable.back_fab_initial);
                    ImageViewAnimatedChange(getContext(), commentSendImagebtn, img1);
                    flagFab = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        chatRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkDetectController.checkConnection(context)) {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                performReviewSend();
            }
        });
        getReviews();
        return view;
    }

    private void getReviews() {
        AddressInterface postInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Call<Review> call = postInterface.retrivereview(new RetriveReviews(PlaceActivity.latitude, PlaceActivity.longitude));
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(@NonNull Call<Review> call, Response<Review> response) {

                if (response.body().getReview().getSuccess() == 1) {
                    reviews.addAll(response.body().getReview().getMessage());
                    Log.d("FetchReveiw","Got IT");

                    reviewRecyclerview.setAdapter(reviewAdapter);
                } else {
                    Log.d("FetchReveiw","Couldn't Got it");
                }

            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(context, "Review Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performReviewSend() {
        String cmts = commentEditTxt.getText().toString();
        if (TextUtils.isEmpty(cmts) || cmts.equals("") || cmts.equals(" ")) {
            Toast.makeText(context, "Please, write your Review first ", Toast.LENGTH_LONG).show();
            return;
        }
        // commentsEdt1.clearFocus();
        commentEditTxt.getText().clear();
        ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AddressInterface postInterface = ApiClient.getApiClient().create(AddressInterface.class);
        Call<Integer> call = postInterface.addReview(new AddReviews(cmts, userId, PlaceActivity.latitude, PlaceActivity.longitude));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {


                    Toast.makeText(context, "Review Successful ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Review Failed ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context, "Review Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
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

    public class AddReviews {
        String review;
        String postUserId;
        String lattitude;
        String longnitude;

        public AddReviews(String review, String postUserId, String lattitude, String longnitude) {
            this.review = review;
            this.postUserId = postUserId;
            this.lattitude = lattitude;
            this.longnitude = longnitude;
        }
    }

    public static class  RetriveReviews {
        String lattitude;
        String longnitude;

        public RetriveReviews(String lattitude, String longnitude) {
            this.lattitude = lattitude;
            this.longnitude = longnitude;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        reviews.clear();
        reviewAdapter.notifyDataSetChanged();
    }
}
