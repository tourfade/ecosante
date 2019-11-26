package com.kamitsoft.ecosante.client;

import android.content.Context;
import android.os.Bundle;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.client.patient.PatientActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PatientBaseFragment extends Fragment {
    protected  PatientActivity contextActivity;
    protected boolean edit;
    protected EcoSanteApp app;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (EcoSanteApp)getActivity().getApplication();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context !=null && context instanceof PatientActivity) {
            contextActivity = (PatientActivity) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contextActivity = null;
    }


}
