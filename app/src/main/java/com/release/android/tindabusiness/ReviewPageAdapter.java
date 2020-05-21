package com.release.android.tindabusiness;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewPageAdapter extends RecyclerView.Adapter<ReviewPageAdapter.ViewHolder> {

    private ArrayList<String> commentNameList = new ArrayList<>();
    private ArrayList<String> commentContentList = new ArrayList<>();
    private ArrayList<String> commentTimeList = new ArrayList<>();

    public ReviewPageAdapter(ArrayList<String> commentNameList, ArrayList<String> commentContentList, ArrayList<String> commentTimeList){
        this.commentNameList = commentNameList;
        this.commentContentList = commentContentList;
        this.commentTimeList = commentTimeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewlistlayout,parent,false);
        ReviewPageAdapter.ViewHolder viewHolder = new ReviewPageAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String timeAgo = getTimeAgo(commentTimeList.get(position));

        holder.name.setText(commentNameList.get(position));
        holder.content.setText(commentContentList.get(position));
        holder.time.setText(timeAgo);

    }

    @Override
    public int getItemCount() {
        return commentNameList.size();
    }

    public String getTimeAgo(String time){

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        long timeVal = Long.parseLong(time);
        long now = System.currentTimeMillis();
        long diff = now - timeVal;

        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name,content,time;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textViewReviewListName);
            content = (TextView) itemView.findViewById(R.id.textViewReviewListContent);
            time = (TextView) itemView.findViewById(R.id.textViewReviewListTime);

        }
    }


}
