package com.kamitsoft.ecosante.client.physician;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.WaitingPatientAdapter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SupervisedPatients extends BaseFragment {
    private RecyclerView recyclerview;
    private WaitingPatientAdapter adapter;
    private View add;
    private PatientsViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_with_add, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        adapter = new WaitingPatientAdapter(getActivity());
        recyclerview.setAdapter(adapter);
        model = ViewModelProviders.of(this).get(PatientsViewModel.class);

        model.getAllDatas().observe(this, patientInfos -> {
            patientInfos = patientInfos.stream()
                    .filter(p-> p.getDistrictUuid()!=null && p.getDistrictUuid().equals(connectedUser.getDistrictUuid()))
                    .collect(Collectors.toList());
            adapter.syncData(patientInfos);
        });


        adapter.setItemClickListener((itemPosition, v) -> {
            app.setCurrentPatient(adapter.getItem(itemPosition));
            Intent i = new Intent(getContext(), PatientActivity.class);
            i.putExtra("isNew",false );
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.exit_to_left);

        });
        add = view.findViewById(R.id.newItem);
        if(add !=null)
        add.setOnClickListener(v -> {
           app.newPatient();
           Intent i = new Intent(getContext(), PatientActivity.class);
           i.putExtra("isNew",true );
           startActivity(i);
           getActivity().overridePendingTransition(R.anim.slide_up,R.anim.fade_out);

       });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(add !=null)
        add.setVisibility(UserType.isAdmin(connectedUser.getUserType())?View.VISIBLE:View.GONE);

    }


    @Override
    protected Class<?> getEntity(){
        return  PatientInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.my_patient);
    }








}
