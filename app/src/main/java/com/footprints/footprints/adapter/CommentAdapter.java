package com.footprints.footprints.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.fragments.ProfilePostsFragment;
import com.footprints.footprints.fragments.bottomsheets.BottomSheetSubComment;
import com.footprints.footprints.models.Comment;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.rest.ApiClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment.CommentModel> comments = new ArrayList<>();
    private Context context;
    private Post.Message post;
    private int postPosition;

    public CommentAdapter(ArrayList<Comment.CommentModel> comments, Context context, Post.Message post, int position) {
        this.comments = comments;
        this.context = context;
        this.post = post;
        this.postPosition= position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_comment, parent, false);
        return new CommentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Comment.CommentModel commentModel = comments.get(position);
        holder.comment_body.setText(commentModel.getComment());
        holder.commentName.setText(commentModel.getName());

        final String commenterProfilImage = ApiClient.BASE_URL + commentModel.getProfileUrl();
        if (!commentModel.getProfileUrl().equals("")) {
            Picasso.with(context).load(commenterProfilImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.commentImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(commenterProfilImage).placeholder(R.drawable.img_default_user).into(holder.commentImage);
                }
            });
        }
        try {
            holder.commentDatetxt.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(commentModel.getCommentDate())));
        } catch (ParseException e) {
            holder.commentDatetxt.setText("just now");
        }

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetSubComment();
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(post));
                args.putParcelable("commentModel", Parcels.wrap(commentModel));
                args.putInt("position",postPosition);
                args.putInt("commentPosition",position);
                args.putBoolean("openkeyBoard",true);
                bottomSheetDialogFragment.setArguments(args);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFrag");
            }
        });

        holder.commentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid",commentModel.getCommentBy());
                ProfilePostsFragment.isClearNeeded=false;
                ProfilePostsFragment.posts.clear();
                ProfilePostsFragment.memoriesAdapter.notifyDataSetChanged();
                context.startActivity(intent);
            }
        });
        if(commentModel.getHasSubComment().equals("1")){
            holder.subCommentSection.setVisibility(View.VISIBLE);
            int commentTotal = commentModel.getSubComments().getTotal();
            if(commentTotal==1){
                holder.viewMoreTxt.setVisibility(View.GONE);
            }else{
                commentTotal--;
                holder.viewMoreTxt.setText("View "+commentTotal+" more comments");
            }
            holder.subCommentBody.setText(commentModel.getSubComments().getLastComment().get(0).getComment());
            holder.subCommentPersonName.setText(commentModel.getSubComments().getLastComment().get(0).getName());
            final String subCommentImageUrl = ApiClient.BASE_URL+commentModel.getSubComments().getLastComment().get(0).getProfileUrl();
            if (!commentModel.getProfileUrl().equals("")) {
                Picasso.with(context).load(subCommentImageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.subCommentProfile, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(subCommentImageUrl).placeholder(R.drawable.img_default_user).into(holder.subCommentProfile);
                    }
                });
            }
            try {
                holder.subCommentDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(commentModel.getSubComments().getLastComment().get(0).getCommentDate())));
            } catch (ParseException e) {
                holder.subCommentDate.setText("just now");
            }
        }

        holder.viewMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetSubComment();
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(post));
                args.putParcelable("commentModel", Parcels.wrap(commentModel));
                args.putInt("position",postPosition);
                args.putInt("commentPosition",position);
                args.putBoolean("openkeyBoard",false);
                bottomSheetDialogFragment.setArguments(args);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFrag");
            }
        });


        holder.subCommentPersonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid",commentModel.getSubComments().getLastComment().get(0).getCommentBy());

                ProfilePostsFragment.isClearNeeded=false;
                ProfilePostsFragment.posts.clear();
                ProfilePostsFragment.memoriesAdapter.notifyDataSetChanged();

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View holder;
        ImageView optionImage, commentImage;
        TextView reply;

        LinearLayout subCommentSection;
        TextView subCommentPersonName, commentDatetxt, comment_body, subCommentBody, subCommentDate, viewMoreTxt, upperLike, upperLikeCounter, edited, readMore;
        ImageView subCommentProfile;
        RelativeLayout relativeLayout;
        TextView commentName;


        ViewHolder(View itemView) {
            super(itemView);

            holder = itemView;
            optionImage = holder.findViewById(R.id.option_id);
            reply = holder.findViewById(R.id.reply_txt);
            commentName = holder.findViewById(R.id.comment_person);
            subCommentSection = holder.findViewById(R.id.sub_comment_section);
            subCommentPersonName = holder.findViewById(R.id.sub_comment_person_2);
            subCommentProfile = holder.findViewById(R.id.sub_comment_profile_2);
            subCommentBody = holder.findViewById(R.id.sub_comment_body_2);
            subCommentDate = holder.findViewById(R.id.sub_comment_date_2);


            edited = holder.findViewById(R.id.comment_edited);
            readMore = holder.findViewById(R.id.read_more_id_1);
            viewMoreTxt = holder.findViewById(R.id.view_more_txt_2);
            relativeLayout = holder.findViewById(R.id.rel_like_section);
            upperLike = holder.findViewById(R.id.upper_like_sub_comement);
            commentDatetxt = holder.findViewById(R.id.comment_date);
            upperLikeCounter = holder.findViewById(R.id.upper_like_counter);
            commentImage = holder.findViewById(R.id.comment_profile);
            comment_body = holder.findViewById(R.id.comment_body);
        }
    }
}
