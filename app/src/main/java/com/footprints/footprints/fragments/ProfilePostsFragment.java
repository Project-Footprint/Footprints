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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.ProfileActivity;
import com.footprints.footprints.adapter.MemoriesAdapter;
import com.footprints.footprints.assymetric.model.ItemImage;
import com.footprints.footprints.assymetric.model.ItemList;
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


    int currentOffset = 0;
    ArrayList<ItemImage> Pathitems = new ArrayList<>();
    private List<ItemList> mItemList = new ArrayList<>();
    int lastItemPosition = 0;

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
        memoriesAdapter = new MemoriesAdapter(posts, context,mItemList);
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

    private void getMemoriesPost(final int limit, int offset) {

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

                        posts.addAll(response.body().getMemories().getMessage());
                        lastItemPosition = posts.size();

                        List<Post.Message> messages =  response.body().getMemories().getMessage();

                        for (int i = 0; i < messages.size(); i++) {
                            Pathitems.clear();

                            if(!isFromStart){
                                Log.d("CheckPostIdd",posts.get(i).getPostId()+" top "+i);
                                //   posts.get(lastItemPosition+i-1).setMyInfo("hey "+i);
                                posts.get(lastItemPosition-limit+i).setMyInfo("hey "+posts.get(lastItemPosition-limit+i).getPostId());
                                posts.get(lastItemPosition-limit+i).setTotalImageSize(posts.get(lastItemPosition-limit+i).getImageurls().size());
                            }else{
                                Log.d("CheckPostIdd",posts.get(i).getPostId()+" Button");
                                posts.get(i).setMyInfo("hey "+i);
                                posts.get(i).setTotalImageSize(messages.get(i).getImageurls().size());
                            }
                            Log.d("CheckPostIdd",posts.size()+" "+i);
                            if (messages.get(i).getHasMultipleImage().equals("1")) {


                                ArrayList<ItemImage> mPathitems = new ArrayList<>();
                                for (int j = 0; j < messages.get(i).getImageurls().size(); j++) {


                                    boolean isCol2Avail = false;
                                    String image = ApiClient.BASE_URL + "" + messages.get(i).getImageurls().get(j).getPath();
                                    ItemImage i1 = new ItemImage(i + 1, image, image);


                                    int totalImageSize = messages.get(i).getImageurls().size();
                                    if (totalImageSize == 3 ||  totalImageSize == 5) {

                                        if(!isFromStart){
                                            posts.get(lastItemPosition-limit+i).setDisplaySize(3);
                                        }else{
                                            posts.get(i).setDisplaySize(3);
                                        }

                                        if (j == 0) {
                                            i1.setRowSpan(2);
                                            i1.setColumnSpan(2);

                                        } else if (j == 1) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 2) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        }
                                    } else if (totalImageSize == 2 || totalImageSize == 1) {
                                        if(!isFromStart){
                                            posts.get(lastItemPosition-limit+i).setDisplaySize(4);
                                        }else{
                                            posts.get(i).setDisplaySize(4);
                                        }
                                        if (j == 0) {
                                            i1.setRowSpan(2);
                                            i1.setColumnSpan(3);

                                        } else if (j == 1) {
                                            i1.setRowSpan(2);
                                            i1.setColumnSpan(3);

                                        }
                                    } else if(totalImageSize==4){

                                        if(!isFromStart){
                                            posts.get(lastItemPosition-limit+i).setDisplaySize(4);
                                        }else{
                                            posts.get(i).setDisplaySize(4);
                                        }

                                        if (j == 0) {
                                            i1.setRowSpan(2);
                                            i1.setColumnSpan(3);

                                        } else if (j == 1) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 2) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        }else{
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);
                                        }

                                    }else {
                                        if(!isFromStart){
                                            posts.get(lastItemPosition-limit+i).setDisplaySize(6);
                                        }else{
                                            posts.get(i).setDisplaySize(6);
                                        }
                                        if (j == 0) {
                                            i1.setRowSpan(2);
                                            i1.setColumnSpan(2);

                                        } else if (j == 1) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 2) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 3) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 4) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        } else if (j == 5) {
                                            i1.setRowSpan(1);
                                            i1.setColumnSpan(1);

                                        }

                                    }


                                    i1.setPosition(currentOffset + j);


                                    Pathitems.add(i1);


                                }

                                for (int k = 0; k < posts.get(i).getDisplaySize(); k++) {
                                    if (k < Pathitems.size()) {
                                        mPathitems.add(Pathitems.get(k));
                                    }

                                }


                                ItemList itemList = new ItemList(i, "User " + (i + 1), mPathitems);
                                mItemList.add(itemList);
                                currentOffset += mPathitems.size();
                            } else {
                                ArrayList<ItemImage> mPathitems = new ArrayList<>();
                                ItemList itemList = new ItemList(i, "User " + (i + 1), mPathitems);
                                if(!isFromStart){
                                    posts.get(lastItemPosition-limit+i).setDisplaySize(0);
                                }else{
                                    posts.get(i).setDisplaySize(0);
                                }
                                mItemList.add(itemList);
                            }

                            Log.d("ChckBing1"," NewsFeed MTotal :"+posts.get(i).getTotalImageSize()+" mDisplay :"+posts.get(i).getDisplaySize());
                        }
                        if (isFromStart) {

                            memoriesRecyclerView.setAdapter(memoriesAdapter);
                        } else {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                           /* for(int i = lastItemPosition;i<limit;i++){
                                memoriesAdapter.notifyItemChanged(i);
                            }*/
                            memoriesAdapter.notifyItemRangeInserted(lastItemPosition,limit);
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
