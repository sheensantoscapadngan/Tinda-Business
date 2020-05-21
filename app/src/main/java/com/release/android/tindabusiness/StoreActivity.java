package com.release.android.tindabusiness;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView add,popupBack,back;
    private ConstraintLayout popupLayout;
    private EditText itemName, itemPrice;
    private TextView addItem;
    private ArrayList<String> itemList,itemIdList,priceList;
    private DatabaseReference rootRef;
    private FirebaseAuth firebaseAuth;
    private String itemNameText,itemPriceText,uid;
    private StorePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setupViews();
        loadItemsFromDatabase();
        activateListeners();
        setupRecyclerView();

    }

    private void setupRecyclerView() {

        adapter = new StorePageAdapter(itemList,priceList,this,itemIdList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void setupViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewStore);
        add = (ImageView) findViewById(R.id.imageViewStoreAdd);
        back = (ImageView) findViewById(R.id.imageViewStoreBack);

        popupLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutStorePopup);
        popupBack = (ImageView) findViewById(R.id.imageViewStorePopupBack);
        addItem = (TextView) findViewById(R.id.textViewStoreAddItem);
        itemName = (EditText) findViewById(R.id.editTextStoreItemName);
        itemPrice = (EditText) findViewById(R.id.editTextStoreItemPrice);

        itemList = new ArrayList<>();
        priceList = new ArrayList<>();
        itemIdList = new ArrayList<>();

        rootRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

    }

    private void activateListeners() {

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupLayout.setVisibility(View.VISIBLE);


            }
        });

        popupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupLayout.setVisibility(View.GONE);

            }
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemNameText = itemName.getText().toString();
                itemPriceText = itemPrice.getText().toString();

                addItemToShopList();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void addItemToShopList() {

        //this function is for adding item to shop and also adding new item to recyclerView

        DatabaseReference shopRef = rootRef.child("Shop").child(uid).push();
        shopRef.child("Item_name").setValue(itemNameText);
        shopRef.child("Item_price").setValue(itemPriceText);

        itemIdList.add(shopRef.getKey());
        itemList.add(itemNameText);
        priceList.add(itemPriceText);

        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Item added to your shop", Toast.LENGTH_SHORT).show();

        popupLayout.setVisibility(View.GONE);

    }

    private void loadItemsFromDatabase() {

        //this function is for loading all the items from the shop//

        DatabaseReference shopRef = rootRef.child("Shop").child(uid);
        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    itemList.add(ds.child("Item_name").getValue().toString());
                    priceList.add(ds.child("Item_price").getValue().toString());
                    itemIdList.add(ds.getKey());

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
