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
import com.kamitsoft.ecosante.model.json.Status;
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
    private StatusConstant currentStatus = StatusConstant.PENDING;
    private int page = 0;
    private int currentID = R.id.active;

    private Observer<? super List<EncounterHeaderInfo>> observer  = encounters-> {
        if(encounters ==null || encounters.size()<= 0){
            return;
        }
        Stream<EncounterHeaderInfo> data = encounters .stream()
                .filter( e -> e.getMonitor().monitorUuid.equals(app.getCurrentUser().getUuid())
                        &&   e.currentStatus().status == currentStatus.status);

        List<EncounterHeaderInfo> list = data.collect(Collectors.toList());
        encountersAdapter.syncData(list);
        if(currentStatus == StatusConstant.ARCHIVED){
            page = (list.size()/25);
        }

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
        model.getCount().observe(this,items ->{
            int[] counter = new int[]{0,0,0,0};

            items.stream().forEach(i->{
                switch (StatusConstant.ofStatus(i.currentStatus().status)){
                    case PENDING:  counter[0]++;break;
                    case REJECTED: counter[1]++;break;
                    case ACCEPTED: counter[2]++; break;
                    case ARCHIVED: counter[3]++; break;
                }
                navBar.getOrCreateBadge(R.id.active).setNumber(counter[0]);
                navBar.getOrCreateBadge(R.id.rejected).setNumber(counter[1]);
                navBar.getOrCreateBadge(R.id.accepted).setNumber(counter[2]);
                navBar.getOrCreateBadge(R.id.archive).setNumber(counter[3]);

            });
            ;
        });
        model.getUserEncounters().observe(this, observer);
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
        item.setChecked(true);
        model.getUserEncounters().removeObserver(observer);
        currentID = item.getItemId();

        switch (item.getItemId()){
            case R.id.archive:
                currentStatus = StatusConstant.ARCHIVED;
                app.service().getPageArchivedEncounters(page, ()->{
                    progress.setVisibility(View.GONE);
                });
                break;
            case R.id.rejected:
                currentStatus = StatusConstant.REJECTED;
                break;
            case R.id.active:
                currentStatus = StatusConstant.PENDING;
                break;
            case R.id.accepted:
                currentStatus = StatusConstant.ACCEPTED;
                break;
        }

        model.getUserEncounters().observe(this, observer);

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
