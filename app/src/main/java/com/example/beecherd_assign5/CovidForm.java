package com.example.beecherd_assign5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Fragment for Covid questionnaire form. Not yet available Version 1.
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class CovidForm extends Fragment {

    public CovidForm(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSates) {
        return inflater.inflate(R.layout.fragment_covid_form, container, false);
    }
}
