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
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.Act;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ActOracleAdapter extends ArrayAdapter {

    private final ActDAO localDatabaseRepo;
    private List<Act> dataList;
    private Context mContext;


    private ActOracleAdapter.ListFilter listFilter = new ActOracleAdapter.ListFilter();

    public ActOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).actDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Act getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.act_oracle_item, parent, false);
        }
        TextView actType = view.findViewById(R.id.acttype);
        TextView actName = view.findViewById(R.id.name);
        ImageView actIcon= view.findViewById(R.id.acticon);

        Act d = getItem(position);
        actType.setText(Utils.niceFormat(d.getType()));
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
                List<Act> matchValues = localDatabaseRepo.finAct("%"+key+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Act>)results.values;
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