package com.kamitsoft.ecosante.client.physician;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MonitoredEncounters extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerview;
    private UserEncountersAdapter encountersAdapter;
    private EncountersViewModel model;
    private BottomNavigationView navBar;
    private StatusConstant currentStatus = StatusConstant.PENDING;
    private int page = 0;
    private ProgressBar progress;


    private Observer<? super List<EncounterHeaderInfo>> observer  = encounters-> {
        if(encounters ==null || encounters.size()<= 0){
            return;
        }
        Stream<EncounterHeaderInfo> data = encounters .stream()
                .filter( e -> {

                    if(currentStatus == StatusConstant.FILTER_TREATED){
                        return  e.getSupervisor() != null
                                && app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid)
                         &&(e.currentStatus().status == StatusConstant.ACCEPTED.status
                                || e.currentStatus().status == StatusConstant.REVIEWED.status
                                || e.currentStatus().status == StatusConstant.NEW.status);
                    }

                    if(currentStatus == StatusConstant.PENDING) {
                        return e.currentStatus().status == StatusConstant.PENDING.status
                                && app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid);
                    }

                    if(currentStatus == StatusConstant.ARCHIVED) {
                        return e.getSupervisor() != null
                                && app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid)
                                && e.currentStatus().status == StatusConstant.ARCHIVED.status;
                    }

                    return  e.getSupervisor() == null || e.getSupervisor().physicianUuid == null;


                });

        List<EncounterHeaderInfo> list = data.collect(Collectors.toList());
        encountersAdapter.syncData(list);
        if(currentStatus == StatusConstant.ARCHIVED){
            page = (list.size()/25);
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.supervised_list, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        progress = view.findViewById(R.id.progress);

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        navBar = view.findViewById(R.id.bottom_navigation);
        navBar.setOnNavigationItemSelectedListener(this);
        navBar.setVisibility(View.VISIBLE);
        encountersAdapter = new UserEncountersAdapter(getActivity());
        recyclerview.setAdapter(encountersAdapter);
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);
        model.getUserEncounters().observe(this, encounters-> {
            encountersAdapter.syncData(encounters);
        });
        model.getCount().observe(this,items ->{
            int[] counter = new int[]{0,0,0,0};

            items.stream().forEach(e -> {
                int cs = e.currentStatus().status;

                if( e.getSupervisor() != null
                        && app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid)
                        && cs == StatusConstant.PENDING.status){
                    counter[0]++;
                }

                if(  e.getSupervisor() != null
                        && app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid)
                        && (
                        cs == StatusConstant.ACCEPTED.status
                        || cs == StatusConstant.REVIEWED.status
                        || cs == StatusConstant.NEW.status)){
                    counter[1]++;
                }

                if( app.getCurrentUser().getUuid().equals(e.getSupervisor().physicianUuid) &&
                        cs == StatusConstant.ARCHIVED.status){
                    counter[2]++;
                }


                if(e.getSupervisor() == null || e.getSupervisor().physicianUuid == null){
                    counter[3]++;
                }

            });
            navBar.getOrCreateBadge(R.id.pending).setNumber(counter[0]);
            navBar.getOrCreateBadge(R.id.treated).setNumber(counter[1]);
            navBar.getOrCreateBadge(R.id.archive).setNumber(counter[2]);
            navBar.getOrCreateBadge(R.id.unassigned).setNumber(counter[3]);
        });

        encountersAdapter.setItemClickListener((itemPosition, v) -> new Task().execute(encountersAdapter.getItem(itemPosition)));
        navBar.setSelectedItemId(R.id.pending);

    }


    @Override
    public  Class<?> getEntity(){
        return  EncounterInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.reviewed_encounters);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getActivity().setTitle(item.getTitle());
        item.setChecked(true);
        model.getUserEncounters().removeObserver(observer);

        switch (item.getItemId()){
            case R.id.pending:
                currentStatus = StatusConstant.PENDING;
                break;
            case R.id.treated:
                currentStatus = StatusConstant.FILTER_TREATED;
                break;
            case R.id.archive:
                currentStatus = StatusConstant.ARCHIVED;
                app.service().getPageArchivedEncounters(page, ()->{
                    progress.setVisibility(View.GONE);
                });
                break;
            case R.id.unassigned:
                currentStatus = StatusConstant.FILTER_UNASSIGNED;
                break;


        }
        model.getUserEncounters().observe(this, observer);

        return false;
    }


    class Task extends AsyncTask<EncounterHeaderInfo, PatientInfo, EncounterInfo> {

        @Override
        protected EncounterInfo doInBackground(EncounterHeaderInfo... encounters) {
            EncounterHeaderInfo ehi = encounters[0];
            publishProgress(app.getDb().patientDAO().getPatient(ehi.getPatientUuid()));

            return app.getDb().encounterDAO().getEncounter(ehi.getUuid());

        }

        @Override
        protected void onProgressUpdate(PatientInfo... values) {
            app.setCurrentPatient(values[0]);
        }

        @Override
        protected void onPostExecute(EncounterInfo v) {
            app.setCurrentEncounter(v);
            Intent i = new Intent(getContext(), Encounter.class);
            i.putExtra("isNew",false );
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

        }
    };



}
