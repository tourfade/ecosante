package com.kamitsoft.ecosante.client.admin;


import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.adapters.UsersAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.AllergiesEditorDialog;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.DistrictViewModel;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.skydoves.colorpickerpreference.FlagMode;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DistrictEditor extends BaseFragment {

    private EditText  districtName, description,population,maxNurse,maxPhysist;
    private TextView physicianTitle, nurseTitle;
    private DistrictViewModel districtViewModel;
    private DistrictInfo editingDistrict;
    private UsersAdapter nursesAdapter, physicianAdapter;
    private RecyclerView nurses, physicians;
    private View fillColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.district_editor, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        physicianAdapter = new UsersAdapter(getActivity());
        nursesAdapter = new UsersAdapter(getActivity());
        nurses = view.findViewById(R.id.nurses);
        nurses.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));
        physicians = view.findViewById(R.id.physicians);
        physicians.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));
        physicianTitle = view.findViewById(R.id.physicianTitle);
        nurseTitle = view .findViewById(R.id.nurseTitle);
        nurses.setAdapter(nursesAdapter);
        physicians.setAdapter(physicianAdapter);
        app.getCurrentDistrict().observe(this, districtInfo -> {
            editingDistrict = districtInfo;
            edit = true;
            initValues();
            unvalidate();
        });
        model.getUsers().observe(this, docs -> {
            if(editingDistrict == null){return;}
            List<UserInfo> districtDocs = docs.stream().filter(m -> UserType.PHYSIST.type == m.getUserType() && editingDistrict.getUuid().equals(m.getDistrictUuid())).collect(Collectors.toList());
            physicianAdapter.syncData(districtDocs);
            physicianTitle.setText((districtDocs==null?0: districtDocs.size()) +" Medecins");

            List<UserInfo> districtNurses= docs.stream().filter(m -> UserType.NURSE.type == m.getUserType() && editingDistrict.getUuid().equals(m.getDistrictUuid())).collect(Collectors.toList());
            nursesAdapter.syncData(districtNurses);
            nurseTitle.setText((districtNurses==null?0: districtNurses.size()) +" Infirmiers");
        });


        districtViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);
        fillColor = view.findViewById(R.id.color);
        districtName = view.findViewById(R.id.district_name);
        description = view.findViewById(R.id.district_description);
        population = view.findViewById(R.id.population);
        maxNurse = view.findViewById(R.id.max_nurse);
        maxPhysist = view.findViewById(R.id.max_physist);

        initListeners();
        connectedUser = app.getCurrentUser();

    }

    private void unvalidate() {
        districtName.setError(null);
        description.setError(null);
        population.setError(null);
        maxNurse.setError(null);
        maxPhysist.setError(null);
    }


    @Override
    public String getTitle() {
        return getString(R.string.district);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.patient, menu);
        menu.findItem(R.id.action_edit).setVisible(!edit);
        menu.findItem(R.id.action_save).setVisible(edit);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    protected int getNavLevel() {
        return 3;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                edit(!super.edit);
                break;

            case R.id.action_save:
                if(editingDistrict != null){
                    districtViewModel.update(editingDistrict);
                }
                edit(false);
                if(contextActivity != null){
                    contextActivity.onBackPressed();
                }

                break;

        }
        getActivity().invalidateOptionsMenu();

        return true;

    }

    public void edit(boolean editable){
        super.edit = editable;
        districtName.setEnabled(edit);
        description.setEnabled(edit);
        population.setEnabled(edit);
        maxNurse.setEnabled(edit);
        maxPhysist.setEnabled(edit);
        fillColor.setEnabled(edit);
    }
    private void initListeners() {

        fillColor.setOnClickListener(v->{
            new ColorPickerDialog(fillColor,editingDistrict)
                    .show(getFragmentManager(), "ColorPickerDialog");
        });
        districtName.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(editingDistrict == null){
                    return;
                }
                if(s==null || s.toString().trim().length()<= 0){
                    districtName.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    editingDistrict.setName(Utils.stringFromEditText(districtName));
                    contextActivity.setTitle(editingDistrict.getName());
                }
            }
        });
        description.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(editingDistrict == null){
                    return;
                }
                if(s==null || s.toString().trim().length()<= 0){
                    editingDistrict.setDescription("");
                }else {
                    editingDistrict.setDescription(Utils.stringFromEditText(description));
                }
            }
        });
        population.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(editingDistrict == null){
                    return;
                }
                if(s==null || s.toString().trim().length()<= 0){
                    editingDistrict.setPopulation(0);
                }else {
                    editingDistrict.setPopulation(Utils.intFromEditText(population));
                }
            }
        });
        maxNurse.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(editingDistrict == null){
                    return;
                }
                if(s==null || s.toString().trim().length()<= 0){
                    editingDistrict.setPopulation(0);
                }else {
                    editingDistrict.setPopulation(Utils.intFromEditText(maxNurse));
                }
            }
        });
        maxPhysist.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(editingDistrict == null){
                    return;
                }
                if(s==null || s.toString().trim().length()<= 0){
                    editingDistrict.setPopulation(0);
                }else {
                    editingDistrict.setPopulation(Utils.intFromEditText(maxPhysist));
                }
            }
        });


    }
    private void initValues(){
        edit(false);
        if(editingDistrict == null){return;}

        fillColor.setBackgroundColor(editingDistrict.getArea().fillColor);
        districtName.setText(Utils.niceFormat(editingDistrict.getName()));
        description.setText(Utils.niceFormat(editingDistrict.getDescription()));
        population.setText(Utils.niceFormat(editingDistrict.getPopulation()));
        maxNurse.setText(Utils.niceFormat(editingDistrict.getMaxNurse()));
        maxPhysist.setText(Utils.niceFormat(editingDistrict.getMaxPhysist()));
        getActivity().setTitle(editingDistrict.getName());

    }





}
