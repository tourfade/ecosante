package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.constant.VitalType;
import com.kamitsoft.ecosante.model.EncounterInfo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import top.defaults.drawabletoolbox.DrawableBuilder;

/**
 * Created by hassa on 01/06/2017.
 */

public class PatientEncountersAdapter extends AbstractAdapter<PatientEncountersAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final EcoSanteApp app;

    private Context context;
    private List<EncounterInfo> mdata;


    // create constructor to innitilize context and data sent from MainActivity
    public PatientEncountersAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        app = (EcoSanteApp)context.getApplicationContext();
        String uuid = app.getCurrentPatient().getUuid();
        app.getDb().encounterDAO()
                .getPatientEncounters(uuid)
                .observe((AppCompatActivity)context, encounters -> {
            syncData(encounters);
        });
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
            v = LayoutInflater.from(this.context).inflate(R.layout.pat_encounter_item_view, parent, false);
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

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL){

            // Get current position of item in recyclerview to bind data and assign values from list

            EncounterInfo current = mdata.get(position);

            myHolder.nurse.setText(Utils.niceFormat(current.getMonitor().monitorFullName));

            myHolder.date.setText(Utils.format(current.getCreatedAt()));
            myHolder.glycemy.setText(Utils.format(current.getGlycemy())+context.getString(VitalType.GLYCEMY.unit));
            myHolder.heartrate.setText(Utils.format(current.getHeartRate())+context.getString(VitalType.HEARTRATE.unit));

            myHolder.pressure.setText(Utils.format(current.getPressureSystolic())+"/"+
                    Utils.format(current.getPressureDiastolic())+" "+context.getString(VitalType.PRESSURE.unit));

            myHolder.status.setBackground(StatusConstant.ofStatus(current.currentStatus().status).drawable);

        }else {
        }
        setAnimation(myHolder.itemView,position);

    }

    public @Nullable EncounterInfo getItem(int position){
        return  mdata !=null && mdata.size()>0?mdata.get(position):null;
    }
    public void syncData(List<EncounterInfo> data) {
       mdata = data;
       notifyDataSetChanged();
    }


    int lastpos = -1;
    private void setAnimation(View itemView, int position) {
        if(position > lastpos){
            Animation anim = AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
            itemView.startAnimation(anim);
        }
        lastpos = position;
    }

    @Override
    public void onViewDetachedFromWindow(MyHolder holder) {
        holder.clearAnimation();
    }



    public  class MyHolder extends AbstractAdapter.EdtiableHolder {

        TextView date, nurse, pressure,glycemy, heartrate;
        View status;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             date = itemView.findViewById(R.id.date);
             nurse = itemView.findViewById(R.id.nurse);
             pressure = itemView.findViewById(R.id.pressure);
             glycemy = itemView.findViewById(R.id.glycemy);
             heartrate = itemView.findViewById(R.id.heartrate);
             status = itemView.findViewById(R.id.status);

        }

    }





}