package com.kamitsoft.ecosante.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.UserEncountersAdapter;
import com.kamitsoft.ecosante.client.adapters.UsersAdapter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Nurses extends BaseFragment {
    private RecyclerView recyclerview;
    private UsersAdapter nursesAdapter;
    private View add;

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

        nursesAdapter = new UsersAdapter(getActivity());
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        recyclerview.setAdapter(nursesAdapter);
        connectedUser = app.getCurrentUser();



        model.getUsers().observe(this, userInfos -> {
            Log.i("XXXXXXX", "-->"+userInfos);
            connectedUser = app.getCurrentUser();
            if(connectedUser == null){
                return;
            }
           userInfos = userInfos
                        .stream()
                        .filter( n-> n.getUserType() == UserType.NURSE.type)
                        .collect(Collectors.toList());

            if(UserType.isPhysist(connectedUser.getUserType())){
                userInfos = userInfos
                        .stream()
                        .filter( n-> n.getDistrictUuid()!=null && n.getDistrictUuid().equals(connectedUser.getDistrictUuid()))
                        .collect(Collectors.toList());
            }
            nursesAdapter.syncData(userInfos);
        });

        nursesAdapter.setItemClickListener((itemPosition, v) -> {
            if(connectedUser == null){
                return;
            }
            if(!UserType.isAdmin(connectedUser.getUserType())){
                Toast.makeText(contextActivity,"Vous ne pouvez pas changer ces iformations",Toast.LENGTH_LONG).show();
                return;
            }
            app.setEditingUser(nursesAdapter.getItem(itemPosition));
            contextActivity.showFragment(UserEditor.class,R.anim.slide_in_left,R.anim.exit_to_left);

        });
        add = view.findViewById(R.id.newItem);
        add.setOnClickListener(v -> {
            UserInfo ui = new UserInfo();
            ui.setNeedUpdate(true);
            ui.setUserType(UserType.NURSE.type);
            app.setEditingUser(ui);
            contextActivity.showFragment(UserEditor.class,R.anim.slide_up,R.anim.fade_out);

        });

        if(connectedUser !=null) {
            add.setVisibility(UserType.isAdmin(connectedUser.getUserType()) ? View.VISIBLE : View.GONE);
        }

    }



    @Override
    protected Class<?> getEntity(){
        return  UserInfo.class;
    }


    @Override
    public String getTitle() {
        return getString(R.string.nurses);
    }



}
