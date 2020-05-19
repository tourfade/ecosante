package com.kamitsoft.ecosante.client.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.PatientBaseFragment;
import com.kamitsoft.ecosante.client.adapters.AppointmentsAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.ApptEditorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.ApptRequestorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.DocEditorDialog;
import com.kamitsoft.ecosante.constant.PointOfView;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.AppointmentsViewModel;

import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PatientAppointments extends PatientBaseFragment {
    private RecyclerView recyclerView;
    private AppointmentsAdapter adapter;
    private AppointmentsViewModel model;
    private PatientInfo currentPatient;
    private boolean isFABOpen;
    private FloatingActionButton  newDoc,newApp,reqApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_with_actions, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentPatient = app.getCurrentPatient();
        model = ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        model.getPatientData().observe(this, appointmentInfos ->  {
            currentPatient = app.getCurrentPatient();
            if(currentPatient != null) {
                adapter.syncData(appointmentInfos.parallelStream()
                        .filter(d -> !d.isDeleted() && d.getPatientUuid().equals(currentPatient.getUuid()))
                        .collect(Collectors.toList()));
            }
        });
        recyclerView =  view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);


        adapter = new AppointmentsAdapter(getActivity(),connectedUser.getUserType());
        recyclerView.setAdapter(adapter);
        newDoc = view.findViewById(R.id.newItem);
        newDoc.setImageResource(R.drawable.appointment);
        newApp = view.findViewById(R.id.newFiles);
        newApp.setImageResource(android.R.drawable.ic_menu_agenda);
        reqApp = view.findViewById(R.id.newPicture);
        reqApp.setImageResource(R.drawable.calendar_full);

        adapter.setItemClickListener(this::handleItem);

        newDoc.findViewById(R.id.newItem).setOnClickListener(v -> {
            toogleFab();
        });

        newApp.setOnClickListener(view1 -> {
           new ApptEditorDialog(true, null).show(getFragmentManager(),"apptdialog");
        });

        reqApp.setOnClickListener((View v)-> {
            new ApptRequestorDialog(true, null).show(getFragmentManager(),"apptdialog");
        });

    }

    private void toogleFab(){
        if(isFABOpen){
            reqApp.animate().translationY(0);
            newApp.animate().translationY(0);
        }else{
            reqApp.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
            newApp.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        }
        isFABOpen = !isFABOpen;
    }

    private void handleItem(int position, View view) {
        AppointmentInfo item = adapter.getItem(position);
        if(view.getId() == R.id.item_delete){
            item.setDeleted(true);
            model.update(item);
            return;
        }
        if(item.getRecipientUuid().equals(app.getCurrentUser().getUuid())) {
            new ApptEditorDialog(false, item).show(getFragmentManager(), "appdialog");
        }else{
            new ApptRequestorDialog(false, item).show(getFragmentManager(), "appReqdialog");
        }
    }


    @Override
    protected Class<?> getEntity(){
        return  AppointmentInfo.class;
    }




}
