package com.footprints.footprints.fragments.bottomsheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.adapter.CommentAdapter;
import com.footprints.footprints.controllers.VerticalNewsPaddingController;
import com.footprints.footprints.fragments.PlaceFriendFragment;
import com.footprints.footprints.fragments.ProfilePostsFragment;
import com.footprints.footprints.models.AddComment;
import com.footprints.footprints.models.Comment;
import com.footprints.footprints.models.Post;
import com.footprints.footprints.models.PostCommentModel;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.ActionInterface;
import com.footprints.footprints.rest.callbacks.PostInterface;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetComment extends BottomSheetDialogFragment {

    Context context;
    Post.Message post;
    CircleImageView commentProfile;
    EditText commentEditBox;
    ImageView commentSend, commentSendImagebtn;
    RecyclerView commentRecyclerView;
    ArrayList<Comment.CommentModel> comments = new ArrayList<>();
    CommentAdapter commentAdapter;
    boolean flagFab = true;
    RelativeLayout commentRel;
    public static TextView comments_txt;
    int position = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View holder = View.inflate(context, R.layout.bottom_sheet_layout, null);


        commentProfile = holder.findViewById(R.id.comment_profile);
        commentEditBox = holder.findViewById(R.id.comment_edt);
        commentSend = holder.findViewById(R.id.comment_send_img);
        comments_txt = holder.findViewById(R.id.comments_txt);
        commentRel = (RelativeLayout) holder.findViewById(R.id.chat_send_rel);
        commentSendImagebtn = (ImageView) holder.findViewById(R.id.comment_send_img);

        dialog.setContentView(holder);
        View view = (View) holder.getParent();
        view.setBackgroundColor(Color.TRANSPARENT);
        post = Parcels.unwrap(getFragmentManager().findFragmentByTag("commentFrag").getArguments().getParcelable("postModel"));
        position = getFragmentManager().findFragmentByTag("commentFrag").getArguments().getInt("position", 0);

        commentRecyclerView = holder.findViewById(R.id.comment_recy);
        commentRecyclerView.addItemDecoration(new VerticalNewsPaddingController(3));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        commentAdapter = new CommentAdapter(comments, context, post, position);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        if (post.getCommentCount().equals("0") || post.getCommentCount().equals("1")) {
            comments_txt.setText(post.getCommentCount() + " Comment");
        } else {
            comments_txt.setText(post.getCommentCount() + " Comments");
        }
        commentRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = commentEditBox.getText().toString().trim();
                if (comment.length() == 0) {
                    Toast.makeText(context, "Please Enter comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentEditBox.setText("");
                ((InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                ActionInterface actionInterface = ApiClient.getApiClient().create(ActionInterface.class);
                Call<PostCommentModel> call = actionInterface.postComment(new AddComment(comment, FirebaseAuth.getInstance().getUid(), "0", post.getPostId(), "0", "0", post.getPostUserId(), ""));
                call.enqueue(new Callback<PostCommentModel>() {
                    @Override
                    public void onResponse(@NonNull Call<PostCommentModel> call, Response<PostCommentModel> response) {
                        if (response.body().getSuccess() == 1) {

                            Toast.makeText(context, "Comment Successful ", Toast.LENGTH_SHORT).show();
                            int commentCount = Integer.parseInt(post.getCommentCount());
                            commentCount++;
                            post.setCommentCount(commentCount + "");
                            comments_txt.setText(commentCount + " Comments");
                            comments.add(response.body().getCommentModel());
                            int pos = comments.indexOf(response.body().getCommentModel());
                            commentAdapter.notifyItemInserted(pos);
                            commentRecyclerView.scrollToPosition(pos);

                            if (ProfilePostsFragment.memoriesAdapter != null) {
                                ProfilePostsFragment.memoriesAdapter.notifyItemChanged(position);
                            }
                            if (PlaceFriendFragment.memoriesAdapter != null) {
                                PlaceFriendFragment.memoriesAdapter.notifyItemChanged(position);
                            }


                        } else {
                            Toast.makeText(context, "Something Went Wrong ! ", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<PostCommentModel> call, Throwable t) {
                        Toast.makeText(context, "Something Went Wrong ! ", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        retrivePostComments();
        commentEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable img = getResources().getDrawable(R.drawable.ic_send_white_24dp);
                Drawable img1 = getResources().getDrawable(R.drawable.send_icon);

                if (s.toString().trim().length() != 0 && flagFab) {
                    commentRel.setBackgroundResource(R.drawable.back_fab);
                    ImageViewAnimatedChange(getContext(), commentSendImagebtn, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    commentRel.setBackgroundResource(R.drawable.back_fab_initial);
                    ImageViewAnimatedChange(getContext(), commentSendImagebtn, img1);
                    flagFab = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void retrivePostComments() {
        PostInterface postInterface = ApiClient.getApiClient().create(PostInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", post.getPostId());
        Call<Comment> call = postInterface.retriveTopComment(params);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull final Response<Comment> response) {
                comments.addAll(response.body().getCommentModel());
                commentRecyclerView.setAdapter(commentAdapter);
                Log.d("chrchsize", comments.size() + " size");
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

                Toast.makeText(context, "Something Went Wrong.. Please Retry !", Toast.LENGTH_LONG).show();
            }
        });
    }



    public void ImageViewAnimatedChange(Context c, final ImageView v, final Drawable new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageDrawable(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });

                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }


}
