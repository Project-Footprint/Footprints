package com.footprints.footprints.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    public static MemoriesAdapter memoriesAdapter;
    public static List<Post.Message> posts = new ArrayList<>();
    Context context;
    public  static   boolean isClearNeeded=true;

    int limit = 3;
    int offset = 0;
    boolean isFromStart = true;
    ProgressBar progressBar;

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
        progressBar = view.findViewById(R.id.feedProgressBar);

        memoriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager mLayoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();


                if (pastVisibleItems + visibleItemCount == (totalItemCount)) {
                    isFromStart=false;
                    progressBar.setVisibility(View.VISIBLE);
                    offset = offset+limit;
                    getMemoriesPost(limit,offset);


                }


            }
        });

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        isFromStart = true;
        offset=0;
        getMemoriesPost(limit,offset);
    }

    private void getMemoriesPost(int limit, int offset) {

        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Map<String, String> params = new HashMap<String, String>();

        params.put("uid", ProfileActivity.uid);
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));

        Call<Post> call = postInterface.retriveUserPosts(params);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, Response<Post> response) {
                if (response.body() != null) {

                    if (response.body().getMemories().getSuccess() == 1) {
                        if(isFromStart){
                            posts.addAll(response.body().getMemories().getMessage());
                            memoriesRecyclerView.setAdapter(memoriesAdapter);
                        }else{
                            if(progressBar!=null){
                                progressBar.setVisibility(View.GONE);
                            }
                            posts.addAll(response.body().getMemories().getMessage());
                            memoriesAdapter.notifyDataSetChanged();
                        }

                    }
                }


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context, "Something went wrong !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if(isClearNeeded){
            posts.clear();
            memoriesAdapter.notifyDataSetChanged();
        }



    }


}
