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
import com.footprints.footprints.models.Post;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MemoriesAdapter  extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder>{
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
        Picasso.with(context).load(message.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.personImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(message.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.personImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        View holder;
        ImageView memoryImage,personImage;
        TextView personName, memoryDate, memoryStatus;


        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView;

            memoryImage = holder.findViewById(R.id.memory_image);
            personName = holder.findViewById(R.id.memory_people_name);
            personImage = holder.findViewById(R.id.memory_people_image);
            memoryDate = holder.findViewById(R.id.memory_date);
            memoryStatus = holder.findViewById(R.id.post);

        }
    }
}
