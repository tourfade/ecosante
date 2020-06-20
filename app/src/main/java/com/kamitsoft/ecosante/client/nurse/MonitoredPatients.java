package com.kamitsoft.ecosante.client.nurse;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.client.adapters.WaitingPatientAdapter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class MonitoredPatients extends BaseFragment {
    private RecyclerView recyclerview;
    private WaitingPatientAdapter adapter;
    private PatientsViewModel model;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcIntent;
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
        model = ViewModelProviders.of(this).get(PatientsViewModel.class);
        model.getAllDatas().observe(this, patientInfos -> {
            patientInfos = patientInfos.stream()
                    .filter(p-> p.getMonitor() != null
                            && p.getMonitor().monitorUuid != null
                            && p.getMonitor().monitorUuid.equals(connectedUser.getUuid()))
                    .collect(Collectors.toList());
            adapter.syncData(patientInfos);
        });
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        adapter = new WaitingPatientAdapter(getActivity());
        recyclerview.setAdapter(adapter);
        adapter.setItemClickListener((itemPosition, v) -> {
            app.setCurrentPatient(adapter.getItem(itemPosition));
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
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //Check if NFC is available on device
        if (nfcAdapter == null) {
            // Device does not support NFC
      //      Toast.makeText(this,"Device does not support NFC!",Toast.LENGTH_LONG).show();
            //this.finish();
        } else {
            if (!nfcAdapter.isEnabled()) {
                // NFC is disabled
             //   Toast.makeText(this, "Enable NFC!",Toast.LENGTH_LONG).show();
            } else {
                app.newPatient();
                nfcIntent = PendingIntent.getActivity(this.contextActivity,
                        0, new Intent(getContext(),PatientActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            }
        }

    }


    @Override
    protected Class<?> getEntity(){
        return  PatientInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.line_up);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this.contextActivity, nfcIntent, null,
                null);
    }

}
