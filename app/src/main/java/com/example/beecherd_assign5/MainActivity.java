package com.example.beecherd_assign5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
 * Main Activity for app, launches the UI.
 * Citation:
 * Code for TabLayout adapted from
 * URL: https://www.youtube.com/watch?v=ajVVjuOSlV4
 * Author: Chirag Kachhadiya
 * Accessed: 20 Feb 2021
 * Contains code adapted from
 * URL: https://developer.android.com/training/appbar/setting-up
 * Permission: Apache License
 * Accessed: 20 Feb 2021
 * @author Diarmaid Beecher 2021
 * @since 19 Apr 2021
 */
public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPageAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabs);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("BOOKING");
                                break;
                            case 1:
                                tab.setText("OFFICE PLAN");
                                break;
                            case 2:
                                tab.setText("COVID FORM");
                                break;
                            case 3:
                                tab.setText("USER");
                                break;
                        }
                    }
        }
        );
        tabLayoutMediator.attach();
    }
}