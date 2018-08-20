package com.footprints.footprints.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.PlaceActivity;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.activities.SearchResultsActivity;
import com.footprints.footprints.controllers.ControllerPixels;
import com.footprints.footprints.models.SearchModel;
import com.footprints.footprints.rest.ApiClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context context;
    List<SearchModel.Message> messages;

    public SearchAdapter(List<SearchModel.Message> message, SearchResultsActivity searchResultsActivity) {
        this.context = searchResultsActivity;
        this.messages = message;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SearchModel.Message message = messages.get(position);
        holder.searchName.setText(message.getName().trim());
        if(message.getType()==1){
            if (!message.getProfileUrl().equals("")) {
                Picasso.with(context).load( ApiClient.BASE_URL+message.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.searchImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(ApiClient.BASE_URL+message.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.searchImage);
                    }
                });
            }else{
                holder.searchImage.setVisibility(View.GONE);
            }
        }else{
            int dp = (int) ControllerPixels.convertDpToPixel(16,context);
            holder.searchName.setPadding(dp,dp,dp,dp);
            holder.searchImage.setVisibility(View.GONE);
        }
        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getType()==1){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid",message.getUid());
                    context.startActivity(intent);
                }else{

                    Intent intent = new Intent(context, PlaceActivity.class);
                    intent.putExtra("name", message.getName());
                    intent.putExtra("latitude", message.getLat());
                    intent.putExtra("longitude", message.getLon());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       View holder;
       ImageView searchImage;
       TextView searchName;
        ViewHolder(View itemView) {
            super(itemView);

            holder = itemView;
            searchImage = holder.findViewById(R.id.search_image);
            searchName = holder.findViewById(R.id.search_text);

        }
    }
}
