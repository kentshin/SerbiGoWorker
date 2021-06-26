package com.example.serbigoworker;


import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class worker_profile extends Fragment {

    FirebaseFirestore fstorage;
    FirebaseAuth fAuth;


    TextView Service;
    TextView Fullname;
    TextView Address;
    RatingBar Rating;
    TextView Total_service;
    TextView R_Date;
    TextView Contact;
    Switch On_Off;
    Button Text_availability;



    private StorageReference mStorageRef;
    ImageView worker_image;


    //second frame
    EditText to_date;
    EditText from_date;
    ImageButton date_from;
    ImageButton date_to;
    TextView total_services;
    TextView total_fee;
    TextView commission;
    TextView profit;
    DatePickerDialog.OnDateSetListener setDate;
    Button filter;


    Timestamp time1;
    Timestamp time2;

    public worker_profile() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profile_view = inflater.inflate(R.layout.fragment_worker_profile, container, false);
        //photo


        fAuth = FirebaseAuth.getInstance();
        fstorage = FirebaseFirestore.getInstance();

        Service = profile_view.findViewById(R.id.service_name);
        Fullname = profile_view.findViewById(R.id.full_name);
        Address = profile_view.findViewById(R.id.address);
        Rating = profile_view.findViewById(R.id.ratingBar2);
        Total_service = profile_view.findViewById(R.id.total_service);
        Contact = profile_view.findViewById(R.id.contact);
        R_Date = profile_view.findViewById(R.id.date);
        On_Off = profile_view.findViewById(R.id.on_off_switch);
        Text_availability = profile_view.findViewById(R.id.text_availability);
        worker_image = profile_view.findViewById(R.id.imageView5);


        filter = profile_view.findViewById(R.id.filter);
        to_date = profile_view.findViewById(R.id.to_date);
        from_date = profile_view.findViewById(R.id.from_date);
        date_to = profile_view.findViewById(R.id.date_to);
        date_from = profile_view.findViewById(R.id.date_from);

        total_services = profile_view.findViewById(R.id.total_services);
        total_fee = profile_view.findViewById(R.id.total_fee);
        commission = profile_view.findViewById(R.id.commission);
        profit = profile_view.findViewById(R.id.profit);



        display_profile();
        date_selection();
        date_filter();
        switch_on_off();
        download_photo();


        return profile_view;
    }


    //getting provider profile
    void display_profile() {


        DocumentReference provider = fstorage.collection("provider").document(fAuth.getCurrentUser().getUid());

        //populating

        Contact.setText(fAuth.getCurrentUser().getPhoneNumber());

        //converting the metadata timestamp long to date
        Long date_reg = fAuth.getCurrentUser().getMetadata().getCreationTimestamp();
        String dateString = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date_reg));
        R_Date.setText(dateString);


        provider.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {

                    String service_name = snapshot.getString("service");
                    String full_name = snapshot.getString("first_name") + " " + snapshot.getString("last_name");
                    String status = snapshot.getLong("status").toString();
                    Address.setText(snapshot.getString("address"));
                    Rating.setRating(snapshot.getDouble("rate").floatValue());
                    Fullname.setText(full_name);


                    //Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();

                    if (status.equals("1")) {
                        On_Off.setChecked(true);
                        Text_availability.setText("Available");
                        Text_availability.setBackgroundColor(Color.parseColor("#FF35D33B"));


                    } else if (status.equals("2")) {
                        On_Off.setChecked(false);
                        Text_availability.setText("Unavailable");
                        Text_availability.setBackgroundColor(Color.parseColor("gray"));
                       // Text_availability.setTextColor(Color.parseColor("gray"));

                    } else if (status.equals("0")) {
                        On_Off.setChecked(false);
                        On_Off.setEnabled(false);
                        Text_availability.setText("Not Activated");
                        Text_availability.setBackgroundColor(Color.parseColor("red"));
                       // Text_availability.setTextColor(Color.parseColor("red"));
                       //

                    }   else if (status.equals("3")) {
                        On_Off.setChecked(false);
                        On_Off.setEnabled(false);
                        Text_availability.setText("Disabled");
                        Text_availability.setBackgroundColor(Color.parseColor("red"));
                        //Text_availability.setTextColor(Color.parseColor("red"));

                    }


                    if (service_name.equals("1")) {

                        Service.setText("Plumbing");

                    }
                    if (service_name.equals("2")) {
                        Service.setText("Carpentry");


                    }
                    if (service_name.equals("3")) {

                        Service.setText("Home Paint");

                    }
                    if (service_name.equals("4")) {

                        Service.setText("Laundry");

                    }
                    if (service_name.equals("5")) {

                        Service.setText("Nail Cleaning");

                    }
                    if (service_name.equals("6")) {

                        Service.setText("Electrical Services");

                    }
                    if (service_name.equals("7")) {

                        Service.setText("Tv Repair");

                    }
                    if (service_name.equals("8")) {

                        Service.setText("Refregirator Repair");

                    }
                    if (service_name.equals("9")) {

                        Service.setText("Washing Machine Repair");

                    }

                }
            }
        });


        CollectionReference number_of_transaction = fstorage.collection("transactions");

        final Query totalservice = number_of_transaction.whereEqualTo("status", 4).whereEqualTo("provider_id", fAuth.getCurrentUser().getUid());
        totalservice.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String total_number_of_service = String.valueOf(queryDocumentSnapshots.size());
                Total_service.setText(total_number_of_service);
            }
        });


    }


    void date_selection() {

        //for second frame calendar

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        final Calendar calendar1 = Calendar.getInstance();
        final int year1 = calendar.get(Calendar.YEAR);
        final int month1 = calendar.get(Calendar.MONTH);
        final int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat dateString = new SimpleDateFormat("MM-dd-yyyy");


        date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = month + "-" + day + "-" + year;
                        from_date.setText(date);

                        Date date1;

                        try {
                            date1 = dateString.parse(String.valueOf(date));
                            time1 = new Timestamp(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, year, month, day);

                datePickerDialog.show();

            }
        });


        date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int month1, int day1) {
                        month1 = month1 + 1;
                        String date = month1 + "-" + day1 + "-" + year1;
                        to_date.setText(date);


                        Date date2;
                        try {
                            date2 = dateString.parse(String.valueOf(date));
                            time2 = new Timestamp(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, year1, month1, day1);

                datePickerDialog.show();

            }
        });

        //end date time

    }


    void date_filter() {

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (to_date.getText().toString().isEmpty() || from_date.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Select Date", Toast.LENGTH_SHORT).show();

                } else {


                    //Toast.makeText(getActivity(), date1.toString(), Toast.LENGTH_SHORT).show();
                    CollectionReference transaction_reference = fstorage.collection("transactions");


                    Query query1 = transaction_reference.whereEqualTo("status", 4).whereEqualTo("provider_id", fAuth.getCurrentUser().getUid()).whereGreaterThanOrEqualTo("sdate", time1)
                            .whereLessThanOrEqualTo("sdate", time2);

                    query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                double total_fees = 0;
                                double comision;
                                double proft;

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    double total_fee_new = documentSnapshot.getLong("total_fee");
                                    total_fees = total_fees + total_fee_new;

                                }

                                comision = total_fees * .10;
                                proft = total_fees - comision;


                                String total_number_of_service = String.valueOf(task.getResult().size());

                                total_services.setText(total_number_of_service);
                                total_fee.setText(String.valueOf(total_fees) + " " + "Php");
                                commission.setText(String.valueOf(comision) + " " + "Php");
                                profit.setText(String.valueOf(proft) + " " + "Php");

                            }


                        }
                    });


                }
            }
        });

    }


    void switch_on_off() {
        On_Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (On_Off.isChecked()) {

                    fstorage.collection("provider")
                            .document(fAuth.getCurrentUser().getUid()).update("status", 1);
                    Text_availability.setText("Available");
                    Text_availability.setBackgroundColor(Color.parseColor("#FF35D33B"));


                } else {

                    fstorage.collection("provider")
                            .document(fAuth.getCurrentUser().getUid()).update("status", 2);
                    Text_availability.setText("Unavailable");
                    Text_availability.setBackgroundColor(Color.parseColor("gray"));
                   // Text_availability.setTextColor(Color.parseColor("gray"));

                }


            }
        });


    }


    void download_photo() {

        String uid = fAuth.getCurrentUser().getUid();
        //Toast.makeText(getContext(), uid, Toast.LENGTH_LONG).show();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child( uid + ".jpeg");
       mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               if (uri != null) {

                   Glide.with(worker_image.getContext()).load(uri).into(worker_image);
               }

           }
       });


    }



    @Override
    public void onStart() {


        super.onStart();
        display_profile ();

    }







}