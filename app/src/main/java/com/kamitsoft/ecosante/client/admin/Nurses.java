package com.kamitsoft.ecosante.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.UserEncountersAdapter;
import com.kamitsoft.ecosante.client.adapters.UsersAdapter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.List;

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
    private UsersViewModel model;
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
        return inflater.inflate(R.layout.list_with_add, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        model = ViewModelProviders.of(this).get(UsersViewModel.class);
        nursesAdapter = new UsersAdapter(getActivity());
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        recyclerview.setAdapter(nursesAdapter);
        model.getUsersOfType(UserType.NURSE).observe(this, userInfos -> nursesAdapter.syncData(userInfos));
        nursesAdapter.setItemClickListener((itemPosition, v) -> {
            app.setEditingUser(nursesAdapter.getItem(itemPosition));
            Intent i = new Intent(getContext(),UserProfileEditor.class);
            i.putExtra(UserProfileEditor.KIND,UserProfileEditor.EDIT_NURSE);
            startActivityForResult(i,1001);
            getActivity().overridePendingTransition(R.anim.enter_from_right ,R.anim.exit_to_left);


        });
        (view.findViewById(R.id.newItem)).setOnClickListener(v -> {
            UserInfo ui = new UserInfo();
            ui.setUserType(UserType.NURSE.type);
            app.setEditingUser(ui);
            Intent i = new Intent(getContext(), UserProfileEditor.class);
            i.putExtra(UserProfileEditor.KIND,UserProfileEditor.NEW_NURSE );
            startActivityForResult(i,1001);
            getActivity().overridePendingTransition(R.anim.slide_up,R.anim.fade_out);

        });

    }

    private void requestSync() {
        getActivity().runOnUiThread(() -> swr.setRefreshing(false));
    }

    @Override
    public String getTitle() {
        return getString(R.string.nurses);
    }







}
