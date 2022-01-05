package com.example.beecherd_assign5;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
 * Adapter for Available Bookings
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class AvailableBookingAdapter extends FirestoreRecyclerAdapter<NewBooking, AvailableBookingAdapter.ViewHolder> {

    private static final String TAG = "AvailableBookingAdapter";
    private Context mNewContext;
    public static String mFrom, mTo;

    String uid;
    long fromCheck, toCheck;
    String deskCheck;

    public AvailableBookingAdapter(Context mNewContext, @NonNull FirestoreRecyclerOptions<NewBooking> options, String mFrom, String mTo) {
        super(options);
        Log.d(TAG, "Running AvailableBookingAdapter");
        this.mNewContext = mNewContext;
        AvailableBookingAdapter.mFrom = mFrom;
        AvailableBookingAdapter.mTo = mTo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.booking_item, viewGroup, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull final NewBooking model) {
        Log.d(TAG, "Running onBindViewHolder");
        viewHolder.deskNum.setText(model.getDeskNum());
        viewHolder.fromDate.setText(mFrom);
        viewHolder.toDate.setText(mTo);


        viewHolder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrom != null && mTo != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        uid = user.getUid();

                        long fromLong = dateConvert(mFrom);
                        long toLong = dateConvert(mTo);

                        bookingCheck(fromLong, toLong, model.getDeskNum());
                    }
                } else {
                    Toast.makeText(mNewContext, "Select dates", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView deskNum;
        TextView fromDate;
        TextView toDate;
        Button book;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            deskNum = itemView.findViewById(R.id.deskNum);
            fromDate = itemView.findViewById(R.id.fromDate);
            toDate = itemView.findViewById(R.id.toDate);
            book = itemView.findViewById(R.id.bookButton);
            itemParentLayout = itemView.findViewById(R.id.bookingItemLayout);
            Log.d(TAG, "Running ViewHolder");
        }

    }

    /**
     * Converts String value passed from Booking.java to a long value for storing in Firestore.
     * Citation:
     * Adapted from
     * URL: https://kodejava.org/how-do-i-convert-string-date-to-long-value/
     * Author: Kode Java
     * Accessed: 10 Apr 2021
     * @param convert String value passed from Booking.java
     * @return long values for storing in Firestore
     */
    private Long dateConvert (String convert) {
        Date date = null;
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = formatter.parse(convert);
            assert date != null;
        } catch (ParseException e) {
            Log.d(TAG, String.valueOf(e));
        }
        assert date != null;
        return date.getTime();
    }

    /**
     * Checks database for conflicting bookings, if none then calls method to add to the Desks and
     * User Bookings collections.
     * Contains code adapted from
     * URL: https://firebase.google.com/docs/firestore/query-data/queries
     * Permission: Apache License
     * Accessed: 11 Apr 2021
     * @param from From date selected in Booking.java
     * @param to To date selected in Booking.java
     * @param deskRef Recycler Item desk reference
     */
    private void bookingCheck(long from, long to, String deskRef) {

        fromCheck = from;
        toCheck = to;
        deskCheck = deskRef;
        CollectionReference dbCheck = FirebaseFirestore.getInstance()
                .collection("Desks")
                .document(deskRef)
                .collection("Bookings");

        dbCheck.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Checking Desks database");
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (document.getLong("From") != null && document.getLong("To") != null) {

                            long bookedFrom = document.getLong("From");
                            long bookedTo = document.getLong("To");

                            Log.d(TAG, "bookedFrom is: " + bookedFrom);
                            Log.d(TAG, "bookedTo is: " + bookedTo);
                            if (fromCheck >= bookedFrom && fromCheck <= bookedTo) {
                                Log.d(TAG, "Conflicting From dates");
                                Toast.makeText(mNewContext, "Not available: " + mFrom, Toast.LENGTH_SHORT).show();
                                break;
                            } else if (toCheck >= bookedFrom && toCheck <= bookedTo) {
                                Log.d(TAG, "Conflicting To dates");
                                Toast.makeText(mNewContext, "Not available: " + mTo, Toast.LENGTH_SHORT).show();
                                break;
                            }else if(bookedFrom > fromCheck && bookedFrom < toCheck) {
                                Toast.makeText(mNewContext, "Double booked", Toast.LENGTH_SHORT).show();
                                break;
                            } else if(bookedTo > fromCheck && bookedTo < toCheck) {
                                Toast.makeText(mNewContext, "Double booked", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                addToDatabase(fromCheck, toCheck, deskCheck);
                            }
                        } else {
                            addToDatabase(fromCheck, toCheck, deskCheck);
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * With no conflicting bookings checked by bookingCheck(), adds booking to the the Desks and
     * User Bookings collections.
     * Contains code adapted from
     * URL: https://firebase.google.com/docs/firestore/manage-data/add-data#java_4
     * Permission: Apache License
     * Accessed: 14 Mar 2021
     * @param from From date selected in Booking.java
     * @param to To date selected in Booking.java
     * @param deskRef Recycler Item desk reference
     */
    private void addToDatabase(long from, long to, String deskRef) {

        CollectionReference bookRef = FirebaseFirestore.getInstance()
                .collection("Desks")
                .document(deskRef)
                .collection("Bookings");

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("userID", uid);
        bookingData.put("From", from);
        bookingData.put("To", to);
        bookingData.put("stringFrom", mFrom);
        bookingData.put("stringTo", mTo);
        bookRef.add(bookingData);
        Log.d(TAG, "Adding to Desks database");

        CollectionReference userRef = FirebaseFirestore.getInstance()
                .collection("User Bookings");

        Map<String, Object> userData = new HashMap<>();
        userData.put("userID", uid);
        userData.put("From", from);
        userData.put("To", to);
        userData.put("stringFrom", mFrom);
        userData.put("stringTo", mTo);
        userData.put("deskNum", deskRef);
        userRef.add(userData);
        Log.d(TAG, "Adding to User Bookings database");

        Toast.makeText(mNewContext, "Booking Successful", Toast.LENGTH_SHORT).show();
    }
}
