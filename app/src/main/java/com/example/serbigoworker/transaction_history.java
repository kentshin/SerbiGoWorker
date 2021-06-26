package com.example.serbigoworker;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class transaction_history extends Fragment {
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private Firebase_adapter2 adapter;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();



    public transaction_history() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View transact_view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        transaction_recycler = transact_view.findViewById(R.id.history_transaction);


        display_history();


        return  transact_view;

    }





    public void display_history() {

        //problem starts here :))

        Query query = reference.whereEqualTo("status", 4).whereEqualTo("provider_id", fAuth.getCurrentUser().getUid()).orderBy("sdate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<service_request> newoptions = new FirestoreRecyclerOptions.Builder<service_request>()
                .setLifecycleOwner(this)
                .setQuery(query, service_request.class)
                .build();

        adapter = new Firebase_adapter2(newoptions);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);

    }









}
