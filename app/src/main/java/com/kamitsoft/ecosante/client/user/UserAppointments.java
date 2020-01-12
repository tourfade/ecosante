package com.kamitsoft.ecosante.client.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.AppointmentsAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.ApptEditorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.ApptRequestorDialog;
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

public class UserAppointments extends BaseFragment {
    private RecyclerView recyclerView;
    private AppointmentsAdapter adapter;
    private AppointmentsViewModel model;

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
        model = ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        model.getUserData().observe(this, appointmentInfos ->  {
            adapter.syncData(appointmentInfos.stream()
                    .filter(a->!a.isDeleted())
                    .collect(Collectors.toList()));
        });

        recyclerView =  view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        adapter = new AppointmentsAdapter(getActivity(), connectedUser.getUserType());
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this::handleItem);

        (view.findViewById(R.id.newItem)).setOnClickListener(v -> {
            //new AppointementEditor(true, null).show(getFragmentManager(),"docdialog");

        });

    }

    @Override
    protected Class<?> getEntity(){
        return  AppointmentInfo.class;
    }


    @Override
    public String getTitle() {
        return getString(R.string.appointment_list);
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

}
