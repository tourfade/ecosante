package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.LabType;
import com.kamitsoft.ecosante.database.AnalysisDAO;
import com.kamitsoft.ecosante.database.DrugDAO;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.Analysis;
import com.kamitsoft.ecosante.model.Drug;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AnalysisOracleAdapter extends ArrayAdapter {

    private final AnalysisDAO localDatabaseRepo;
    private List<Analysis> dataList;
    private Context mContext;


    private AnalysisOracleAdapter.ListFilter listFilter = new AnalysisOracleAdapter.ListFilter();

    public AnalysisOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).analysisDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Analysis getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lab_oracle_item, parent, false);
        }
        TextView labCat = view.findViewById(R.id.labcat);
        TextView labName = view.findViewById(R.id.labname);
        ImageView labIcon= view.findViewById(R.id.labicon);

        Analysis d = getItem(position);
        LabType type = LabType.labNumberOf(d.getLabNumber());
        labIcon.setImageResource(type.icon);
        labCat.setText(type.title);
        labName.setText(Utils.niceFormat(d.getLabName()));
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
                final String key = prefix.toString().toLowerCase();

                //Call to database to get matching records using room
                List<Analysis> matchValues = localDatabaseRepo.finAnalysis("%"+key+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Analysis>)results.values;
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