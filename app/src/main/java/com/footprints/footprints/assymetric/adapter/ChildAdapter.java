package com.footprints.footprints.assymetric.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.footprints.footprints.R;
import com.footprints.footprints.assymetric.AGVRecyclerViewAdapter;
import com.footprints.footprints.assymetric.AsymmetricItem;
import com.footprints.footprints.assymetric.model.ItemImage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChildAdapter extends AGVRecyclerViewAdapter<ViewHolder> {
    private final List<ItemImage> items;
    private int mDisplay = 0;
    private int mTotal = 0;
    Context context;

    public ChildAdapter(List<ItemImage> items,int mDisplay, int mTotal,Context context) {
      this.items = items;
      this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;
        Log.d("ChckBing4","Constructor Called MTotal :"+this.mTotal+" mDisplay :"+this.mDisplay);

    }




    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      Log.d("RecyclerViewActivity", "onCreateView");
      return new ViewHolder(parent, viewType,items);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      Log.d("RecyclerViewActivity", "onBindView position=" + position);
      holder.bind(items,position,mDisplay,mTotal,context);
    }

    @Override public int getItemCount() {
      return items.size();
    }

    @Override public AsymmetricItem getItem(int position) {
      return (AsymmetricItem) items.get(position);
    }

    @Override public int getItemViewType(int position) {
      return position % 2 == 0 ? 1 : 0;
    }
  }


class ViewHolder extends RecyclerView.ViewHolder {
  private final ImageView mImageView;
  private final TextView textView;

  public ViewHolder(ViewGroup parent, int viewType, List<ItemImage> items) {
    super(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.adapter_item, parent, false));

    mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
    textView = (TextView) itemView.findViewById(R.id.tvCount);



  }


  public void bind(final List<ItemImage> item, final int position, int mDisplay, int mTotal, final Context context) {
   // ImageLoader.getInstance().displayImage(String.valueOf(item.get(position).getImagePath()), mImageView);

      Picasso.with(context).load(item.get(position).getImagePath()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(mImageView, new com.squareup.picasso.Callback() {
          @Override
          public void onSuccess() {

          }

          @Override
          public void onError() {
              Picasso.with(context).load(item.get(position).getImagePath()).placeholder(R.drawable.default_image_placeholder).error(R.drawable.default_image_placeholder).into(mImageView);
          }
      });

     // ImageLoader.getInstance().displayImage(String.valueOf(item.get(position).getImagePath()), mImageView);
    textView.setText("+"+(mTotal-mDisplay));
    //textView.setText("MTotal "+mTotal+" MDisplay "+mDisplay);

     /* textView.setVisibility(View.VISIBLE);
      mImageView.setAlpha(.5f);*/
    if(mTotal > mDisplay)
    {
      if(position  == mDisplay-1) {
        textView.setVisibility(View.VISIBLE);
          textView.setAlpha(.5f);
      }
      else {
        textView.setVisibility(View.INVISIBLE);
        mImageView.setAlpha(1f);
      }
    }
    else
    {
      mImageView.setAlpha(1f);
      textView.setVisibility(View.INVISIBLE);
    }

    // textView.setText(String.valueOf(item.getPosition()));
  }
}
