package com.kamitsoft.ecosante.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.UsersAdapter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Physicians extends BaseFragment {
    private RecyclerView recyclerview;
    private UsersAdapter physicianAdapter;

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
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        physicianAdapter = new UsersAdapter(getActivity());
        recyclerview.setAdapter(physicianAdapter);
        model.getUsers().observe(this, userInfos ->{
            userInfos = userInfos
                    .stream()
                    .filter( n-> n.getUserType() == UserType.PHYSIST.type)
                    .collect(Collectors.toList());
            physicianAdapter.syncData(userInfos);
        } );

        physicianAdapter.setItemClickListener((itemPosition, v) -> {
            app.setEditingUser(physicianAdapter.getItem(itemPosition));
            contextActivity.showFragment(UserEditor.class,R.anim.slide_in_left,R.anim.exit_to_left);
        });

        (view.findViewById(R.id.newItem)).setOnClickListener(v -> {
            UserInfo ui = new UserInfo();
            ui.setUserType(UserType.PHYSIST.type);
            app.setEditingUser(ui);
            contextActivity.showFragment(UserEditor.class,R.anim.slide_up,R.anim.fade_out);


        });

    }


    @Override
    protected Class<?> getEntity(){
        return  UserInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.physiscians);
    }








}
