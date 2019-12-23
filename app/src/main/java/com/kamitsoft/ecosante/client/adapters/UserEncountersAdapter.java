package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fadel on 01/09/2019.
 */

public class UserEncountersAdapter extends RecyclerView.Adapter<UserEncountersAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final EcoSanteApp app;

    private DateFormat df = DateFormat.getDateInstance();
    private Context context;
    private List<EncounterHeaderInfo> mdata;

    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public UserEncountersAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        app = (EcoSanteApp)context.getApplicationContext();
    }

    // return total item from List
    @Override
    public int getItemCount() {
        if (mdata == null){
            return 1;
        }else {
            return mdata.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mdata == null){
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
            v = LayoutInflater.from(this.context).inflate(R.layout.user_encounter_item_view, parent, false);
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

            EncounterHeaderInfo current = mdata.get(position);
            myHolder.patient.setText(Utils.formatName(context, current.getFirstName()+" "+Utils.niceFormat(current.getMiddleName()),current.getLastName(),-1));
            myHolder.dobpob.setText(Utils.formatAge(current.getDob())+" né(é) à "+Utils.niceFormat(current.getPob()));
            myHolder.mobile.setText(current.getMobile());
            myHolder.date.setText(Utils.format(current.getCreatedAt()));
            if(UserType.isNurse(app.getCurrentUser().getUserType())){
                myHolder.monitorTitle.setText("Supervisé par:");
                myHolder.monitor.setText(Utils.niceFormat(current.getSupervisor().supFullName));

            }
            if(UserType.isPhysist(app.getCurrentUser().getUserType())){
                myHolder.monitorTitle.setText("Rencontre avec:");
                myHolder.monitor.setText(Utils.niceFormat(current.getMonitor().monitorFullName));

            }



            Utils.load(context,current.getAvatar(),myHolder.avatar,R.drawable.broken_mage,R.drawable.patient);


        }else {
        }
        setAnimation(myHolder.itemView,position);

    }


    public void syncData(List<EncounterHeaderInfo> data) {
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

    public void setItemClickListener(ItemClickListener listener) {
        myClickListener = listener;
    }



    public EncounterHeaderInfo getItem(int itemPosition) {
        return mdata.get(itemPosition);
    }

    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView date,patient,dobpob,mobile, monitorTitle, monitor;
        ImageView avatar;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             avatar = itemView.findViewById(R.id.patientImg);
             date = itemView.findViewById(R.id.date);
             patient = itemView.findViewById(R.id.patient);
             dobpob = itemView.findViewById(R.id.dobpob);
             mobile = itemView.findViewById(R.id.mobile);
             monitor = itemView.findViewById(R.id.monitor);
             monitorTitle = itemView.findViewById(R.id.monitorTitle);

            itemView.setOnClickListener(this);

        }

        public void clearAnimation(){
            itemView.clearAnimation();
        }
        @Override
        public void onClick(View v) {
            if(myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }





}