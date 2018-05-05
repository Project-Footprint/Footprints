package com.footprints.footprints.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.adapter.MemoriesAdapter;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.PostInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePostsFragment extends Fragment {
    RecyclerView memoriesRecyclerView;
    MemoriesAdapter memoriesAdapter;
    List<Post.Message> posts = new ArrayList<>();
    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_friends, container, false);

        memoriesRecyclerView = view.findViewById(R.id.memories_recy);
        memoriesRecyclerView.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        memoriesRecyclerView.setLayoutManager(layoutManager);
        memoriesRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        memoriesAdapter = new MemoriesAdapter(posts, context);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getMemoriesPost();
    }

    private void getMemoriesPost() {

        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", ProfileActivity.uid);
        Call<Post> call = postInterface.retriveUserPosts(params);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, Response<Post> response) {
                if(response.body()!=null){
                    if (response.raw().cacheResponse() != null) {
                        Log.d("checkCache", "From Cache Inside Memoreis  ");
                    } else if (response.raw().networkResponse() != null) {
                        Log.d("checkCache", "Network call Inside Memoreis ");
                    } else {
                        Log.d("checkCache", "Unknown Inside Memoreis ");
                    }
                    if (response.body().getMemories().getSuccess() == 1) {
                        posts.addAll(response.body().getMemories().getMessage());
                        Log.d("FetchReveiw","Got IT");
                        memoriesRecyclerView.setAdapter(memoriesAdapter);
                    } else {
                        Log.d("FetchReveiw","Couldn't Got it");
                    }
                }


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context, "Review Failed... Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        posts.clear();
        memoriesAdapter.notifyDataSetChanged();
    }
}
