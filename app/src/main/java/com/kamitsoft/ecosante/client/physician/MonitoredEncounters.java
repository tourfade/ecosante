package com.kamitsoft.ecosante.client.physician;

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
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
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

public class MonitoredEncounters extends BaseFragment {
    private RecyclerView recyclerview;
    private UserEncountersAdapter encountersAdapter;
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
        return inflater.inflate(R.layout.list, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        encountersAdapter = new UserEncountersAdapter(getActivity());
        recyclerview.setAdapter(encountersAdapter);
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);
        model.getUserEncounters().observe(this, encounters-> {
            encountersAdapter.syncData(encounters);
        });

        encountersAdapter.setItemClickListener((itemPosition, v) -> new Task().execute(encountersAdapter.getItem(itemPosition)));


    }


    @Override
    public  Class<?> getEntity(){
        return  EncounterInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.reviewed_encounters);
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
