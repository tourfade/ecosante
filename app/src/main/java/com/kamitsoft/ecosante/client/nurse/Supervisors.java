package com.kamitsoft.ecosante.client.nurse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.SupervisorsAdapter;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Supervisors extends BaseFragment {
    private RecyclerView recyclerview;
    private SupervisorsAdapter supervisor;
    private UsersViewModel model;

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
        model = ViewModelProviders.of(this).get(UsersViewModel.class);
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        supervisor = new SupervisorsAdapter(getActivity());
        recyclerview.setAdapter(supervisor);
        model.getUsers().observe(this, userInfos -> {
                        userInfos = userInfos
                                .stream()
                                .filter( u->
                                    u.getUserType() == UserType.PHYSIST.type
                                        && connectedUser.getDistrictUuid()!= null
                                            && connectedUser.getDistrictUuid().equals(u.getDistrictUuid())
                                )
                    .collect(Collectors.toList());

                    supervisor.syncData(userInfos);
                });

    }


    @Override
    protected Class<?> getEntity(){
        return  UserInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.supervisors);
    }



}
