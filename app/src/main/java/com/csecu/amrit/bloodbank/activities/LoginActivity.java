package com.csecu.amrit.bloodbank.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csecu.amrit.bloodbank.R;
import com.csecu.amrit.bloodbank.controllers.AllOperations;
import com.csecu.amrit.bloodbank.interfaces.Sendable;
import com.csecu.amrit.bloodbank.models.Donor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements Sendable {
    EditText etContact, etPassword;
    Button btSubmit;
    String contact = null, password = null;

    AllOperations operations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linkAll();
        operations = new AllOperations(this);

        if (!operations.isNetworkAvailable()) {
            operations.warningToast("Check your Internet Connection");
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void linkAll() {
        etContact = findViewById(R.id.login_et_contact);
        etPassword = findViewById(R.id.login_et_password);
        btSubmit = findViewById(R.id.login_bt_submit);
    }

    public void onLogin(View view) {
        getAllValues();
        boolean status = checkAllValues();
        if (status) {
            if (!operations.isNetworkAvailable()) {
                operations.errorToast("Check your Internet Connection");
            } else {
                checkDonor();
            }
        }
    }

    private void checkDonor() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference donorReference = database.getReference("Donor");

        donorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(contact).exists()) {
                    Donor donor = dataSnapshot.child(contact).getValue(Donor.class);
                    if (password.equals(donor.getPassword())) {
                        operations.successToast("Sign-in successful");
                    } else {
                        operations.errorToast("Password doesn't match. Try again");
                    }
                } else {
                    operations.errorToast("You are not registered");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                operations.errorToast(databaseError.toString());
            }
        });
    }

    @Override
    public void getAllValues() {
        contact = operations.getEncodedString(contact, etContact);
        password = operations.getEncodedString(password, etPassword);
    }

    @Override
    public boolean checkAllValues() {
        if (contact.length() > 0 && password.length() > 0) {
            return true;
        } else {
            if (contact.length() == 0) {
                View focusView = etContact;
                etContact.setError("This field can't be empty");
                focusView.requestFocus();
                return false;
            } else {
                View focusView = etPassword;
                etPassword.setError("This field can't be empty");
                focusView.requestFocus();
                return false;
            }
        }
    }
}
