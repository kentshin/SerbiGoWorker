package com.example.serbigoworker;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class job_request extends Fragment implements Firebase_adapter.OnClick, OnMapReadyCallback {


    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private Firebase_adapter adapter;
    private FirebaseAuth fAuth;

    private GoogleMap mMap;
    double lat, lng;
    private Marker newlocationmarker;

    private StorageReference mStorageRef;
    private EditText report_reason;


    //for this class
    String trans_id;
    String reported_transaction;
    String reported_by;
    String reported_client;
    String reported_client_id;
    String reportreason;





    public job_request() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View transact_view = inflater.inflate(R.layout.fragment_job_request, container, false);

            //map fragment
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            SupportMapFragment fragment = new SupportMapFragment();
            transaction.add(R.id.map , fragment);
            transaction.commit();
            fragment.getMapAsync(this);



            fAuth = FirebaseAuth.getInstance();
            fstorage = FirebaseFirestore.getInstance();
            transaction_recycler = transact_view.findViewById(R.id.service_request_list);

            display_trasactions();
            swipe_delete();




        return  transact_view;

    }



    public void display_trasactions() {

        Query query = reference.whereLessThan("status", 4).whereEqualTo("provider_id",fAuth.getCurrentUser().getUid()).limit(1);
        FirestoreRecyclerOptions<service_request> newoptions = new FirestoreRecyclerOptions.Builder<service_request>()
                .setLifecycleOwner(this)
                .setQuery(query, service_request.class)
                .build();

        adapter = new Firebase_adapter(newoptions, this);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);



    }


    //for accepting request
    @Override
    public void onItemClick2(DocumentSnapshot snapshot, int position) {


        final FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

        trans_id = snapshot.getId();
        long s_status = (long) snapshot.get("status");

        if(s_status == 1) {

            fstorage.collection("transactions")
                    .document(trans_id).update("status",2);

            DocumentReference provider_id = fstorage.collection("transactions").document(trans_id);
            provider_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {

                       String provider_id = snapshot.getString("provider_id");

                     fstorage.collection("provider")
                                .document(provider_id).update("status",2);


                    }

                }
            });

        }

    }



    //for loading message notes
    @Override
    public void onItemClick3(DocumentSnapshot snapshot, int position) {

        FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
        String trans_id = snapshot.getId();

        DocumentReference docRef_msg = fstorage.collection("transactions").document(trans_id);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.additional_details);
        dialog.show();

        final TextView msgbox = (TextView) dialog.findViewById(R.id.add_message);
        Button dismis = (Button) dialog.findViewById(R.id.button);

        //getting the message
        docRef_msg.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {

                       msgbox.setText(snapshot.getString("remarks"));

                }

            }
        });



        dismis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }








    //for viewing location map
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int postion) {

       // String transaction_id = snapshot.getId();

        DocumentReference get_client_id = fstorage.collection("transactions").document(snapshot.getId());
        get_client_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {

                   String client_id = snapshot.getString("client_id");

                    //for client location
                    DocumentReference docRef2 = fstorage.collection("client").document(client_id);
                    docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            if (snapshot.exists()) {

                                BitmapDescriptor icon_custom = BitmapDescriptorFactory.fromResource(R.drawable.myhousepin);
                                double client_lat;
                                double client_lng;

                                client_lat = snapshot.getGeoPoint("geo_location").getLatitude();
                                client_lng = snapshot.getGeoPoint("geo_location").getLongitude();

                                LatLng location = new LatLng(client_lat, client_lng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                                MarkerOptions markerOptions = new MarkerOptions().icon(icon_custom);
                                markerOptions.position(location);
                                markerOptions.title("Customer Location");
                                newlocationmarker = mMap.addMarker(markerOptions);


                            }
                        }
                    });


                }

            }
        });

    }



    @Override
    public void onItemClick4(DocumentSnapshot snaphot, int position) {

        reported_transaction = snaphot.getId();
        reported_by = snaphot.get("provider_id").toString();
        reported_client_id = snaphot.get("client_id").toString();
        trans_id = snaphot.getId();


        report_dialogbox ();

    }



    //call button click
    @Override
    public void onItemClick5(DocumentSnapshot snaphot, int position) {

        Intent intent = new Intent (getActivity(), call2.class);
        startActivity(intent);

    }








    @Override
    public void onMapReady(GoogleMap googleMap) {


        MarkerOptions marker;
        mMap = googleMap;



        //for service_provider
        final DocumentReference docRef = fstorage.collection("geo_location").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {

                    lat = snapshot.getGeoPoint("location").getLatitude();
                    lng = snapshot.getGeoPoint("location").getLongitude();

                    LatLng location = new LatLng (lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                    //mMap.addMarker(new MarkerOptions().position(location).title("Current Address"));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(location);
                    markerOptions.title("My Location Address");
                    newlocationmarker = mMap.addMarker(markerOptions);


                }
            }
        });






    }




    public void dialogbox () {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.additional_details);
        dialog.show();

        TextView msgbox = (TextView) dialog.findViewById(R.id.add_message);
        Button dismis = (Button) dialog.findViewById(R.id.button);


        dismis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }




    void swipe_delete() {

        Query query2 = reference.whereLessThan("status", 2).whereEqualTo("provider_id",fAuth.getCurrentUser().getUid());
        query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Long stat =  document.getLong("status");
                        if (stat <= 2 ) {
                            //for swiping to delete
                            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
                                @Override
                                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                    return false;
                                }

                                @Override
                                public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                                    adapter.deleteTransaction(viewHolder.getAdapterPosition());
                                    Toast.makeText(getActivity(), "Service Request Deleted", Toast.LENGTH_SHORT).show();

                                }
                            }).attachToRecyclerView(transaction_recycler);
                            //for swiping to delete

                        }else {
                            Toast.makeText(getActivity(), "Transaction is already accepted", Toast.LENGTH_SHORT).show();

                        }


                    }


                }else {

                    Toast.makeText(getActivity(), "error getting details", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }



    public void report_dialogbox () {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.report_dialogbox);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button report = (Button)dialog.findViewById(R.id.yes);
        Button cancel = (Button)dialog.findViewById(R.id.no);

        final EditText report_reason = (EditText)dialog.findViewById(R.id.report_reason);



        //cancel
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //report
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reportreason = report_reason.getText().toString();

                final DocumentReference docRef = fstorage.collection( "reported_client_logs"). document(reported_transaction);

                if (reportreason.isEmpty()) {
                    report_reason.setError("Please state the reason why you are reporting this Customer");
                }else {

                    Map<String,Object> reported_client_logs = new HashMap<>();


                    reported_client_logs.put("reported_by", reported_by);
                    reported_client_logs.put("client_id", reported_client_id);
                    reported_client_logs.put("reported_date", Timestamp.now());
                    reported_client_logs.put("report_reason", reportreason);

                    docRef.set(reported_client_logs).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                Toast.makeText(getContext(), "Report Created Successfully!", Toast.LENGTH_SHORT).show();

                                fstorage.collection("transactions")
                                        .document(trans_id).update("status",5);

                                fstorage.collection("provider")
                                        .document(reported_by).update("status",1);

                                dialog.dismiss();

                            }
                        }
                    });

                }

            }
        });


    }




    }
