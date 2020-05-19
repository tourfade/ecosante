package com.kamitsoft.ecosante.client.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.adapters.DistrictAdapter;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.DistrictViewModel;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DistrictList extends BaseFragment {
    private RecyclerView recyclerview;
    private DistrictAdapter adapter;
    private View add;
    private DistrictViewModel districtViewModel;
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

        adapter = new DistrictAdapter(getActivity());
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        recyclerview.setAdapter(adapter);
        districtViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);

        districtViewModel.getDistricts().observe(this, districtInfos -> {
            List<DistrictInfo> userInfos = districtInfos
                                            .stream()
                                            .filter(d -> !d.isDeleted())
                                            .collect(Collectors.toList());

            adapter.syncData(userInfos);
        });

        adapter.setItemClickListener((itemPosition, v) -> {
            if(connectedUser ==null){
                return;
            }


             });
        add = view.findViewById(R.id.newItem);

        add.setOnClickListener(v -> {


        });


    }



    @Override
    protected Class<?> getEntity(){
        return  UserInfo.class;
    }


    @Override
    public String getTitle() {
        return getString(R.string.district);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
