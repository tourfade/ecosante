package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.database.SpecialityDAO;
import com.kamitsoft.ecosante.model.Speciality;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class SpecialityOracleAdapter extends ArrayAdapter {

    private final SpecialityDAO localDatabaseRepo;
    private List<Speciality> dataList;
    private Context mContext;


    private SpecialityOracleAdapter.ListFilter listFilter = new SpecialityOracleAdapter.ListFilter();

    public SpecialityOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).specialityDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Speciality getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.speciality_oracle_item, parent, false);
        }
        TextView specialityid = view.findViewById(R.id.specialityid);
        TextView name = view.findViewById(R.id.name);
        Speciality d = getItem(position);
        specialityid.setText(""+d.getFieldvalue());
        name.setText(Utils.niceFormat(d.getName()));

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                //Call to database to get matching records using room
                List<Speciality> matchValues = localDatabaseRepo.finSpecialities(searchStrLowerCase+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Speciality>)results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}