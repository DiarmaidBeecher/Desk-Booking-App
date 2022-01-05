package com.example.beecherd_assign5;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Adapter for Current Bookings
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class CurrentBookingAdapter extends FirestoreRecyclerAdapter<CurrentBooking, CurrentBookingAdapter.ViewHolder> {

    private static final String TAG = "CurrentBookingAdapter";
    private Context mNewContext;
    String fromCheck, toCheck;


    public CurrentBookingAdapter(Context mNewContext, @NonNull FirestoreRecyclerOptions<CurrentBooking> options) {
        super(options);
        Log.d(TAG, "Running CurrentBookingAdapter");
        this.mNewContext = mNewContext;

    }

    @NonNull
    @Override
    public CurrentBookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.booking_item, viewGroup, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentBookingAdapter.ViewHolder viewHolder, final int position, @NonNull final CurrentBooking model) {
        Log.d(TAG, "Running onBindViewHolder");
        viewHolder.deskNum.setText(model.getDeskNum());
        Log.d(TAG, "From date long: " + model.getFrom());
        viewHolder.fromDate.setText(model.getStringFrom());
        Log.d(TAG, "To date long: " + model.getTo());
        viewHolder.toDate.setText((model.getStringTo()));

        viewHolder.cancel.setText(R.string.cancel);

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getSnapshots().getSnapshot(position).getReference().delete();
                clearAvailableBooking(model.getDeskNum(), model.getStringFrom(), model.getStringTo());
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView deskNum;
        TextView fromDate;
        TextView toDate;
        Button cancel;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            deskNum = itemView.findViewById(R.id.deskNum);
            fromDate = itemView.findViewById(R.id.fromDate);
            toDate = itemView.findViewById(R.id.toDate);
            cancel = itemView.findViewById(R.id.bookButton);
            itemParentLayout = itemView.findViewById(R.id.bookingItemLayout);
            Log.d(TAG, "Running ViewHolder");
        }

    }

    /**
     * Queries Desks collection for booking corresponding to that in the User Bookings collection
     * and calls method to delete the booking if found.
     * Contains code adapted from
     * URL: https://firebase.google.com/docs/firestore/query-data/queries
     * Permission: Apache License
     * Accessed: 11 Apr 2021
     * @param deskRef Recycler item desk reference
     * @param from Booked From date
     * @param to Booked To date
     */
    private void clearAvailableBooking(String deskRef, String from, String to) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uID = user.getUid();

            fromCheck = from;
            toCheck = to;

            CollectionReference bookRef = FirebaseFirestore.getInstance()
                    .collection("Desks")
                    .document(deskRef)
                    .collection("Bookings");

            bookRef.whereEqualTo("userID", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Checking Desks database");
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            if (document.getLong("From") != null && document.getLong("To") != null) {

                                String bookedFrom = document.getString("stringFrom");
                                String bookedTo = document.getString("stringTo");

                                Log.d(TAG, "bookedFrom is: " + bookedFrom);
                                Log.d(TAG, "bookedTo is: " + bookedTo);

                                if (fromCheck.equals(bookedFrom) && toCheck.equals(bookedTo)) {
                                    deleteBooking(document.getReference());
                                    Toast.makeText(mNewContext, "Not available: ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    /**
     * Deletes booking document once retrieved
     * Contains code adapted from
     * URL: https://firebase.google.com/docs/firestore/manage-data/delete-data
     * Permission: Apache License
     * Accessed: 11 Apr 2021
     * @param doc Document reference of booking
     */
    private void deleteBooking(DocumentReference doc) {
        Log.d(TAG, "DocumentReference is: " + doc);
        doc.delete();
        Toast.makeText(mNewContext, "Booking Cancelled", Toast.LENGTH_SHORT).show();
    }

}
