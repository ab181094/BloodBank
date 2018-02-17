package com.csecu.amrit.bloodbank.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        String donationDate = donorList.get(position).getDate();
        float days = 0;
        if (donationDate != null) {
            String[] values = donationDate.split("[\\-]");
            String result = "";
            for (String s : values) {
                result = result + s + " ";
            }
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            String newDate = year + " " + month + " " + day;
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd");
            try {
                Date firstDate = myFormat.parse(result);
                Date secondDate = myFormat.parse(newDate);
                long diff = secondDate.getTime() - firstDate.getTime();
                days = (diff / (1000 * 60 * 60 * 24));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("NewDate", e.toString());
            }
            if (days > 90) {
                return 1;
            } else
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

            if (donor.getPicture() != null) {
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

            if (donor.getPicture() != null) {
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