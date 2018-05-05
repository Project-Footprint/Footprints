package com.footprints.footprints.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.footprints.footprints.R;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.rest.ApiClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MemoriesAdapter  extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> implements BaseSliderView.OnSliderClickListener {
    List<Post.Message> posts = new ArrayList<>();
    Context context;

    public MemoriesAdapter(List<Post.Message> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new MemoriesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post.Message message = posts.get(position);

        holder.memoryStatus.setText(message.getPost());
        holder.personName.setText(message.getName());

        try {
            holder.memoryDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(message.getStatusTime())));
        } catch (ParseException e) {
            holder.memoryDate.setText("Unknown");
        }
        Picasso.with(context).load(ApiClient.BASE_URL+message.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.personImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(ApiClient.BASE_URL+message.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.personImage);
            }
        });

        if(message.getPost().isEmpty()){
            holder.memoryStatus.setVisibility(View.GONE);


        }
        if(message.getHasImage().equals("1")){

            if(message.getHasMultipleImage().equals("0")){
                if(message.getImageurls().size()>0){
                    holder.memoryImage.setVisibility(View.VISIBLE);
                    final Uri uri = Uri.parse(ApiClient.BASE_URL+message.getImageurls().get(0).getPath());
                    Log.d("checkImageUrll",uri.toString());
                    Log.d("checkImageUrll","Single Image ");
                    Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.memoryImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(uri).placeholder(R.drawable.default_image_placeholder).error(R.drawable.default_image_placeholder).into(holder.memoryImage);
                        }
                    });
                }else{
                    holder.memoryImage.setVisibility(View.GONE);
                }

            }else{
                if(message.getImageurls().size()>1){
                    DefaultSliderView defaultSliderView;
                    List<Post.Imageurl> urls = message.getImageurls();

                    for(int i  = 0 ;i<urls.size();i++){

                        defaultSliderView = new DefaultSliderView(context);
                        defaultSliderView.empty(R.drawable.default_image_placeholder);
                        defaultSliderView.image(ApiClient.BASE_URL+urls.get(i).getPath())
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(this);


                        holder.sliderLayout.addSlider(defaultSliderView);
                    }

                    holder.sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
                    holder.sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
                    holder.sliderLayout.setDuration(4000);


                    holder.sliderLayout.setVisibility(View.VISIBLE);
                    holder.sliderLayout.stopAutoCycle();
                    holder.memoryImage.setVisibility(View.GONE);
                }else{
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.memoryStatus.getLayoutParams();
                    Resources r = context.getResources();
                    int px = (int) ControllerPixels.convertDpToPixel(5,context);
                    params.bottomMargin = px;
                    holder.memoryStatus.setLayoutParams(params);
                    holder.memoryImage.setVisibility(View.GONE);
                }

            }

        }else{
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.memoryStatus.getLayoutParams();
            Resources r = context.getResources();
            int px = (int) ControllerPixels.convertDpToPixel(5,context);
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
                intent.putExtra("uid",message.getPostUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        View holder;
        ImageView memoryImage,personImage;
        TextView personName, memoryDate, memoryStatus;
         SliderLayout sliderLayout;


        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView;

            memoryImage = holder.findViewById(R.id.memory_image);
            personName = holder.findViewById(R.id.memory_people_name);
            personImage = holder.findViewById(R.id.memory_people_image);
            memoryDate = holder.findViewById(R.id.memory_date);
            memoryStatus = holder.findViewById(R.id.post);
            sliderLayout = holder.findViewById(R.id.slider);

        }
    }

}
