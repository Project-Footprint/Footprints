package com.footprints.footprints.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.footprints.footprints.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
    Context context;
    ArrayList<File> images;
    public  ImagePreviewAdapter(ArrayList<File> images,Context context){
        this.context = context;
        this.images = images;
    }
    @NonNull
    @Override
    public ImagePreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_image, parent, false);
        return new ImagePreviewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagePreviewAdapter.ViewHolder holder, int position) {
        final File image = images.get(position);

        if(!image.toString().isEmpty()){
            Picasso.with(context).load(image).placeholder(R.drawable.default_image_placeholder).into(holder.reviewerImage);
        }

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewerImage;
        View holder;
         ViewHolder(View itemView) {
            super(itemView);
            this.holder = itemView;
            this.reviewerImage = holder.findViewById(R.id.review_image);
        }
    }
}
