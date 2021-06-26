package com.example.serbigoworker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase_adapter2 extends FirestoreRecyclerAdapter<service_request, Firebase_adapter2.transholder> {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;


    public Firebase_adapter2(@NonNull FirestoreRecyclerOptions<service_request> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull final Firebase_adapter2.transholder holder, int position, @NonNull service_request model) {

        //getting the service rate
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        String transaction_id = snapshot.getId();

        final DocumentReference docRef = fstorage.collection("feedback").document(transaction_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                    Double stars = snapshot.getDouble("service_rating");
                    holder.rate.setRating(stars.floatValue());

                }

            }
        });


        holder.service_date.setText(model.getSdate().toDate().toString());
        //for cost history
        holder.cost.setText(model.getTotal_fee() + ".00 Php");

        //getting customer name
        DocumentReference getting_customer = fstorage.collection("client").document(model.getClient_id());

        getting_customer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    String client_fn = snapshot.getString("first_name") + " " + snapshot.getString("last_name");
                    holder.customer_name.setText(client_fn);

                }
            }
        });



    }


    @NonNull
    @Override
    public Firebase_adapter2.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_holder, parent, false);

        return new Firebase_adapter2.transholder(view);
    }




    public class transholder extends RecyclerView.ViewHolder {

        TextView service_date;
        TextView customer_name;
        TextView cost;
        RatingBar rate;


        public transholder(@NonNull View itemView) {
            super(itemView);

            service_date = itemView.findViewById(R.id.date);
            customer_name = itemView.findViewById(R.id.customer_name);
            cost = itemView.findViewById(R.id.cost);
            rate = itemView.findViewById(R.id.ratingBar);

        }
    }
}
