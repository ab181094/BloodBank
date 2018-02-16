package com.csecu.amrit.bloodbank.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.csecu.amrit.bloodbank.R;
import com.csecu.amrit.bloodbank.controllers.AllOperations;
import com.csecu.amrit.bloodbank.models.Donor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Amrit on 2/16/2018.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder> {
    private ArrayList<Donor> donorList;
    private AllOperations operations;
    private Context context;

    public CustomListAdapter(Context context, ArrayList<Donor> donorList) {
        this.donorList = donorList;
        operations = new AllOperations(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Donor donor = donorList.get(position);

        String name = donor.getName();
        String group = donor.getBlood();

        name = operations.decodeString(name);

        holder.tvName.setText(name);
        holder.tvGroup.setText(group);

        if (donor.getPicture().length() > 0) {
            holder.linearLayout.setBackgroundColor(Color.GREEN);
        } else {
            holder.linearLayout.setBackgroundColor(Color.RED);
        }

        if (donor.getPicture().length() > 0) {
            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child("Photos/" + donor.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .into(holder.imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    operations.errorToast("Failed to load images");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_list,
                parent, false);
        return new MyViewHolder(view);
    }

    public Donor getItem(int position) {
        return donorList.get(position);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView tvName;
        private TextView tvGroup;
        private LinearLayout linearLayout;

        private MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.user_image);
            tvName = view.findViewById(R.id.tv_name);
            tvGroup = view.findViewById(R.id.tv_group);
            linearLayout = view.findViewById(R.id.layout_availability);
        }
    }
}
