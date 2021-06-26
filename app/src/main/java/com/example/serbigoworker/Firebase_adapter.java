package com.example.serbigoworker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Firebase_adapter  extends FirestoreRecyclerAdapter<service_request, Firebase_adapter.transholder> {

    private OnClick onclick;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    private StorageReference mStorageRef;


//for clicking events
    public Firebase_adapter(@NonNull FirestoreRecyclerOptions<service_request> options, OnClick onclick) {
        super(options);

        this.onclick = onclick;
    }




    //populating the layout xml
    @Override
    protected void onBindViewHolder(@NonNull final Firebase_adapter.transholder holder, int position, @NonNull service_request model) {

        global_variables.client_id = model.getClient_id();
        long stat = model.getStatus();
        holder.total_fee.setText(model.getTotal_fee()+ " " + "Php");


        identification_class.client_id = model.getClient_id();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child( model.getClient_id()+ ".jpeg");

        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    Glide.with(holder.worker_photo.getContext()).load(uri).into(holder.worker_photo);

                }
            }
        });


        if (stat == 1) {

            holder.status_button.setText("Accept?");

        } else if (stat == 2) {

            holder.status_button.setText("Accepted");
            holder.status_button.setBackgroundColor(Color.parseColor("#8CDD81"));
            holder.status_button.setEnabled(false);

        } else if (stat == 3) {

            holder.status_button.setText("On Service");
            holder.status_button.setBackgroundColor(Color.parseColor("#63AB62"));
            holder.status_button.setEnabled(false);
            holder.report_button.setVisibility(View.VISIBLE);
        }

        DocumentReference client_id = fstorage.collection("client").document(model.getClient_id());

       client_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {

                    String customer_fn = snapshot.getString("first_name") + " " +  snapshot.getString("last_name");
                    String address = snapshot.getString("home_address");

                    holder.customer_name.setText(customer_fn);
                    holder.address.setText(address);




                }

            }
        });


        holder.sdate.setText(model.getSdate().toDate().toString());





    }





    @NonNull
    @Override
    public Firebase_adapter.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_container, parent, false);

        return new Firebase_adapter.transholder(view);
    }

    //for deleting transaction request
    public void deleteTransaction(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }




    public class transholder extends RecyclerView.ViewHolder {


        TextView customer_name;
        TextView address;
        TextView total_fee;
        TextView sdate;
        Button status_button;
        ImageButton message;
        ImageView worker_photo;
        ImageButton report_button;
        ImageButton call_button;

        public transholder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.customer_name);
            address = itemView.findViewById(R.id.customer_address);
            total_fee = itemView.findViewById(R.id.total_fee);
            sdate= itemView.findViewById(R.id.date);
            status_button = itemView.findViewById(R.id.service_status);
            message = itemView.findViewById(R.id.message);
            worker_photo = itemView.findViewById(R.id.worker_photo);
            report_button = itemView.findViewById(R.id.report_button);
            call_button = itemView.findViewById(R.id.call);


            //for viewing geo_location
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick(getSnapshots().getSnapshot(postion), postion);


                    }

                }
            });



            //for accepting work
            status_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){


                        onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);




                    }

                }
            });



            //for viewing details
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){


                        onclick.onItemClick3(getSnapshots().getSnapshot(postion), postion);
                    }

                }
            });


            //for report button
            report_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick4(getSnapshots().getSnapshot(postion), postion);

                    }

                }
            });



            //for call button
            call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick5(getSnapshots().getSnapshot(postion), postion);


                    }

                }
            });








        }
    }




    public interface OnClick {

        void onItemClick(DocumentSnapshot snapshot, int position);
        void onItemClick2(DocumentSnapshot snapshot, int postion);
        void onItemClick3(DocumentSnapshot snapshot, int postion);
        void onItemClick4(DocumentSnapshot snapshot, int postion);
        void onItemClick5(DocumentSnapshot snapshot, int postion);
    }


    public void setOnclick (Firebase_adapter.OnClick onclick) {
        this.onclick = onclick;


    }









}
