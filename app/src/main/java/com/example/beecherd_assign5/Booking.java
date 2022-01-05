package com.example.beecherd_assign5;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
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
 *  Fragment for booking a desk and view previous bookings.
 *  @author Diarmaid Beecher
 *  @since 20 Apr 2021
 */
public class Booking extends Fragment {
    private static final String TAG = "Booking";

    // Date Picker variables
    TextView mFromDate, mToDate;
    Calendar mFromCalendar = Calendar.getInstance();
    Calendar mToCalendar = Calendar.getInstance();
    Calendar now = Calendar.getInstance();
    private static final String FROM_DATE = "From";
    private static final String TO_DATE = "To";
    private String mFrom, mTo;

    // Firebase Booking variables
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference availableDesks = db.collection("Desks");
    private CollectionReference currentDesks = db.collection("User Bookings");
    private AvailableBookingAdapter adapter;
    private CurrentBookingAdapter adapter2;


    public Booking(){
        // Required empty public constructor
    }

    /**
     * Creates view of fragment.
     * @param inflater initiates layout XML and View objects
     * @param container contains View objects
     * @param savedInstanceStates Bundle containing state information
     * @return view to be displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceStates) {
        View root = inflater.inflate(R.layout.fragment_booking, container, false);


        if (savedInstanceStates != null) {
            mFrom = savedInstanceStates.getString(FROM_DATE, "");
            mTo = savedInstanceStates.getString(TO_DATE, "");
        }

        /*
         *  Date picker
         */
        mFromDate = root.findViewById(R.id.fromDate);
        mFromDate.setText(mFrom);
        mToDate = root.findViewById(R.id.toDate);
        mToDate.setText(mTo);


        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mFromCalendar.set(Calendar.YEAR, year);
                        mFromCalendar.set(Calendar.MONTH, monthOfYear);
                        mFromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay(1);
                    }
                };

                new DatePickerDialog(Objects.requireNonNull(getActivity()), mDateListener,
                        mFromCalendar.get(Calendar.YEAR),
                        mFromCalendar.get(Calendar.MONTH),
                        mFromCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mToCalendar.set(Calendar.YEAR, year);
                        mToCalendar.set(Calendar.MONTH, monthOfYear);
                        mToCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay(2);

                    }
                };

                new DatePickerDialog(Objects.requireNonNull(getActivity()), mDateListener,
                        mToCalendar.get(Calendar.YEAR),
                        mToCalendar.get(Calendar.MONTH),
                        mToCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        /*
         *  Available Booking
         */
        Query query = availableDesks.orderBy("deskNum", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<NewBooking> options = new FirestoreRecyclerOptions.Builder<NewBooking>()
                .setQuery(query, NewBooking.class)
                .build();

        adapter = new AvailableBookingAdapter(getContext(), options, mFrom, mTo);

        RecyclerView recyclerView = root.findViewById(R.id.availableBookingsView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Available Booking adapter set");

        /*
         *  Current Booking
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uID = user.getUid();
            Query query2 = currentDesks.whereEqualTo("userID", uID);
            FirestoreRecyclerOptions<CurrentBooking> options2 = new FirestoreRecyclerOptions.Builder<CurrentBooking>()
                    .setQuery(query2, CurrentBooking.class)
                    .build();

            adapter2 = new CurrentBookingAdapter(getContext(), options2);

            RecyclerView recyclerView2 = root.findViewById(R.id.currentBookingsView);
            recyclerView2.setHasFixedSize(true);
            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView2.setAdapter(adapter2);
            Log.d(TAG, "Current Booking adapter set");
        }

        return root;
    }

    /**
     * Validates calendar selection, if valid sets selection as a String in the view, passes to
     * AvailableBookingAdapter, and calls method to update RecyclerView for selection to be visible.
     * @param id value for Switch statement depending on if From or To date was selected
     */
    private void updateDateDisplay(int id) {
        String SelectedFrom = DateUtils.formatDateTime(getActivity(), mFromCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String SelectedTo = DateUtils.formatDateTime(getActivity(), mToCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        switch (id) {
            case 1:
                try {
                    if (mFromCalendar.compareTo(now) < 0) {
                        Toast.makeText(getContext(), "Invalid selection", Toast.LENGTH_SHORT).show();
                    } else if (mFromCalendar.compareTo(mToCalendar) > 0 && mTo != null) {
                        Toast.makeText(getContext(), "Invalid From selection", Toast.LENGTH_SHORT).show();
                    } else {
                        mFromDate.setText(SelectedFrom);
                        mFrom = SelectedFrom;
                        AvailableBookingAdapter.mFrom = SelectedFrom;
                        updateRecycler();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "NullPointerException: From Date");
                }
                break;
            case 2:
                try {
                    if (mToCalendar.compareTo(mFromCalendar) < 0) {
                        Toast.makeText(getContext(), "Invalid To selection", Toast.LENGTH_SHORT).show();
                    } else {
                        mToDate.setText(SelectedTo);
                        mTo = SelectedTo;
                        AvailableBookingAdapter.mTo = SelectedTo;
                        updateRecycler();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "NullPointerException: To Date");
                }
                break;
        }
    }

    /**
     * Called when a valid calendar selection is made. Creates a new Query and options and updates
     * AvailableBookingAdapter so selected date is visible in RecyclerView.
     * Citation:
     * Adapted from
     * URL: https://medium.com/firebase-developers/update-queries-without-changing-recyclerview-adapter-using-firebaseui-android-32098b3082b2
     * Author: Firebase Developers
     * Accessed: 10 Apr 2021
     */
    private void updateRecycler() {
        Query newQuery = availableDesks.orderBy("deskNum", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<NewBooking> newOptions = new FirestoreRecyclerOptions.Builder<NewBooking>()
                .setQuery(newQuery, NewBooking.class)
                .build();

        adapter.updateOptions(newOptions);
        Log.d(TAG, "Updating Recycler");
    }

    /**
     * Saves String values selected in date picker when exiting app or changing view.
     * Citation:
     * Adapted from
     * URL: https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f
     * Author: Hootsuite Engineering
     * Accessed: 25 Feb 2021
     * @param outState Bundle containing state information
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FROM_DATE, mFrom);
        outState.putString(TO_DATE, mTo);
    }

    /**
     *  Starts adapter listening for changes in Firestore when fragment is in view.
     */
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    /**
     *  Stops adapter listening for changes in Firestore when fragment is not in view.
     */
    @Override
    public void onStop(){
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }
}
