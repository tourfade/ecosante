package com.kamitsoft.ecosante.client.patient.oracles;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.database.DistrictDAO;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.database.SpecialityDAO;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.Speciality;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class DistrictOracleAdapter extends ArrayAdapter {

    private final DistrictDAO localDatabaseRepo;
    private List<DistrictInfo> dataList;
    private Context mContext;


    private DistrictOracleAdapter.ListFilter listFilter = new DistrictOracleAdapter.ListFilter();

    public DistrictOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).districtDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public DistrictInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.district_oracle_item, parent, false);
        }
        View  v = view.findViewById(R.id.color);
        TextView name = view.findViewById(R.id.name);
        DistrictInfo d = getItem(position);
        name.setText(Utils.niceFormat(d.getName()));
        v.setBackgroundColor(d.getArea().fillColor);
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
                List<DistrictInfo> matchValues = localDatabaseRepo.finDistricts(searchStrLowerCase+"%");
                Log.i("XXXXX", " key "+searchStrLowerCase+" "+matchValues.size());
                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<DistrictInfo>)results.values;
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