package com.kamitsoft.ecosante.client;

import android.content.Context;
import android.os.Bundle;

import com.kamitsoft.ecosante.EcoSanteApp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected  EcoSanteActivity contextActivity;
    protected EcoSanteApp app;
    protected boolean edit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (EcoSanteApp)getActivity().getApplication();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context !=null && context instanceof EcoSanteActivity) {
            contextActivity = (EcoSanteActivity) getActivity();
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
        //contextActivity.setTitle(getTitle());

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


}
