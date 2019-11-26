package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.database.DrugDAO;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.Drug;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class DrugOracleAdapter extends ArrayAdapter {

    private final DrugDAO localDatabaseRepo;
    private List<Drug> dataList;
    private Context mContext;


    private DrugOracleAdapter.ListFilter listFilter = new DrugOracleAdapter.ListFilter();

    public DrugOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).drugDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Drug getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drug_oracle_item, parent, false);
        }
        TextView ref = view.findViewById(R.id.reference);
        TextView dci = view.findViewById(R.id.dci);
        Drug d = getItem(position);
        ref.setText(Utils.niceFormat(d));
        dci.setText(d.getDci());

        if(d.isEps3() && !d.isEps2()){
            ref.setTextColor(Color.RED);
        }else if(d.isEps2() && !d.isEps1()){
            ref.setTextColor(Color.parseColor("#FFA500"));
        }else {
            ref.setTextColor(Color.DKGRAY);
        }

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
                List<Drug> matchValues = localDatabaseRepo.finDrugs(searchStrLowerCase+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Drug>)results.values;
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