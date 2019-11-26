package com.kamitsoft.ecosante.client.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.AppointmentsAdapter;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.AppointmentsViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class UserAppointments extends BaseFragment {
    private RecyclerView recyclerView;
    private AppointmentsAdapter adapter;
    private SwipeRefreshLayout swr;
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
            adapter.syncData(appointmentInfos);
        });

        recyclerView =  view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        adapter = new AppointmentsAdapter(getActivity(), false);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener((itemPosition, v) -> {
            //new AppointementEditor(false, adapter.getItem(itemPosition)).show(getFragmentManager(),"docdialog");



        });

        (view.findViewById(R.id.newItem)).setOnClickListener(v -> {
            //new AppointementEditor(true, null).show(getFragmentManager(),"docdialog");

        });

    }

    private void requestSync() {
        getActivity().runOnUiThread(() -> swr.setRefreshing(false));
    }

    @Override
    public String getTitle() {
        return getString(R.string.appointment_list);
    }


}
