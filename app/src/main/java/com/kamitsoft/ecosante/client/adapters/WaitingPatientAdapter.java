package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.model.PatientInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by hassa on 01/06/2017.
 */

public class WaitingPatientAdapter extends RecyclerView.Adapter<WaitingPatientAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final EcoSanteApp app;

    private Context context;
    private List<PatientInfo> mdata;

    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public WaitingPatientAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        app = (EcoSanteApp)context.getApplicationContext();
        /*app.getDb().patientDAO().getPatients().observe((AppCompatActivity)context, patientInfos -> {
            syncData(patientInfos);
        });*/
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
            v = LayoutInflater.from(this.context).inflate(R.layout.patient_item_view, parent, false);
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

            PatientInfo current = mdata.get(position);

            myHolder.fname.setText(current.getFirstName() + " "+ Utils.niceFormat(current.getMiddleName()));
            myHolder.lname.setText(current.getLastName());
            myHolder.dob.setText(current.getDob()==null?"N/A":Utils.format(current.getDob()));
            myHolder.pob.setText(current.getPob());
            myHolder.sex.setText(Gender.sex(current.getSex()).title);
            myHolder.mobile.setText(current.getMobile());
            myHolder.status.setText(MaritalStatus.status(current.getMaritalStatus()).title);
            myHolder.occupation.setText(current.getOccupation());
            Utils.load(context,current.getAvatar(),myHolder.avatar,R.drawable.user_avatar,R.drawable.patient);



        }else {
        }
        setAnimation(myHolder.itemView,position);

    }

    public @Nullable PatientInfo getItem(int position){
        return  mdata !=null && mdata.size()>0?mdata.get(position):null;
    }
    public void syncData(List<PatientInfo> data) {
        mdata = data;
        notifyDataSetChanged();
    }


    int lastpos = -1;
    private void setAnimation(View itemView, int position) {
        if(position > lastpos){

            Animation anim = AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left);
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



    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fname;
        TextView lname;
        TextView pob;
        TextView dob;
        TextView sex;
        TextView mobile;
        TextView status;
        TextView occupation;
        ImageView avatar;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             fname = itemView.findViewById(R.id.firstname);
             lname = itemView.findViewById(R.id.lastname);
             pob = itemView.findViewById(R.id.pob);
             dob = itemView.findViewById(R.id.dob);
             sex = itemView.findViewById(R.id.sex);
             mobile = itemView.findViewById(R.id.mobile);
             status = itemView.findViewById(R.id.status);
             occupation = itemView.findViewById(R.id.occupation);
             avatar = itemView.findViewById(R.id.patientPicture);
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
