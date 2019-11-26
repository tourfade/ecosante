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
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class PhysistOracleAdapter extends ArrayAdapter {

    private final UserDAO localDatabaseRepo;
    private List<UserInfo> dataList;
    private Context mContext;


    private PhysistOracleAdapter.ListFilter listFilter = new PhysistOracleAdapter.ListFilter();

    public PhysistOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).userDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public UserInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_oracle_item, parent, false);
        }
        TextView speciality = view.findViewById(R.id.speciality);
        TextView fullName = view.findViewById(R.id.fullName);
        ImageView avatar = view.findViewById(R.id.avatar);
        UserInfo user = getItem(position);
        speciality.setText(Utils.niceFormat(user.getSpeciality()));
        fullName.setText(Utils.formatUser(getContext(), user));
        Utils.load(getContext(),user.getAvatar(),avatar,R.drawable.user_avatar,R.drawable.physist);

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
                List<UserInfo> matchValues = localDatabaseRepo.findUsers(searchStrLowerCase+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<UserInfo>)results.values;
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