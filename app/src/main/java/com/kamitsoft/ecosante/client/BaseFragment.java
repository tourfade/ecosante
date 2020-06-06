package com.kamitsoft.ecosante.client;

import android.content.Context;
import android.os.Bundle;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;
import com.kamitsoft.ecosante.model.viewmodels.EntitiesViewModel;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BaseFragment extends Fragment {
    protected EcoSanteActivity contextActivity;
    protected EcoSanteApp app;
    protected boolean edit;
    protected UserInfo connectedUser;
    private   EntitiesViewModel entityModel;
    protected UsersViewModel model;
    protected SwipeRefreshLayout swr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (EcoSanteApp)getActivity().getApplication();
        connectedUser  = app.getCurrentUser();
        entityModel = ViewModelProviders.of(this).get(EntitiesViewModel.class);
        if(getEntity() != null)
        entityModel.getDirtyEntities().observe(this, entitySyncs -> {
            for(EntitySync e:entitySyncs){

                if(e.getEntity().equalsIgnoreCase(getEntity().getSimpleName()) && e.isDirty()){
                    app.service().requestSync(getEntity(),null);
                }
            }
        });

        model = ViewModelProviders.of(this).get(UsersViewModel.class);
        model.getLiveConnectedUser().observe(this, u-> connectedUser  = u);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context !=null && context instanceof EcoSanteActivity) {
            contextActivity = (EcoSanteActivity) getActivity();
            contextActivity.setTitle(getTitle());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contextActivity = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        contextActivity.setTitle(getTitle());
    }

    protected   String getTitle(){
        return "";
    }
    protected   String getSubtitleTitle(){
        return null;
    }

    protected int getNavLevel(){
        return  0;
    }

    protected final void requestSync() {
        app.service().requestSync(getEntity(),() -> {
            if(swr != null)
                swr.post(()->swr.setRefreshing(false));
        });
    }
    protected Class<?> getEntity(){
        return getClass();
    }
}
