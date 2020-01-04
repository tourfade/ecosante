package com.kamitsoft.ecosante.client.patient;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.PatientBaseFragment;
import com.kamitsoft.ecosante.client.adapters.PatientEncountersAdapter;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PatientEncounters extends PatientBaseFragment {
    private RecyclerView recyclerview;
    private PatientEncountersAdapter encounterAdapter;
    private FloatingActionButton newEncounter;
    private EncountersViewModel model;
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        encounterAdapter = new PatientEncountersAdapter(getActivity());
        recyclerview.setAdapter(encounterAdapter);
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        model.getEncounters().observe(this, encounters-> {
            Stream<EncounterInfo> data = encounters
                    .stream()
                    .filter(e -> !e.isDeleted()
                            && e.getPatientUuid().equals(app.getCurrentPatient().getUuid()));
            encounterAdapter.syncData(data.collect(Collectors.toList()));
        });

        encounterAdapter.setItemClickListener((itemPosition, v) -> {
            if(v.getId() == R.id.item_delete){
                EncounterInfo item = encounterAdapter.getItem(itemPosition);
                item.setDeleted(true);
                model.update(item);
                return;
            }
            if(v.getId() == R.id.item_edit){
                Toast.makeText(getContext(),"Nothing to edit", Toast.LENGTH_LONG).show();
                return;
            }
            Intent i = new Intent(getContext(), Encounter.class);
            app.setCurrentEncounter(encounterAdapter.getItem(itemPosition));
            startActivityForResult(i,102);
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        });
        newEncounter = view.findViewById(R.id.newItem);
        newEncounter.setOnClickListener((View v)-> {
            Intent i = new Intent(getContext(), Encounter.class);
            i.putExtra("isNew", true);
            app.setNewEncounter();
            startActivityForResult(i,101);
            getActivity().overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
        });

    }

    @Override
    protected Class<?> getEntity() {
        return EncounterInfo.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        app.exitEncounter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        encounterAdapter.removeListener();
    }


}
