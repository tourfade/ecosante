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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MonitoredPatients extends BaseFragment {
    private RecyclerView recyclerview;
    private WaitingPatientAdapter waitingList;
    private SwipeRefreshLayout swr;

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
        waitingList = new WaitingPatientAdapter(getActivity());
        recyclerview.setAdapter(waitingList);
        waitingList.setItemClickListener((itemPosition, v) -> {
            app.setCurrentPatient(waitingList.getItem(itemPosition));
            Intent i = new Intent(getContext(), PatientActivity.class);
            i.putExtra("isNew",false );
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.exit_to_left);

        });

        (view.findViewById(R.id.newItem)).setOnClickListener(v -> {
           app.newPatient();
           Intent i = new Intent(getContext(), PatientActivity.class);
           i.putExtra("isNew",true );
           startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_up,R.anim.fade_out);

       });

    }

    private void requestSync() {
        getActivity().runOnUiThread(() -> swr.setRefreshing(false));
    }

    @Override
    public String getTitle() {
        return getString(R.string.my_patient);
    }








}
