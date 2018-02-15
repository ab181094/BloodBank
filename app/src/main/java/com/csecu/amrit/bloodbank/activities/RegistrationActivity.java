package com.csecu.amrit.bloodbank.activities;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.csecu.amrit.bloodbank.R;
import com.csecu.amrit.bloodbank.controllers.AllOperations;
import com.csecu.amrit.bloodbank.interfaces.Sendable;
import com.csecu.amrit.bloodbank.models.Donor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class RegistrationActivity extends AppCompatActivity implements Sendable {
    EditText etName, etEmail, etContact, etPassword, etConfirmPassword, etAge, etAddress;
    Spinner spGender, spBloodGroup;
    TextView tvPicture, tvDate;
    Button btPicture, btDate, btSubmit;

    String name = null, email = null, contact = null, password = null, rePassword = null, age = null,
            address = null, gender = null, blood = null, picture = null, date = null;
    int day = 0, month = 0, year = 0;
    Uri uri = null;

    AllOperations operations;

    private static final int GALLERY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linkAll();
        operations = new AllOperations(getApplicationContext());

        if (!operations.isNetworkAvailable()) {
            operations.warningToast("Check your Internet Connection");
        }

        setInitialData();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void setInitialData() {
        String[] genders = getResources().getStringArray(R.array.gender_arrays);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                R.layout.registration_spinner_item, R.id.spinner_item, genders);
        spGender.setAdapter(genderAdapter);

        String[] groups = getResources().getStringArray(R.array.blood_group_arrays);

        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(this,
                R.layout.registration_spinner_item, R.id.spinner_item, groups);
        spBloodGroup.setAdapter(bloodGroupAdapter);

        /*final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        date = year + "-" + (month + 1) + "-" + day;
        tvDate.setText(date);
        tvDate.setVisibility(View.VISIBLE);*/
    }

    private void linkAll() {
        etName = findViewById(R.id.registration_et_name);
        etEmail = findViewById(R.id.registration_et_email);
        etContact = findViewById(R.id.registration_et_contact);
        etPassword = findViewById(R.id.registration_et_password);
        etConfirmPassword = findViewById(R.id.registration_et_confirmPassword);
        etAge = findViewById(R.id.registration_et_age);
        etAddress = findViewById(R.id.registration_et_address);

        spGender = findViewById(R.id.registration_spinner_gender);
        spBloodGroup = findViewById(R.id.registration_spinner_bloodGroup);

        tvPicture = findViewById(R.id.registration_tv_picture);
        tvDate = findViewById(R.id.registration_tv_date);

        btPicture = findViewById(R.id.registration_bt_picture);
        btDate = findViewById(R.id.registration_bt_date);
        btSubmit = findViewById(R.id.registration_bt_submit);
    }

    public void onSubmit(View view) {
        getAllValues();
        boolean status = checkAllValues();
        if (status) {
            if (!operations.isNetworkAvailable()) {
                operations.errorToast("Check your Internet Connection");
            } else {
                addDonor();
            }
        }
    }

    private void addDonor() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference donorReference = database.getReference("Donor");

        donorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(contact).exists()) {
                    operations.errorToast("Donor already exists");
                } else {
                    Donor donor = new Donor(name, email, contact, password, age, address, gender,
                            blood, picture, date);
                    donorReference.child(contact).setValue(donor);

                    if (uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = reference.child("Photos").child(picture);
                        imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                operations.successToast("Image uploaded successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                operations.errorToast("Image upload failed");
                            }
                        });
                    } else {
                        operations.warningToast("You've not selected a photo");
                    }
                    operations.successToast("You have successfully registered as donor");
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                operations.errorToast(databaseError.toString());
            }
        });
    }

    public void onTakePhoto(View view) {
        if (mayRequestStorage()) {
            if (etContact.getText().toString().length() > 0) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            } else {
                View focusView = etContact;
                etContact.setError("This field can't be empty");
                focusView.requestFocus();
            }
        } else {
            operations.errorToast("Set the permissions first");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            if (uri != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        File file = new File(picturePath);
                        String filePath = file.getName();
                        picture = operations.getEncodedString(contact, etContact)
                                + filePath.substring(filePath.lastIndexOf("."));
                        tvPicture.setText(picture);
                        tvPicture.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    operations.errorToast("" + e.toString());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }

    private boolean mayRequestStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            Snackbar.make(btPicture, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
        }
        return false;
    }

    public void onPickDate(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog dialog =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        tvDate.setText(date);
                        tvDate.setVisibility(View.VISIBLE);
                    }
                }, year, month, day);
        dialog.show();
    }

    @Override
    public void getAllValues() {
        name = operations.getEncodedString(name, etName);
        email = operations.getEncodedString(email, etEmail);
        contact = operations.getEncodedString(contact, etContact);
        password = operations.getEncodedString(password, etPassword);
        rePassword = operations.getEncodedString(rePassword, etConfirmPassword);
        age = operations.getEncodedString(age, etAge);
        address = operations.getEncodedString(address, etAddress);
        gender = (String) spGender.getSelectedItem();
        blood = (String) spBloodGroup.getSelectedItem();
    }

    @Override
    public boolean checkAllValues() {
        View focusView;
        if (password.equals(rePassword) && password.length() > 4) {
            if (name.length() > 0 && contact.length() > 0 && password.length() > 0) {
                return true;
            } else {
                if (name.length() == 0) {
                    focusView = etName;
                    etName.setError("This field can't be empty");
                    focusView.requestFocus();
                    return false;
                } else if (contact.length() == 0) {
                    focusView = etContact;
                    etContact.setError("This field can't be empty");
                    focusView.requestFocus();
                    return false;
                } else {
                    focusView = etPassword;
                    etPassword.setError("This field can't be empty");
                    focusView.requestFocus();
                    return false;
                }
            }
        } else {
            if (password.length() < 5) {
                focusView = etPassword;
                etPassword.setError("Passwords must have more than 5 characters");
                focusView.requestFocus();
                return false;
            } else {
                focusView = etConfirmPassword;
                etConfirmPassword.setError("Passwords don't match each other");
                focusView.requestFocus();
                return false;
            }
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
