package com.kamitsoft.ecosante.client.nurse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.UserEncountersAdapter;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class UserEncounters extends BaseFragment {
    private RecyclerView recyclerview;
    private UserEncountersAdapter encountersAdapter;
    private EncountersViewModel model;
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
        return inflater.inflate(R.layout.list, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        encountersAdapter = new UserEncountersAdapter(getActivity());
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);

        model.getUserEncounters().observe(this, encounters-> {

            Stream<EncounterHeaderInfo> data = encounters
                    .stream()
                    .filter(e -> e.getUserUuid().equals(app.getCurrentUser().getUuid()));
            encountersAdapter.syncData(data.collect(Collectors.toList()));
        });
        recyclerview.setAdapter(encountersAdapter);
        encountersAdapter.setItemClickListener((itemPosition, v) -> new Task().execute(encountersAdapter.getItem(itemPosition)));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
    }

    private void requestSync() {
        getActivity().runOnUiThread(() -> swr.setRefreshing(false));
    }

    @Override
    public String getTitle() {
        return getString(R.string.encounters);
    }


    class Task extends AsyncTask<EncounterHeaderInfo, Void, Intent> {

        @Override
        protected Intent doInBackground(EncounterHeaderInfo... encounters) {
            EncounterHeaderInfo ehi = encounters[0];
            app.setCurrentPatient(app.getDb().patientDAO().getPatient(ehi.getPatientUuid()));
            app.setCurrentEncounter(app.getDb().encounterDAO().getEncounter(ehi.getUuid()));
            Intent i = new Intent(getContext(), Encounter.class);
            i.putExtra("isNew",false );
            return i;
        }

        @Override
        protected void onPostExecute(Intent i) {
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        }
    };



}