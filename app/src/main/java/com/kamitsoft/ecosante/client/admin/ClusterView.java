package com.kamitsoft.ecosante.client.admin;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.model.ClusterInfo;
import com.kamitsoft.ecosante.model.viewmodels.ClusterViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class ClusterView extends BaseFragment {

    private ImageView logo;
    private TextView name, matricule, account;
    private EditText address, email,city, country, phone1, phone2, webSite;
    private ClusterInfo clusterInfo;
    private Button save;
    private ImagePickerActivity picker;
    private ClusterViewModel clusterModel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ImagePickerActivity)
            picker  = (ImagePickerActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        clusterModel = ViewModelProviders.of(this).get(ClusterViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cluster, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logo = view.findViewById(R.id.logo);
        matricule = view.findViewById(R.id.matricule);
        account = view.findViewById(R.id.account);
        name = view.findViewById(R.id.cluster_name);

        address = view.findViewById(R.id.address);
        city = view.findViewById(R.id.city);
        country = view.findViewById(R.id.country);

        phone1 = view.findViewById(R.id.phone1);
        phone2 = view.findViewById(R.id.phone2);

        email = view.findViewById(R.id.email);

        webSite = view.findViewById(R.id.website);
        save = view.findViewById(R.id.save);
        initListeners();
        clusterModel.getClusters().observe(this, clusters->{
            if(clusters !=null && clusters.size() > 0) {
                this.clusterInfo = clusters.get(0);
            }else {
                clusterInfo = new ClusterInfo();
            }
            initValues();
        });
        if(clusterInfo == null || clusterInfo.getAccountId() <= 0){
            entityModel.init(ClusterInfo.class);
        }

    }

    @Override
    protected Class<?> getEntity() {
        return ClusterInfo.class;
    }

    @Override
    public String getTitle() {
        return getString(R.string.cluster);
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
        return 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                edit(!super.edit);
                break;

            case R.id.action_save:
                saveCluster();
                break;

        }
        getActivity().invalidateOptionsMenu();

        return true;

    }

    private void saveCluster() {
        clusterInfo.setNeedUpdate(true);
        picker.syncAvatar(clusterInfo.getLogo(), null, 0);
        clusterModel.insert(clusterInfo);
        edit(false);
        if(contextActivity != null){
            //contextActivity.onBackPressed();
        }

    }

    public void edit(boolean editable){
        super.edit = editable;

        logo.setEnabled(edit);
        name.setEnabled(edit);

        address.setEnabled(edit);
        city.setEnabled(edit);
        country.setEnabled(edit);

        phone1.setEnabled(edit);
        phone2.setEnabled(edit);

        email.setEnabled(edit);

        webSite.setEnabled(edit);
        save.setEnabled(edit);

    }

    private void initListeners() {
        if(picker != null) {
            picker.setPlaceholder(R.drawable.kamit);
            picker.setSelectionFinishedListener((logo)-> clusterInfo.setLogo(logo));
            logo.setOnClickListener(picker::pick);
        }
        save.setOnClickListener(v->saveCluster());

        name.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    name.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    clusterInfo.setName(s.toString());
                }
            }
        });

        address.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                    clusterInfo.setAddress(s.toString());
            }
        });
        city.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setCity(s.toString());
            }
        });

        country.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setCountry(s.toString());
            }
        });

        phone1.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setPhone1(s.toString());
            }
        });
        phone2.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setPhone2(s.toString());
            }
        });
        email.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setEmail(s.toString());
            }
        });

        webSite.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                clusterInfo.setWebsite(s.toString());
            }
        });



    }

    private void initValues(){
        edit(false);
        Utils.loadRectangle(getContext(),
                BuildConfig.AVATAR_BUCKET,
                clusterInfo.getLogo(),
                logo,R.drawable.kamit,R.drawable.kamit );

        matricule.setText(Utils.niceFormat(clusterInfo.getMatricule()));
        account.setText(Utils.niceFormat(clusterInfo.getAccount()));
        name.setText(Utils.niceFormat(clusterInfo.getName()));

        address.setText(Utils.niceFormat(clusterInfo.getAddress()));
        city.setText(Utils.niceFormat(clusterInfo.getCity()));
        country.setText(Utils.niceFormat(clusterInfo.getCountry()));

        phone1.setText(Utils.niceFormat(clusterInfo.getPhone1()));
        phone2.setText(Utils.niceFormat(clusterInfo.getPhone2()));

        email.setText(Utils.niceFormat(clusterInfo.getEmail()));

        webSite.setText(Utils.niceFormat(clusterInfo.getWebsite()));
    }


}
