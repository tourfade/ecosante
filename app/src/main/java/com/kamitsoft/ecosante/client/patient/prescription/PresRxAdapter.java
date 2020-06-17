package com.kamitsoft.ecosante.client.patient.prescription;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.adapters.ItemClickListener;
import com.kamitsoft.ecosante.model.MedicationInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hassa on 01/06/2017.
 */

public class PresRxAdapter extends RecyclerView.Adapter<PresRxAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private Context context;
    private List<MedicationInfo> mdata;

    // create constructor to innitilize context and data sent from MainActivity
    public PresRxAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        if (mdata == null || mdata.size() == 0){
            return 1;
        }else {
            return mdata.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mdata == null || mdata.size() == 0){
            return VIEW_TYPE_EMPTY;
        }else {
            return VIEW_TYPE_NORMAL;
        }
    }



    // Inflate the layout when viewholder created
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        MyHolder vh;

        if (viewType == VIEW_TYPE_NORMAL){
            v = LayoutInflater.from(this.context).inflate(R.layout.pres_med_item, parent, false);
            vh = new MyHolder(v);
        }else {
            v = LayoutInflater.from(this.context).inflate(R.layout.layout_empty, parent, false);
            vh = new MyHolder(v);
        }

        return vh;
    }

    // Bind data
    @Override
    public void onBindViewHolder(MyHolder myHolder, int position) {

       if (getItemViewType(position) == VIEW_TYPE_NORMAL){

            MedicationInfo current = mdata.get(position);
            myHolder.drugName.setText(current.getDrugName() );
            myHolder.drugRenew.setText(String.valueOf(current.getRenewal()));
            myHolder.startingDate.setText(Utils.format(current.getStartingDate()));
            myHolder.directions.setText(current.getDirection());



       }


    }

    public @Nullable MedicationInfo getItem(int position){
        return  mdata !=null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(MedicationInfo data) {
        int idx = mdata.indexOf(data);
        if (idx >= 0) {
            mdata.set(idx, data);
            notifyItemChanged(idx);
        } else {
            mdata.add(0,data);
            notifyItemInserted(0);
        }

    }
    public void syncData(List<MedicationInfo> data) {
        mdata = data;
        notifyDataSetChanged();
    }





    public List<MedicationInfo> getData() {
        return mdata;
    }


    public static class MyHolder extends RecyclerView.ViewHolder  {


        View sep;
        TextView drugName;
        TextView drugRenew;
        TextView startingDate,  directions;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             sep = itemView.findViewById(R.id.sep);
             drugName = itemView.findViewById(R.id.drug);
             drugRenew = itemView.findViewById(R.id.renewal);
             startingDate = itemView.findViewById(R.id.starting_date);
             directions = itemView.findViewById(R.id.posology);

        }


    }





}