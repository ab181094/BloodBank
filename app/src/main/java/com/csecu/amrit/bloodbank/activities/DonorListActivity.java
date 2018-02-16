package com.csecu.amrit.bloodbank.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.csecu.amrit.bloodbank.R;
import com.csecu.amrit.bloodbank.adapters.CustomListAdapter;
import com.csecu.amrit.bloodbank.controllers.AllOperations;
import com.csecu.amrit.bloodbank.models.Donor;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DonorListActivity extends AppCompatActivity {
    ArrayList<Donor> donorList;
    AllOperations operations;

    RecyclerView recyclerView;
    CustomListAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference donorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.list_recyclerView);

        donorList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();
        donorRef = database.getReference("Donor");
        operations = new AllOperations(this);
        loadList();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void loadList() {
        donorRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                donorList.add(dataSnapshot.getValue(Donor.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Donor donor = dataSnapshot.getValue(Donor.class);
                int index = getItemIndex(donor);
                donorList.set(index, donor);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Donor donor = dataSnapshot.getValue(Donor.class);
                int index = getItemIndex(donor);
                donorList.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CustomListAdapter(DonorListActivity.this, donorList);
        recyclerView.setAdapter(adapter);
    }

    private int getItemIndex(Donor donor) {
        int index = -1;
        for (int i = 0; i < donorList.size(); i++) {
            if (donorList.get(i).getName().equals(donor.getName())) {
                index = i;
                break;
            }
        }
        return index;
    }
}
