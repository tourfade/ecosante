package com.kamitsoft.ecosante.client.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.model.PatientInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class Home extends BaseFragment {

    private AutoCompleteTextView patientSearch;
    private PatientOracleAdapter patientOracle;
    private InfoAdapter infoAdapter;
    private LiveData<PatientInfo> currents;
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currents = new MutableLiveData<>();
        currents.observe(this,
                patientInfo -> {
                   infoAdapter.syncData(patientInfo);
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientSearch = view.findViewById(R.id.searchKey);
        patientOracle = new PatientOracleAdapter(getActivity());
        patientSearch.setAdapter(patientOracle);
        infoAdapter = new InfoAdapter(getContext());
        RecyclerView infoList = view.findViewById(R.id.patientdata);
        infoList.setAdapter(infoAdapter);
        initListeners();
        initValues();
    }

    @Override
    public String getTitle() {
        return getString(R.string.menu_home);
    }


    private void initListeners() {

    }
    private void initValues(){
        patientSearch.setOnItemClickListener((parent, view, position, id) -> {
            PatientInfo patient = patientOracle.getItem(position);
            ((MutableLiveData)currents).setValue(patient);
            patientSearch.setText(Utils.formatPatient(getContext(),patient));
        });


    }




}
