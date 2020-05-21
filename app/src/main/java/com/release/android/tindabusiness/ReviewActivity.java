package com.release.android.tindabusiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private ImageView back;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private ArrayList<String> reviewNameList,reviewContentList,reviewTimeList;
    private ReviewPageAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    private String uid;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //show progress dialog//
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ////////////////////////

        setupViews();
        activateListeners();
        setupRecyclerView();
        setupRatingBar();
        loadReviews();
    }

    private void setupViews() {

        back = (ImageView) findViewById(R.id.imageViewReviewBack);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarRating);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewReview);

        reviewNameList = new ArrayList<>();
        reviewContentList = new ArrayList<>();
        reviewTimeList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        uid = firebaseAuth.getCurrentUser().getUid();

    }

    private void activateListeners() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupRecyclerView() {

        adapter = new ReviewPageAdapter(reviewNameList,reviewContentList,reviewTimeList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loadReviews() {

        DatabaseReference reviewRef = rootRef.child("Business_Reviews").child(uid);
        reviewRef.orderByChild("Reviewer_timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    reviewNameList.add(ds.child("Reviewer_name").getValue().toString());
                    reviewContentList.add(ds.child("Reviewer_content").getValue().toString());
                    reviewTimeList.add(ds.child("Reviewer_timestamp").getValue().toString());

                    adapter.notifyDataSetChanged();
                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupRatingBar() {

        DatabaseReference ratingRef = rootRef.child("Business_Ratings").child(uid);
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                float ratingScore;
                int ratingCount;

                if(dataSnapshot.exists()){
                    ratingScore = Float.valueOf(dataSnapshot.child("Total_rating").getValue().toString());
                    ratingCount = Integer.valueOf(dataSnapshot.child("Total_count").getValue().toString());

                    ratingBar.setRating(ratingScore / ratingCount);
                }else{
                    ratingBar.setRating(0f);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
