package com.release.android.tindabusiness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StorePageAdapter extends RecyclerView.Adapter<StorePageAdapter.ViewHolder> {

    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<String> priceList = new ArrayList<>();
    private Context context;
    private ArrayList<String> itemIdList = new ArrayList<>();

    public StorePageAdapter(ArrayList<String> itemList, ArrayList<String> priceList,Context context,ArrayList<String> itemIdList) {
        this.itemList = itemList;
        this.priceList = priceList;
        this.context = context;
        this.itemIdList = itemIdList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainlistlayout,viewGroup,false);
        StorePageAdapter.ViewHolder viewHolder = new StorePageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(itemList.get(i));
        viewHolder.price.setText("P"+priceList.get(i));
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to remove " + itemList.get(i) + " from your shop?");
                builder.setTitle("Confirmation for Removal");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("Shop").child(uid).child(itemIdList.get(i));
                        shopRef.setValue(null);

                        itemIdList.remove(i);
                        itemList.remove(i);
                        priceList.remove(i);

                        notifyDataSetChanged();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout layout;
        private TextView name,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayoutMainList);
            name = (TextView) itemView.findViewById(R.id.textViewMainListName);
            price = (TextView) itemView.findViewById(R.id.textViewMainListPrice);

        }
    }

}
