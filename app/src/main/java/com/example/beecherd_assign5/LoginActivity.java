package com.example.beecherd_assign5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

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
 * Launches log in screen for app. Currently only available for Google sign in.
 * Citation:
 * Contains code adapted from
 * URL: https://firebase.google.com/docs/auth/android/firebaseui
 * Permission: Apache License
 * Accessed: 01 Mar 2021
 * Adapted from FireChatApp example app.
 * @author Chris Coughlan
 * @author adapted by Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final AuthUI.IdpConfig providers = new AuthUI.IdpConfig.GoogleBuilder().build();
        SignInButton googleSign = findViewById(R.id.signinButton);
        googleSign.setSize(SignInButton.SIZE_WIDE);

        googleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Collections.singletonList(providers))
                                .build(),
                        RC_SIGN_IN);
            }
        });

        Button mSignOut = findViewById(R.id.signoutButton);



        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    /**
     * Overrides the onActivityResult, if launching the firebase AuthUI login is successful returns
     * The login data is returned and activated on firebase including user details.
     * User details are inserted into the applications SharedPreferences for use in other activities.
     * Adapted from FireChatApp example app.
     * @param requestCode denotes the activity for result(we only have one here)
     * @param resultCode true if login successful
     * @param data data returned by the successful activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent MainActivity = new Intent(this, MainActivity.class);
                if (user != null) {
                    startActivity(MainActivity);
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                if (response != null) {
                    Log.i(TAG, "onActivityResult: "+response.getError());
                }
                Toast toast = Toast.makeText(this, "login failed please try again", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /**
     * Signs user out of Firebase and generates a toast to give the user feedback on successful log
     * out of Firebase.
     * Adapted from FireChatApp example app.
     */
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplication(), "Logging Out", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

}
