package com.kamitsoft.ecosante.client.patient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.badge.BadgeDrawable;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.EntitiesViewModel;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PatientBaseFragment extends Fragment {
    protected PatientActivity contextActivity;
    protected boolean edit;
    protected EcoSanteApp app;
    protected EntitiesViewModel entityModel;
    protected SwipeRefreshLayout swr;
    protected UserInfo connectedUser;
    protected PatientInfo currentPatient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (EcoSanteApp)getActivity().getApplication();
        connectedUser  = app.getCurrentUser();
        currentPatient = app.getCurrentPatient();
        entityModel = ViewModelProviders.of(this).get(EntitiesViewModel.class);

        if(getEntity() != null) {
            entityModel.getDirtyEntities().observe(this, entitySyncs -> {
                for (EntitySync e : entitySyncs) {
                    if (e.getEntity().equalsIgnoreCase(getEntity().getSimpleName())) {
                        app.service().requestSync(getEntity(), null);
                        break;
                    }
                }
            });
        };



    }

    protected Class<?> getEntity() {
        return null;
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

    protected final void requestSync() {
        entityModel.init(getEntity());
        app.service().requestSync(getEntity(),() -> {
            if(swr != null)
                swr.setRefreshing(false);
        });
    }


}
