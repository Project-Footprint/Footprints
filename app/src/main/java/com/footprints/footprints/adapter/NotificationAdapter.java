package com.footprints.footprints.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.controllers.AgoDateParse;
import com.footprints.footprints.models.Notification;
import com.footprints.footprints.rest.ApiClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

        /*
                1. liked your post
                2. commented on your post
                3. replied on your comment
                4. Send you friend Request
                5. Accepted your friend Request

         */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    ArrayList<Notification> notifications = new ArrayList<>();
    Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, int position) {
        final Notification notification = notifications.get(position);


        if(notification.getType().equals("1")){
            holder.notificationTitle.setText(notification.getName()+" Liked your post");
        }else if(notification.getType().equals("2")){
            holder.notificationTitle.setText(notification.getName()+" commented on your post");
        }else if(notification.getType().equals("3")){
            holder.notificationTitle.setText(notification.getName()+" replied on your post");
        }else if(notification.getType().equals("4")){
            holder.notificationTitle.setText(notification.getName()+" send you friend Request");
        }else if(notification.getType().equals("5")){
            holder.notificationTitle.setText(notification.getName()+" accepted your friend Request");
        }



        if(notification.getType().equals("1") || notification.getName().equals("2") || notification.getName().equals("3")){
            holder.notificationBody.setText(notification.getPost());
            if( notification.getPost()!=null && !notification.getPost().equals("") ){
                holder.notificationBody.setText(notification.getPost());
            }else{
                holder.notificationBody.setVisibility(View.GONE);
            }

        }else{
            holder.notificationBody.setVisibility(View.GONE);
        }



        try {
            holder.notificationTime.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(notification.getNotificationTime())));
        } catch (ParseException e) {
            holder.notificationTime.setText("Unknown");
        }


        Picasso.with(context).load(ApiClient.BASE_URL+notification.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.img_default_user).into(holder.notificationFromImage, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(ApiClient.BASE_URL+notification.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.notificationFromImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View holder;
        CircleImageView notificationFromImage;
        TextView notificationTitle ,notificationTime,notificationBody;


        ViewHolder(View itemView) {
            super(itemView);
            holder = itemView;

            notificationTitle = holder.findViewById(R.id.activity_title_single);
            notificationFromImage = holder.findViewById(R.id.activity_profile_single);
            notificationBody  = holder.findViewById(R.id.activity_body);
            notificationTime = holder.findViewById(R.id.activity_date_single);
        }
    }
}
