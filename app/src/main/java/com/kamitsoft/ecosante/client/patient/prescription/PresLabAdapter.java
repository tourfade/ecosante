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
import com.kamitsoft.ecosante.constant.LabType;
import com.kamitsoft.ecosante.model.LabInfo;
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

public class PresLabAdapter extends RecyclerView.Adapter<PresLabAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final NumberFormat decimalFormatf;
   // private final EcoSanteApp app;

    private Context context;
    private List<LabInfo> mdata;

    // create constructor to innitilize context and data sent from MainActivity
    public PresLabAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        decimalFormatf = DecimalFormat.getNumberInstance();

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
            v = LayoutInflater.from(this.context).inflate(R.layout.pres_lab_item, parent, false);
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
           LabInfo current = mdata.get(position);
           myHolder.lab.setText(Utils.niceFormat(current.getLabName()));
           myHolder.category.setText(LabType.ofType(current.getType()).title);
           myHolder.notes.setText(Utils.niceFormat(current.getNotes()));
       }


    }

    public @Nullable LabInfo getItem(int position){
        return  mdata !=null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(LabInfo data) {
        int idx = mdata.indexOf(data);
        if (idx >= 0) {
            mdata.set(idx, data);
            notifyItemChanged(idx);
        } else {
            mdata.add(0,data);
            notifyItemInserted(0);
        }

    }
    public void syncData(List<LabInfo> data) {
        mdata = data;
        notifyDataSetChanged();
    }



    public List<LabInfo> getData() {
        return mdata;
    }


    public static class MyHolder extends RecyclerView.ViewHolder  {



        TextView lab;
        TextView category;
        TextView notes;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            lab = itemView.findViewById(R.id.lab);
            category = itemView.findViewById(R.id.category);
            notes = itemView.findViewById(R.id.notes);

        }


    }





}