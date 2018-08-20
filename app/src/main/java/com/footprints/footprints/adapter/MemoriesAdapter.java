package com.footprints.footprints.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.footprints.footprints.R;
import com.footprints.footprints.activities.FullPostActivity;
import com.footprints.footprints.activities.PlaceActivity;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.controllers.NetworkDetectController;
import com.footprints.footprints.fragments.ProfilePostsFragment;
import com.footprints.footprints.fragments.bottomsheets.BottomSheetComment;
import com.footprints.footprints.models.AddLike;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.ActionInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {
    List<Post.Message> posts = new ArrayList<>();
    Activity context;

    public MemoriesAdapter(List<Post.Message> posts, Context context) {
        this.posts = posts;
        this.context = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new MemoriesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post.Message message = posts.get(position);



        if(message.getPost()!=null && !message.getPost().equals("") && message.getPost().length()>1){
            holder.memoryStatus.setText(message.getPost());
        }else{
            holder.memoryStatus.setVisibility(View.GONE);
        }
        holder.personName.setText(message.getName());
        if(message.getPlaceName()!=null){
            holder.memory_place_name.setVisibility(View.VISIBLE);
            holder.memory_place_name_at.setVisibility(View.VISIBLE);
            holder.memory_place_name.setText(message.getPlaceName());
        }


        if (message.getPrivacy().equals("0")) {
            holder.privacy_icon.setImageResource(R.drawable.icon_friends);
        } else if (message.getPrivacy().equals("1")) {
            holder.privacy_icon.setImageResource(R.drawable.icon_onlyme);
        } else {
            holder.privacy_icon.setImageResource(R.drawable.icon_public);
        }
        if (message.getIsLiked()) {
            holder.likeImage.setImageResource(R.drawable.icon_like_selected);
        } else {
            holder.likeImage.setImageResource(R.drawable.icon_like);
        }
        if (message.getLikeCount().equals("0") || message.getLikeCount().equals("1")) {
            holder.likeTxt.setText(message.getLikeCount() + " Like");
        } else {
            holder.likeTxt.setText(message.getLikeCount() + " Likes");
        }
        if (message.getCommentCount().equals("0") || message.getCommentCount().equals("1")) {
            holder.commentTxt.setText(message.getCommentCount() + " Comment");
        } else {
            holder.commentTxt.setText(message.getCommentCount() + " Comments");
        }
        try {
            holder.memoryDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(message.getStatusTime())));
        } catch (ParseException e) {
            holder.memoryDate.setText("Unknown");
        }
        Picasso.with(context).load(ApiClient.BASE_URL + message.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.personImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(ApiClient.BASE_URL + message.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.personImage);
            }
        });

        if (message.getPost().isEmpty()) {
            holder.memoryStatus.setVisibility(View.GONE);


        }
        if (message.getHasImage().equals("1")) {

            if (message.getHasMultipleImage().equals("0")) {
                if (message.getImageurls().size() > 0) {
                    holder.memoryImage.setVisibility(View.VISIBLE);
                    final Uri uri = Uri.parse(ApiClient.BASE_URL + message.getImageurls().get(0).getPath());
                    Log.d("checkImageUrll", uri.toString());
                    Log.d("checkImageUrll", "Single Image ");
                    Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.memoryImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(uri).placeholder(R.drawable.default_image_placeholder).error(R.drawable.default_image_placeholder).into(holder.memoryImage);
                        }
                    });
                } else {
                    holder.memoryImage.setVisibility(View.GONE);
                }

            } else {
                if (message.getImageurls().size() > 1) {
                    DefaultSliderView defaultSliderView;
                    List<Post.Imageurl> urls = message.getImageurls();

                    for (int i = 0; i < urls.size(); i++) {

                        defaultSliderView = new DefaultSliderView(context);
                        defaultSliderView.empty(R.drawable.default_image_placeholder);
                        defaultSliderView.image(ApiClient.BASE_URL + urls.get(i).getPath())
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        Intent intent = new Intent(context, FullPostActivity.class);
                                        Bundle args = new Bundle();
                                        args.putParcelable("postModel", Parcels.wrap(message));
                                        args.putBoolean("fromNetwork",false);
                                        intent.putExtra("postBundel",args);
                                        context.startActivity(intent);
                                    }
                                });



                        holder.sliderLayout.addSlider(defaultSliderView);

                    }



                    holder.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
                    holder.sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
                    holder.sliderLayout.setDuration(4000);


                    holder.sliderLayout.setVisibility(View.VISIBLE);
                    holder.sliderLayout.stopAutoCycle();
                    holder.memoryImage.setVisibility(View.GONE);

                } else {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.memoryStatus.getLayoutParams();
                    Resources r = context.getResources();
                    int px = (int) ControllerPixels.convertDpToPixel(5, context);
                    params.bottomMargin = px;
                    holder.memoryStatus.setLayoutParams(params);
                    holder.memoryImage.setVisibility(View.GONE);
                }

            }

        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.memoryStatus.getLayoutParams();
            Resources r = context.getResources();
            int px = (int) ControllerPixels.convertDpToPixel(5, context);
            params.bottomMargin = px;
            holder.memoryStatus.setLayoutParams(params);
            holder.memoryImage.setVisibility(View.GONE);
        }

        /*if(message.getStatusImage().isEmpty() && message.getPost().isEmpty()){
            holder.holder.setVisibility(View.GONE);
        }*/
        holder.personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", message.getPostUserId());

                ProfilePostsFragment.isClearNeeded=false;
                ProfilePostsFragment.posts.clear();
                if(ProfilePostsFragment.memoriesAdapter!=null){
                    ProfilePostsFragment.memoriesAdapter.notifyDataSetChanged();
                }


                context.startActivity(intent);

            }
        });

        holder.memory_place_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("name", message.getPlaceName());
                intent.putExtra("latitude", message.getLat());
                intent.putExtra("longitude", message.getLon());
                context.startActivity(intent);
            }
        });
        holder.likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkDetectController.checkConnection(context)) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                holder.likeSection.setEnabled(false);
                if (!message.getIsLiked()) {


                    holder.likeImage.setImageResource(R.drawable.icon_like_selected);
                    int count = Integer.parseInt(message.getLikeCount());
                    count++;
                    posts.get(position).setLikeCount(count + "");
                    posts.get(position).setIsLiked(true);
                    if (count == 1) {
                        holder.likeTxt.setText(count + " Like");
                    } else {
                        holder.likeTxt.setText(count + " Likes");
                    }


                    ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                    Call<Integer> call = actionInterface.addLike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), message.getPostId(), message.getPostUserId(), "1"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                            if (response.body().equals("0")) {
                                holder.likeImage.setImageResource(R.drawable.icon_like);
                                int count = Integer.parseInt(message.getLikeCount());
                                count--;
                                if (count == 0 || count == 1) {
                                    holder.likeTxt.setText(count + " Like");
                                } else {
                                    holder.likeTxt.setText(count + " Likes");
                                }
                                posts.get(position).setLikeCount(count + "");
                                Toast.makeText(context, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                                posts.get(position).setIsLiked(false);
                            }

                            holder.likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            holder.likeSection.setEnabled(true);
                            holder.likeImage.setImageResource(R.drawable.icon_like);
                            int count = Integer.parseInt(message.getLikeCount());
                            count--;
                            if (count == 0 || count == 1) {
                                holder.likeTxt.setText(count + " Like");
                            } else {
                                holder.likeTxt.setText(count + " Likes");
                            }
                            posts.get(position).setLikeCount(count + "");
                            posts.get(position).setIsLiked(false);
                            Toast.makeText(context, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Handing the unlike event

                    int count = Integer.parseInt(message.getLikeCount());
                    count--;
                    if (count < 0) {
                        Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    holder.likeImage.setImageResource(R.drawable.icon_like);
                    posts.get(position).setLikeCount(count + "");
                    posts.get(position).setIsLiked(false);

                    if (count == 0 || count == 1) {
                        holder.likeTxt.setText(count + " Like");
                    } else {
                        holder.likeTxt.setText(count + " Likes");
                    }
                    holder.likeSection.setEnabled(false);

                    ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                    Call<Integer> call = actionInterface.unLike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), message.getPostId(), message.getPostUserId(), "1"));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                            if (response.body().equals("0")) {
                                holder.likeImage.setImageResource(R.drawable.icon_like_selected);
                                int count = Integer.parseInt(message.getLikeCount());
                                count++;
                                if (count == 0 || count == 1) {
                                    holder.likeTxt.setText(count + " Like");
                                } else {
                                    holder.likeTxt.setText(count + " Likes");
                                }

                                posts.get(position).setLikeCount(count + "");
                                Toast.makeText(context, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                                posts.get(position).setIsLiked(true);
                            }

                            holder.likeSection.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            holder.likeSection.setEnabled(true);
                            holder.likeImage.setImageResource(R.drawable.icon_like_selected);
                            int count = Integer.parseInt(message.getLikeCount());
                            count++;
                            if (count == 0 || count == 1) {
                                holder.likeTxt.setText(count + " Like");
                            } else {
                                holder.likeTxt.setText(count + " Likes");
                            }
                            posts.get(position).setLikeCount(count + "");
                            posts.get(position).setIsLiked(true);
                            Toast.makeText(context, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        holder.commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetComment();
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(message));
                args.putInt("position",position);
                bottomSheetDialogFragment.setArguments(args);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFrag");
            }
        });
        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullPostActivity.class);
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(message));
                args.putBoolean("fromNetwork",false);
                intent.putExtra("postBundel",args);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

  /*  @Override
    public void onSliderClick(BaseSliderView slider) {

        Intent intent = new Intent(context, FullPostActivity.class);
        Bundle args = new Bundle();
        args.putBoolean("fromNetwork",true);
        intent.putExtra("postBundel",args);
        context.startActivity(intent);
    }*/


    public class ViewHolder extends RecyclerView.ViewHolder {

        View holder;
        ImageView memoryImage, personImage, likeImage, commentImage, privacy_icon;
        TextView personName, memoryDate, memoryStatus,memory_place_name,memory_place_name_at;
        SliderLayout sliderLayout;
        LinearLayout likeSection, commentSection;
        TextView likeTxt, commentTxt;


        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView;
          //  ( (CardView) holder).setPreventCornerOverlap(false);


          /*  ( (CardView) holder).setPadding(0,0,0,0);
            ( (CardView) holder).setUseCompatPadding(true);
            ( (CardView) holder).setContentPadding(0,0,0,0);*/

           // ( (CardView) holder).setUseCompatPadding(false);

            memoryImage = holder.findViewById(R.id.memory_image);
            personName = holder.findViewById(R.id.memory_people_name);
            memory_place_name = holder.findViewById(R.id.memory_place_name);
            memory_place_name_at = holder.findViewById(R.id.memory_place_name_at);
            personImage = holder.findViewById(R.id.memory_people_image);
            memoryDate = holder.findViewById(R.id.memory_date);
            memoryStatus = holder.findViewById(R.id.post);
            sliderLayout = holder.findViewById(R.id.slider);
            privacy_icon = holder.findViewById(R.id.privacy_icon);

            likeSection = holder.findViewById(R.id.likeSection);
            commentSection = holder.findViewById(R.id.commentSection);

            likeTxt = holder.findViewById(R.id.like_txt);
            likeImage = holder.findViewById(R.id.like_img);

            commentTxt = holder.findViewById(R.id.comment_txt);
            commentImage = holder.findViewById(R.id.comment_img);

        }
    }



}
