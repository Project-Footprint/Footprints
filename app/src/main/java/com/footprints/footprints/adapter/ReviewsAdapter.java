package com.footprints.footprints.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.models.Review;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review.Message> reviews = new ArrayList<>();
    private Context context;

    public ReviewsAdapter(List<Review.Message> reviews, Context context) {
        this.reviews = (ArrayList<Review.Message>) reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapter.ViewHolder holder, int position) {
        final Review.Message message = reviews.get(position);
        holder.review.setText(message.getReview());
        holder.reviewerName.setText(message.getName());
        try {
            holder.reviewDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(message.getDate())));
        } catch (ParseException e) {
           holder.reviewDate.setText("Unknown");
        }
        Picasso.with(context).load(message.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.reviewerImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(message.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.reviewerImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View holder;
        ImageView reviewerImage;
        TextView reviewerName, reviewDate, review;


        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView;
            reviewerImage = holder.findViewById(R.id.reviewr_image);
            reviewerName = holder.findViewById(R.id.reviewer_name);
            reviewDate = holder.findViewById(R.id.review_date);
            review = holder.findViewById(R.id.review);

        }

    }

}
