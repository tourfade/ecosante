package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.database.ActDAO;
import com.kamitsoft.ecosante.database.AllergenDAO;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.Act;
import com.kamitsoft.ecosante.model.Allergen;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AllergenOracleAdapter extends ArrayAdapter {

    private final AllergenDAO localDatabaseRepo;
    private List<Allergen> dataList;
    private Context mContext;


    private AllergenOracleAdapter.ListFilter listFilter = new AllergenOracleAdapter.ListFilter();

    public AllergenOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).allergenDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Allergen getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.act_oracle_item, parent, false);
        }
        TextView code = view.findViewById(R.id.acttype);
        TextView actName = view.findViewById(R.id.name);
        ImageView actIcon= view.findViewById(R.id.acticon);

        Allergen d = getItem(position);
        code.setText(Utils.niceFormat(d.getCode()));
        actName.setText(Utils.niceFormat(d.getName()));
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
                List<Allergen> matchValues = localDatabaseRepo.finAll("%"+key+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Allergen>)results.values;
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