package com.csecu.amrit.bloodbank.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CustomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Donor> donorList;
    private AllOperations operations;
    private Context context;

    public CustomListAdapter(Context context, ArrayList<Donor> donorList) {
        this.donorList = donorList;
        operations = new AllOperations(context);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (donorList.get(position).getDate() != null) {
            return 0;
        } else
            return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            final InvalidViewHolder viewHolder = (InvalidViewHolder) holder;
            Donor donor = donorList.get(position);

            String name = donor.getName();
            String group = donor.getBlood();

            name = operations.decodeString(name);

            viewHolder.tvName.setText(name);
            viewHolder.tvGroup.setText(group);

            if (donor.getPicture().length() > 0) {
                final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                mStorageRef.child("Photos/" + donor.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .into(viewHolder.imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        operations.errorToast("Failed to load images");
                    }
                });
            }
        } else {
            final ValidViewHolder viewHolder = (ValidViewHolder) holder;
            Donor donor = donorList.get(position);

            String name = donor.getName();
            String group = donor.getBlood();

            name = operations.decodeString(name);

            viewHolder.tvName.setText(name);
            viewHolder.tvGroup.setText(group);

            if (donor.getPicture().length() > 0) {
                final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                mStorageRef.child("Photos/" + donor.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .into(viewHolder.imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        operations.errorToast("Failed to load images");
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_invalid_list,
                    parent, false);
            return new InvalidViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_valid_list,
                    parent, false);
            return new ValidViewHolder(view);
        }
    }

    public Donor getItem(int position) {
        return donorList.get(position);
    }

    /**
     * View holder class
     */
    class InvalidViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView tvName;
        private TextView tvGroup;

        private InvalidViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.invalid_user_image);
            tvName = view.findViewById(R.id.invalid_tv_name);
            tvGroup = view.findViewById(R.id.invalid_tv_group);
        }
    }

    class ValidViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView tvName;
        private TextView tvGroup;

        private ValidViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.valid_user_image);
            tvName = view.findViewById(R.id.valid_tv_name);
            tvGroup = view.findViewById(R.id.valid_tv_group);
        }
    }
}