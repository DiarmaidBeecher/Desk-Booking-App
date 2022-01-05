package com.example.beecherd_assign5;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
 * ViewPageAdapter manages which fragment is in view.
 * Citation:
 * ViewPageAdapter adapted from
 * URL: https://www.youtube.com/watch?v=ajVVjuOSlV4
 * Author: Chirag Kachhadiya
 * Accessed: 20 Feb 2021
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class ViewPageAdapter extends FragmentStateAdapter {

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragment = new Fragment();

        //finds the tab position (note array starts at 0)
        position = position + 1;

        //finds the fragment
        switch (position) {
            case 1:
                fragment = new Booking();
                break;
            case 2:
                fragment = new OfficePlan();
                break;
            case 3:
                fragment = new CovidForm();
                break;
            case 4:
                fragment = new User();
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
