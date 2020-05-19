package com.kamitsoft.ecosante.client.nurse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.UserEncountersAdapter;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserEncounters extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerview;
    private UserEncountersAdapter encountersAdapter;
    private EncountersViewModel model;
    private ProgressBar progress;
    private BottomNavigationView navBar;
    private boolean archived;
    private int page = 0;
    private Observer<? super List<EncounterHeaderInfo>> archive  = encounters-> {
        if(encounters ==null || encounters.size()<= 0){
            return;
        }
        Stream<EncounterHeaderInfo> data = encounters .stream()
                .filter( e -> e.getMonitor().monitorUuid.equals(app.getCurrentUser().getUuid())
                        && e.currentStatus().status == StatusConstant.ARCHIVED.status);

        List<EncounterHeaderInfo> list = data.collect(Collectors.toList());
        encountersAdapter.syncData(list);
        if(archived){
            page = (list.size()/25);
        }
    };
    private Observer<? super List<EncounterHeaderInfo>> running  = encounters-> {
        if(encounters ==null || encounters.size()<= 0){
            return;
        }
        Stream<EncounterHeaderInfo> data = encounters
                .stream()
                .filter(e -> e.getMonitor().monitorUuid.equals(app.getCurrentUser().getUuid())
                        && e.currentStatus().status != StatusConstant.ARCHIVED.status
                        && e.currentStatus().status != StatusConstant.DELETED.status);

        encountersAdapter.syncData(data.collect(Collectors.toList()));

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        progress = view.findViewById(R.id.progress);
        navBar = view.findViewById(R.id.bottom_navigation);
        navBar.setOnNavigationItemSelectedListener(this);
        navBar.setVisibility(View.VISIBLE);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        encountersAdapter = new UserEncountersAdapter(getActivity());
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);

        model.getUserEncounters().observe(this, running);
        recyclerview.setAdapter(encountersAdapter);
        encountersAdapter.setItemClickListener((itemPosition, v) -> new Task().execute(encountersAdapter.getItem(itemPosition)));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);

    }

    @Override
    public void onResume() {
        super.onResume();
        model.archive();
    }

    @Override
    protected Class<?> getEntity(){
        return  EncounterInfo.class;
    }


    @Override
    public String getTitle() {
        return getString(R.string.encounters);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getActivity().setTitle(item.getTitle());
        archived = item.getItemId() == R.id.archive;
        item.setChecked(true);
        model.getUserEncounters().removeObserver(running);
        model.getUserEncounters().removeObserver(archive);

        if(archived){
            model.getUserEncounters().observe(this, archive);
            app.service().getPageArchivedEncounters(page, ()->{
                progress.setVisibility(View.GONE);
            });

        }else{
            model.getUserEncounters().observe(this, running);
        }
        return false;
    }


    class Task extends AsyncTask<EncounterHeaderInfo, PatientInfo, EncounterInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected EncounterInfo doInBackground(EncounterHeaderInfo... encounters) {
            EncounterHeaderInfo ehi = encounters[0];
            PatientInfo patient = app.getDb().patientDAO().getPatient(ehi.getPatientUuid());
            if(patient == null){
               app.service().requestSync(PatientInfo.class, ()->{
                   new Task().execute(ehi);
               });
            }else {
                publishProgress(patient);
                return app.getDb().encounterDAO().getEncounter(ehi.getUuid());
            }
            return null;


        }

        @Override
        protected void onProgressUpdate(PatientInfo... values) {
            app.setCurrentPatient(values[0]);
        }

        @Override
        protected void onPostExecute(EncounterInfo v) {

            if(v == null){
                return;
            }
            progress.setVisibility(View.GONE);

            app.setCurrentEncounter(v);
            Intent i = new Intent(getContext(), Encounter.class);
            i.putExtra("isNew",false );
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

        }
    };

}
