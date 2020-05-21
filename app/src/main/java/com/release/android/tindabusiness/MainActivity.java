package com.release.android.tindabusiness;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView add,menu,headerPicture,postBack,postImage;
    private DrawerLayout drawerLayout;
    private TextView logout,headerName,headerAddress,headerNumber,headerGenre,viewStore,noPosts,viewProfile,viewPrivacy,postPost,postHeaderCount,postDescriptionCount;
    private String uid,headerNameText,headerAddressText,headerNumberText,headerGenreText,headerPictureText,postHeaderText,postDescriptionText,postImageText,postDownloadUrl;
    private DatabaseReference rootRef,postRef;
    private FirebaseAuth firebaseAuth;
    private NavigationView navigationView;
    private View headerView;
    private ArrayList<String> postTimeList,postHeaderList,postImageList,postDescriptionList,postLikeList,postCommentList,postIdList,commentNameList,commentContentList;
    private String businessName,businessID;
    private ShopFeedAdapter adapter;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private ConstraintLayout popupLayout;
    private EditText postHeader,postDescription;
    private static final int REQUEST_GALLERY = 1;
    private StorageReference storageReference;
    private long now;
    private TextView viewReviews;
    private ArrayList<String> followerList;
    private String postReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load progress dialog//
        progressDialog = new ProgressDialog(    this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading feed...");
        progressDialog.show();
        //----------------------------------------//

        setupViews();
        checkIfLoggedIn();

    }

    private void checkIfLoggedIn() {

        //this is for checking if user is already logged in or not//

        if(firebaseAuth.getCurrentUser() != null) {

            uid = firebaseAuth.getCurrentUser().getUid();

            loadBusinessInformation();
            activateListeners();
            setupBroadcastReceiver();


        }else{

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }

        ///////////////////////////////////////////////////////////

    }

    private void setupBroadcastReceiver() {

        //this function is for setting up broadcast receiver to listen for commands or request from Profile activity//
        //also for setting up receiver from comment activity///

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals("finish_activity")){
                    reloadBusinessInformation();
                }

            }
        };

        registerReceiver(broadcastReceiver,new IntentFilter("finish_activity"));
    }

    private void loadFeed() {

        //this function is for loading the feed content from the db//

        final DatabaseReference feedRef = rootRef.child("Posts").child(businessID);

        feedRef.orderByChild("Post_timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot ds : dataSnapshot.getChildren()){

                    postIdList.add(ds.getKey());
                    postDescriptionList.add(ds.child("Post_description").getValue().toString());
                    postHeaderList.add(ds.child("Post_header").getValue().toString());
                    postImageList.add(ds.child("Post_image").getValue().toString());
                    postTimeList.add(ds.child("Post_timestamp").getValue().toString());

                    //get like count//
                    feedRef.child(ds.getKey()).child("Post_likes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                int likeCount = 0;

                                for (DataSnapshot likeSnapshot : dataSnapshot.getChildren()) {
                                    likeCount++;
                                }

                                postLikeList.add(Integer.toString(likeCount));
                                getCommentCount(feedRef,ds);

                            }else{
                                postLikeList.add("0");
                                getCommentCount(feedRef,ds);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                if(postTimeList.size() == 0){
                    noPosts.setVisibility(View.VISIBLE);
                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCommentCount(DatabaseReference feedRef, DataSnapshot ds) {


        //get comment count//
        feedRef.child(ds.getKey()).child("Post_comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                        commentNameList.add(commentSnapshot.getKey());
                        commentContentList.add(commentSnapshot.getValue().toString());
                    }

                    postCommentList.add(Integer.toString(commentNameList.size()));

                    if (postTimeList.size() != 0) {
                        noPosts.setVisibility(View.GONE);
                    }

                    Log.d("COMMENT_CHECK","SIZE IS: " + String.valueOf(postCommentList.size()));
                    adapter.notifyDataSetChanged();

                }else{
                    Log.d("COMMENT_CHECK","ASIZE IS: " + String.valueOf(postCommentList.size()));
                    postCommentList.add("0");

                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupRecyclerView() {

        adapter = new ShopFeedAdapter(postTimeList,postHeaderList,postImageList,postDescriptionList,postLikeList,postCommentList,this,uid,businessName,postIdList,uid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void setupViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMain);
        add = (ImageView) findViewById(R.id.imageViewMainAdd);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);
        navigationView = (NavigationView) findViewById(R.id.navigationViewMain);
        menu = (ImageView) findViewById(R.id.imageViewMainMenu);
        viewStore = (TextView) findViewById(R.id.textViewMainViewStore);

        popupLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutPostPopout);
        postBack = (ImageView) findViewById(R.id.imageViewPostBack);
        postPost = (TextView) findViewById(R.id.textViewPostPost);
        postHeader = (EditText) findViewById(R.id.editTextPostHeader);
        postDescription = (EditText) findViewById(R.id.editTextPostDescription);
        postImage = (ImageView) findViewById(R.id.imageViewPostImage);
        postHeaderCount = (TextView) findViewById(R.id.textViewPostHeaderCount);
        postDescriptionCount = (TextView) findViewById(R.id.textViewPostDescriptionCount);

        headerView = navigationView.getHeaderView(0);
        logout = (TextView) headerView.findViewById(R.id.textViewMainHeaderLogout);
        headerName = (TextView) headerView.findViewById(R.id.textViewMainHeaderName);
        headerAddress = (TextView) headerView.findViewById(R.id.textViewMainHeaderAddress);
        headerNumber = (TextView) headerView.findViewById(R.id.textViewMainHeaderNumber);
        headerGenre = (TextView) headerView.findViewById(R.id.textViewMainHeaderGenre);
        headerPicture = (ImageView) headerView.findViewById(R.id.imageViewMainHeaderPicture);
        viewProfile = (TextView) headerView.findViewById(R.id.textViewMainHeaderProfile);
        viewPrivacy = (TextView) headerView.findViewById(R.id.textViewMainHeaderPrivacyPolicy);
        viewReviews = (TextView) headerView.findViewById(R.id.textViewMainHeaderReviews);

        postTimeList = new ArrayList<>();
        postHeaderList = new ArrayList<>();
        postImageList = new ArrayList<>();
        postDescriptionList = new ArrayList<>();
        postLikeList = new ArrayList<>();
        postCommentList = new ArrayList<>();
        postIdList = new ArrayList<>();
        followerList = new ArrayList<>();

        commentNameList = new ArrayList<>();
        commentContentList = new ArrayList<>();

        rootRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        noPosts = (TextView) findViewById(R.id.textViewMainNoPosts);

    }

    private void activateListeners() {

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(Gravity.START);

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        viewStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,StoreActivity.class);
                startActivity(intent);

            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                intent.putExtra("nameText",headerNameText);
                intent.putExtra("addressText",headerAddressText);
                intent.putExtra("numberText",headerNumberText);
                intent.putExtra("imageText",headerPictureText);
                intent.putExtra("genreText",headerGenreText);
                startActivity(intent);

            }
        });

        viewPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tinda-business.flycricket.io/privacy.html"));
                startActivity(browserIntent);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupLayout.setVisibility(View.VISIBLE);

            }
        });

        postBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupLayout.setVisibility(View.GONE);

            }
        });

        postPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postHeaderText = postHeader.getText().toString();
                postDescriptionText = postDescription.getText().toString();

                Boolean isValid = validateInputs();

                if(isValid){

                    progressDialog.setMessage("Saving post...");
                    progressDialog.show();
                    savePostToDB();

                }

            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_GALLERY);

            }
        });

        postHeader.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                postHeaderCount.setText("(" + s.toString().length() + "/70)");

            }
        });

        postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                postDescriptionCount.setText("(" + s.toString().length() + "/160)");

            }
        });

        viewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,ReviewActivity.class);
                startActivity(intent);

            }
        });

    }

    private void savePostToDB() {

        //this function is for uploading post information except image//

        now = System.currentTimeMillis();

        postRef = rootRef.child("Posts").child(uid).push();
        postReference = postRef.getKey();
        postRef.child("Post_description").setValue(postDescriptionText);
        postRef.child("Post_header").setValue(postHeaderText);
        postRef.child("Post_timestamp").setValue(now);
        uploadPostImage();

    }

    private void uploadPostImage() {

        //this function is for uploading post image to DB//
        Uri pictureUri = Uri.parse(postImageText);
        final StorageReference pictureRef = storageReference.child("Post_pictures").child(pictureUri.getLastPathSegment());
        pictureRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        postDownloadUrl = uri.toString();
                        postRef.child("Post_image").setValue(postDownloadUrl);

                        Toast.makeText(MainActivity.this, "Post successful!", Toast.LENGTH_SHORT).show();
                        loadFollowerList();

                    }
                });
            }
        });

    }

    private void loadFollowerList() {

        //this function is for getting list of followers for notification//

        final DatabaseReference followerRef = rootRef.child("Followers").child(uid);
        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    followerList.add(ds.getKey());
                }

                uploadPostToFollowers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void uploadPostToFollowers() {

        //this function is for posting to user's notification//

        long now = System.currentTimeMillis();

        for(String userID : followerList){

            DatabaseReference notificationRef = rootRef.child("Notification").child(userID).child(postReference);
            notificationRef.child("Business_name").setValue(headerNameText);
            notificationRef.child("Business_image").setValue(postDownloadUrl);
            notificationRef.child("timestamp").setValue(now);
            notificationRef.child("Business_genre").setValue(headerGenreText);
            notificationRef.child("Business_id").setValue(uid);

        }

        notifyPostSuccess();
    }

    private void notifyPostSuccess() {

        //this function is for making changes after post is successful//
        postDescription.setText("");
        postHeader.setText("");
        postImageText = "";

        //this part is for notifying feed adapter of new post//
        postIdList.add(postRef.getKey());
        postDescriptionList.add(postDescriptionText);
        postHeaderList.add(postHeaderText);
        postImageList.add(postDownloadUrl);
        postTimeList.add(String.valueOf(now));
        postLikeList.add(Integer.toString(0));
        postCommentList.add(Integer.toString(0));

        progressDialog.dismiss();
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        adapter.notifyDataSetChanged();

        popupLayout.setVisibility(View.GONE);
    }

    private Boolean validateInputs() {

        //this function is for checking if post input is valid//

        if(postImageText.length() == 0){
            Toast.makeText(this, "An image is required to make a post!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(postHeaderText.length() == 0){
            postHeader.setError("This cannot be left blank");
            postHeader.requestFocus();
            return false;
        }

        if(postDescriptionText.length() == 0){
            postDescription.setError("This cannot be left blank");
            postDescription.requestFocus();
            return false;
        }

        if(postHeaderText.length() > 70){
            postHeader.setError("Cannot exceed character limit");
            postHeader.requestFocus();
            return false;
        }

        if(postDescriptionText.length() > 160){
            postDescription.setError("Cannot exceed character limit");
            postDescription.requestFocus();
            return false;
        }

        return true;
    }

    private void setupHeaderLayout() {

        //this function is for editing Navigation View header//
        headerGenre.setText(headerGenreText);
        headerName.setText(headerNameText);
        headerNumber.setText(headerNumberText);
        headerAddress.setText(headerAddressText);

        Glide.with(getApplicationContext()).load(headerPictureText).into(headerPicture);

    }

    private void loadBusinessInformation() {

        //this is for getting Business information from DB//

        DatabaseReference genreRef = rootRef.child("Genre_List").child(uid);
        genreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                headerGenreText = dataSnapshot.child("Business_genre").getValue().toString();

                DatabaseReference businessRef = rootRef.child("Businesses").child(headerGenreText).child(uid);
                businessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        headerNameText = dataSnapshot.child("Business_name").getValue().toString();
                        headerAddressText = dataSnapshot.child("Business_address").getValue().toString();
                        headerNumberText = dataSnapshot.child("Business_number").getValue().toString();
                        headerPictureText = dataSnapshot.child("Business_picture").getValue().toString();

                        businessName = headerNameText;
                        businessID = uid;

                        setupRecyclerView();
                        loadFeed();
                        setupHeaderLayout();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void reloadBusinessInformation(){

        //this is for resetting business information after editing from profile//

        DatabaseReference genreRef = rootRef.child("Genre_List").child(uid);
        genreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                headerGenreText = dataSnapshot.child("Business_genre").getValue().toString();

                DatabaseReference businessRef = rootRef.child("Businesses").child(headerGenreText).child(uid);
                businessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        headerNameText = dataSnapshot.child("Business_name").getValue().toString();
                        headerAddressText = dataSnapshot.child("Business_address").getValue().toString();
                        headerNumberText = dataSnapshot.child("Business_number").getValue().toString();
                        headerPictureText = dataSnapshot.child("Business_picture").getValue().toString();

                        businessName = headerNameText;
                        businessID = uid;

                        setupHeaderLayout();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {



                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_GALLERY &&
                resultCode == RESULT_OK){

            postImageText = data.getData().toString();
            postImage.setImageURI(data.getData());

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if user is already logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){

        }

    }
}
