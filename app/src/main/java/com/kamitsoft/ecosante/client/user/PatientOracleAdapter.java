package com.kamitsoft.ecosante.client.user;


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
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class PatientOracleAdapter extends ArrayAdapter {

    private final PatientDAO localDatabaseRepo;
    private List<PatientInfo> dataList;
    private Context mContext;


    private PatientOracleAdapter.ListFilter listFilter = new PatientOracleAdapter.ListFilter();

    public PatientOracleAdapter(Context context) {
        super(context, 0);
        dataList = new ArrayList<>();
        localDatabaseRepo = KsoftDatabase.getInstance(context).patientDAO();
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public PatientInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.patient_item_view, parent, false);
        }
        TextView fname = view.findViewById(R.id.firstname);
        TextView lname = view.findViewById(R.id.lastname);
        TextView pob = view.findViewById(R.id.pob);
        TextView dob = view.findViewById(R.id.dob);
        TextView sex = view.findViewById(R.id.sex);
        TextView mobile = view.findViewById(R.id.mobile);
        TextView status = view.findViewById(R.id.status);
        TextView occupation = view.findViewById(R.id.occupation);
        ImageView avatar = view.findViewById(R.id.patientPicture);
        PatientInfo current = getItem(position);
        fname.setText(current.getFirstName() + " "+ Utils.niceFormat(current.getMiddleName()));
        lname.setText(current.getLastName());
        dob.setText(Utils.format(current.getDob()));
        pob.setText(current.getPob());
        sex.setText(Gender.sex(current.getSex()).title);
        mobile.setText(current.getMobile());
        status.setText(MaritalStatus.status(current.getMaritalStatus()).title);
        occupation.setText(current.getOccupation());
        Utils.load(getContext(),current.getAvatar(),avatar,R.drawable.user_avatar,R.drawable.physist);

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
                    results.values = new ArrayList<PatientInfo>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                //Call to database to get matching records using room
                List<PatientInfo> matchValues = localDatabaseRepo.findPatients(searchStrLowerCase.trim()+"%");

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<PatientInfo>)results.values;
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