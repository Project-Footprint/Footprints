package com.footprints.footprints.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.adapter.SearchAdapter;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.models.SearchModel;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.ActionInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String keyword="";
    SearchAdapter searchAdapter;
    List<SearchModel.Message> message =new ArrayList<>(); ;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
         keyword = getIntent().getStringExtra("keyword");
         progressDialog = new ProgressDialog(SearchResultsActivity.this);
        progressDialog.setMessage("Loading please wait.");
        setTitle(keyword);

        recyclerView = findViewById(R.id.search_recy);
        recyclerView.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchResultsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        searchAdapter = new SearchAdapter(message, SearchResultsActivity.this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
        performSearch();
    }

    private void performSearch() {
        ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("keyword", keyword);
        Call<SearchModel> call = actionInterface.getSearchResponse(params);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchModel> call, Response<SearchModel> response) {
                if (response.body() != null) {
                    progressDialog.dismiss();
                    if (response.body().getSucess() == 1) {
                        message.addAll(response.body().getMessage());

                        recyclerView.setAdapter(searchAdapter);
                    }else{
                        Toast.makeText(SearchResultsActivity.this,"No, Records Found",Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SearchResultsActivity.this, "Something went wrong !", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        message.clear();
        searchAdapter.notifyDataSetChanged();
    }
}
